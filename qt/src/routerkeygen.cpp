/*
 * Copyright 2012 Rui Araújo, Luís Fonseca
 *
 * This file is part of Router Keygen.
 *
 * Router Keygen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Router Keygen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Router Keygen.  If not, see <http://www.gnu.org/licenses/>.
 */
#include "routerkeygen.h"
#include "ui_routerkeygen.h"
#include <QMessageBox>
#include "WirelessMatcher.h"
#include <QCompleter>
#include <QStringList>
#include <QMovie>
#include <QListWidgetItem>
#include <QContextMenuEvent>
#include <QMouseEvent>
#include <QMenu>
#include <QDebug>
#include <QClipboard>
#include "QWifiManager.h"

RouterKeygen::RouterKeygen(QWidget *parent) :
		QMainWindow(parent), ui(new Ui::RouterKeygen) {
	ui->setupUi(this);
	connect(ui->calculateButton, SIGNAL( clicked() ), this,
			SLOT( manualCalculation() ));
	connect(ui->refreshScan, SIGNAL( clicked() ), this,
			SLOT( refreshNetworks() ));
	connect(ui->networkslist, SIGNAL( cellClicked(int,int) ), this,
			SLOT( tableRowSelected(int,int) ));
	connect(&manager, SIGNAL( scanFinished(int) ), this,
			SLOT( scanFinished(int) ));
	loadingAnim = new QMovie(":/images/loading.gif");
	manager.startScan();
	/*Auto-Complete!*/
	QStringList wordList;
	wordList << "TECOM-AH4222-" << "TECOM-AH4021-" << "Thomson" << "WLAN"
			<< "WLAN_" << "eircom" << "InfostradaWiFi-" << "SKY" << "DLink-"
			<< "WiFi" << "YaCom" << "Discus--" << "FASTWEB-1-";
	QCompleter *completer = new QCompleter(wordList, this);
	completer->setCaseSensitivity(Qt::CaseInsensitive);
	completer->setCompletionMode(QCompleter::PopupCompletion);
	ui->ssidInput->setCompleter(completer);
	ui->networkslist->setSelectionBehavior(QAbstractItemView::SelectRows);
	ui->networkslist->setEditTriggers(QAbstractItemView::NoEditTriggers);
	ui->passwordsList->installEventFilter(this);
	this->router = NULL;
	this->calculator = NULL;

}

RouterKeygen::~RouterKeygen() {
	delete ui;
	delete loadingAnim;
	if (!router)
		delete router;
	if (calculator->isRunning()) {
		router->stop();
		calculator->exit(0);
	}
	delete calculator;
}
void RouterKeygen::manualCalculation() {
	if (ui->ssidInput->text().trimmed() == "")
		return;
	calc(ui->ssidInput->text().trimmed(), ui->macInput->text().toUpper());
}

void RouterKeygen::calc(QString ssid, QString mac) {
	if (router != NULL)
		delete router;
	if (ssid == "")
		return;
	if (mac.length() < 17) {
		mac = "";
		if (mac.length() != 5)
			ui->statusBar->showMessage(tr("Invalid MAC. It will not be used."));
	}

	router = matcher.getKeygen(ssid, mac, 0, "");
	if (!router) {
		ui->statusBar->showMessage(
				tr("Unsupported network. Check the MAC address and the SSID."));
		return;
	}
	ui->passwordsList->clear();
	loadingAnim->start();
	loading = new QLabel();
	loading->setMovie(loadingAnim);
	loadingText = new QLabel(tr("Calculating keys. This can take a while."));
	ui->statusBar->clearMessage();
	ui->statusBar->addWidget(loading);
	ui->statusBar->addWidget(loadingText);
	this->calculator = new KeygenThread(router);
	connect(this->calculator, SIGNAL( finished() ), this, SLOT( getResults() ));
	ui->calculateButton->setEnabled(false);
	this->calculator->start();
}

void RouterKeygen::tableRowSelected(int row, int) {
	calc(ui->networkslist->item(row, 0)->text().trimmed(),
			ui->networkslist->item(row, 1)->text().toUpper());
}

void RouterKeygen::refreshNetworks() {
	ui->refreshScan->setEnabled(false);
	manager.startScan();
}

void RouterKeygen::scanFinished(int code) {
	ui->refreshScan->setEnabled(true);
	switch (code) {
	case QWifiManager::SCAN_OK: {
		ui->networkslist->clear();
		QVector<QScanResult*> networks = manager.getScanResults();
		ui->networkslist->setRowCount(networks.size());
		for (int i = 0; i < networks.size(); ++i) {
			ui->networkslist->setItem(i, 0,
					new QTableWidgetItem(networks.at(i)->ssid));
			ui->networkslist->setItem(i, 1,
					new QTableWidgetItem(networks.at(i)->bssid));
			QString level;
			level.setNum(networks.at(i)->level, 10);
			ui->networkslist->setItem(i, 2, new QTableWidgetItem(level));
		}
		ui->networkslist->resizeColumnsToContents();
		break;
	}
	case QWifiManager::ERROR_NO_NM:
		ui->statusBar->showMessage(tr("Network Manager was not detected"));
		break;

	case QWifiManager::ERROR_NO_WIFI:
		ui->statusBar->showMessage(tr("No Wifi device detected"));
		break;

	case QWifiManager::ERROR_NO_WIFI_ENABLED:
		ui->statusBar->showMessage(tr("The wifi card was not enabled"));
		break;
	}

}

void RouterKeygen::getResults() {
	loadingAnim->stop();
	ui->statusBar->removeWidget(loading);
	ui->statusBar->removeWidget(loadingText);
	ui->calculateButton->setEnabled(true);
	listKeys = this->calculator->getResults();
	if (listKeys.isEmpty()) {
		ui->statusBar->showMessage(tr("No keys were calculated."));
		delete calculator;
		calculator = NULL;
		return;
	}
	for (int i = 0; i < listKeys.size(); ++i)
		ui->passwordsList->insertItem(0, listKeys.at(i));
	ui->statusBar->showMessage(tr("Calculation finished"));
	delete calculator;
	calculator = NULL;
}

bool RouterKeygen::eventFilter(QObject *obj, QEvent *event) {
	// you may handle multiple objects checking "obj" parameter
	if (event->type() == QEvent::ContextMenu) {
		QMouseEvent *mouseEvent = static_cast<QMouseEvent*>(event);
		if (obj == ui->passwordsList) {
			rightButtonClicked(obj, mouseEvent->globalPos());
			return true;
		}
	}
	return QObject::eventFilter(obj, event);
}

void RouterKeygen::rightButtonClicked(QObject *obj, const QPoint &pos) {
	if (obj == ui->passwordsList) {
		if (ui->passwordsList->count() == 0)
			return;
		if (ui->passwordsList->selectedItems().size() == 0)
			return;
		QMenu *menu = new QMenu();
		QAction * copy = new QAction(tr("Copy"), this);
		copy->setShortcut(QKeySequence::Copy);
		copy->setStatusTip(tr("Copy this key"));
		connect(copy, SIGNAL(triggered()), this, SLOT(copyKey()));
		menu->addAction(copy);
		menu->exec(pos);

	}
}

void RouterKeygen::copyKey() {
	QList<QListWidgetItem*> selectedItems = ui->passwordsList->selectedItems();
	if (selectedItems.size() == 0)
		return;
	QClipboard *clipboard = QApplication::clipboard();
	clipboard->setText(selectedItems.at(0)->text(), QClipboard::Clipboard);
}

