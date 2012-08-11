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
#include <QWidgetAction>
#include "QWifiManager.h"
#include <QDesktopServices>
#include <stdlib.h>

RouterKeygen::RouterKeygen(QWidget *parent) :
		QMainWindow(parent), ui(new Ui::RouterKeygen), loading(NULL), loadingText(
				NULL) {
	ui->setupUi(this);
	connect(ui->calculateButton, SIGNAL( clicked() ), this,
			SLOT( manualCalculation() ));
	connect(ui->refreshScan, SIGNAL( clicked() ), this,
			SLOT( refreshNetworks() ));
	connect(ui->networkslist, SIGNAL( cellClicked(int,int) ), this,
			SLOT( tableRowSelected(int,int) ));
#ifdef Q_OS_UNIX
	connect(ui->forceRefresh, SIGNAL( stateChanged(int) ), this,
			SLOT( forceRefreshToggle(int) ));
#else
	ui->forceRefresh->setVisible(false); // it is not needed in Windows
#endif
	wifiManager = new QWifiManager();
	connect(wifiManager, SIGNAL( scanFinished(int) ), this,
			SLOT( scanFinished(int) ));
	loadingAnim = new QMovie(":/images/loading.gif");
	loadingAnim->setParent(this);
	/*Auto-Complete!*/
	QStringList wordList;
	wordList << "Thomson" << "Blink" << "SpeedTouch" << "O2Wireless"
			<< "Orange-" << "INFINITUM" << "BigPond" << "Otenet" << "Bbox-"
			<< "DMAX" << "privat" << "DLink-" << "Discus--" << "eircom"
			<< "FASTWEB-1-" << "Alice-" << "WLAN_" << "WLAN" << "JAZZTEL_"
			<< "WiFi" << "YaCom" << "SKY" << "TECOM-AH4222-" << "TECOM-AH4021-"
			<< "InfostradaWiFi-" << "TN_private_" << "CYTA" << "PBS" << "CONN"
			<< "OTE" << "Vodafone-" << "EasyBox-" << "Arcor-" << "Megared"
			<< "Optimus" << "OptimusFibra" << "MEO-";
	QCompleter *completer = new QCompleter(wordList, this);
	completer->setCaseSensitivity(Qt::CaseInsensitive);
	completer->setCompletionMode(QCompleter::PopupCompletion);
	ui->ssidInput->setCompleter(completer);
	ui->networkslist->setSelectionBehavior(QAbstractItemView::SelectRows);
	ui->networkslist->setEditTriggers(QAbstractItemView::NoEditTriggers);
	ui->passwordsList->installEventFilter(this);
	this->router = NULL;
	this->calculator = NULL;

	//Set widget ration
	ui->splitter->setStretchFactor(0, 2);
	ui->splitter->setStretchFactor(1, 1);

	// set up and show the system tray icon

	// build menu
	trayMenu = new QMenu(this);
	trayIcon = new QSystemTrayIcon(this);
	trayIcon->setIcon(windowIcon());
	trayIcon->setContextMenu(trayMenu);
	trayIcon->show();

	settings = new QSettings("Exobel", "RouterKeygen");
	bool forceRefresh = settings->value(FORCE_REFRESH, false).toBool();
	wifiManager->setForceScan(forceRefresh);
	ui->forceRefresh->setChecked(forceRefresh);
	runInBackground = settings->value(RUN_IN_BACKGROUND, true).toBool();
	runOnStartUp = settings->value(RUN_ON_START_UP, false).toBool();
	qApp->setQuitOnLastWindowClosed(!runInBackground);
	wifiManager->startScan();

}

RouterKeygen::~RouterKeygen() {
	delete ui;
	delete loadingAnim;
	delete wifiManager;
	if (router != NULL)
		delete router;
	if (calculator != NULL) {
		if (calculator->isRunning()) {
			router->stop();
			calculator->wait();
		}
		delete calculator;
	}
}
void RouterKeygen::manualCalculation() {
	if (ui->ssidInput->text().trimmed() == "")
		return;
	calc(ui->ssidInput->text().trimmed(), ui->macInput->text().toUpper());
}

void RouterKeygen::calc(QString ssid, QString mac) {
	if (calculator != NULL) {
		return; //ignore while a calculator is still running
	}
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
	setLoadingAnimation(tr("Calculating keys. This can take a while."));
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
	setLoadingAnimation(tr("Scanning the network"));
	wifiManager->startScan();
}

void RouterKeygen::scanFinished(int code) {
	cleanLoadingAnimation();
	ui->refreshScan->setEnabled(true);
	switch (code) {
	case QWifiManager::SCAN_OK: {
		ui->networkslist->clear();
		QVector<QScanResult*> networks = wifiManager->getScanResults();
		ui->networkslist->setRowCount(networks.size());
		trayMenu->clear();
		connect(trayMenu->addAction(tr("Vulnerable networks")),
				SIGNAL(triggered()), this, SLOT(show()));
		bool foundVulnerable = false;
		for (int i = 0; i < networks.size(); ++i) {
			ui->networkslist->setItem(i, 0,
					new QTableWidgetItem(networks.at(i)->ssid));
			ui->networkslist->setItem(i, 1,
					new QTableWidgetItem(networks.at(i)->bssid));
			QString level;
			level.setNum(networks.at(i)->level, 10);
			ui->networkslist->setItem(i, 2, new QTableWidgetItem(level));
			Keygen * supported = matcher.getKeygen(networks.at(i)->ssid,
					networks.at(i)->bssid, networks.at(i)->level, "");
			if (supported != NULL) {
				ui->networkslist->setItem(i, 3,
						new QTableWidgetItem(tr("Yes")));
				delete supported;
				addNetworkToTray(networks.at(i)->ssid, networks.at(i)->level);
				foundVulnerable = true;
			} else
				ui->networkslist->setItem(i, 3, new QTableWidgetItem(tr("No")));
		}
		if (!foundVulnerable) {
			trayMenu->addAction(tr("\tNone were detected"))->setEnabled(false);
		}
		trayMenu->addSeparator();
		QAction * startUp = trayMenu->addAction(tr("Run on Start up"));
		startUp->setCheckable(true);
		startUp->setChecked(runOnStartUp);
		connect(startUp, SIGNAL(toggled(bool)), this,
				SLOT(startUpRunToggle(bool)));
		QAction * backgroundRun = trayMenu->addAction(
				tr("Run in the background"));
		backgroundRun->setCheckable(true);
		backgroundRun->setChecked(runInBackground);
		connect(backgroundRun, SIGNAL(toggled(bool)), this,
				SLOT(backgroundRunToggle(bool)));
		trayMenu->addSeparator();
		QAction * exitAction = trayMenu->addAction(tr("Exit"));
		connect(exitAction, SIGNAL(triggered()), qApp, SLOT(quit()));
		QStringList headers;
		headers << "SSID" << "BSSID" << tr("Strength") << tr("Supported");
		ui->networkslist->setHorizontalHeaderLabels(headers);
		ui->networkslist->resizeColumnsToContents();
		ui->networkslist->horizontalHeader()->setStretchLastSection(true);
		ui->networkslist->sortByColumn(2); //Order by Strength
		ui->networkslist->sortByColumn(3); // and then by support
		//ui->statusBar->clearMessage();
		break;
	}
	case QWifiManager::ERROR_NO_NM:
		ui->statusBar->showMessage(tr("Network Manager was not detected"));
		break;

	case QWifiManager::ERROR_NO_WIFI:
		ui->statusBar->showMessage(tr("No Wifi device detected"));
		break;

	case QWifiManager::ERROR_NO_WIFI_ENABLED:
		ui->statusBar->showMessage(tr("The wifi card is not enabled"));
		break;
	}

}

void RouterKeygen::addNetworkToTray(const QString & ssid, int level) {
	QIcon icon;
	if (level >= 75)
		icon = QIcon::fromTheme("nm-signal-100");
	else if (level >= 50)
		icon = QIcon::fromTheme("nm-signal-75");
	else if (level >= 25)
		icon = QIcon::fromTheme("nm-signal-50");
	else
		icon = QIcon::fromTheme("nm-signal-25");
	QAction * net = trayMenu->addAction(icon, ssid);
	connect(net, SIGNAL(triggered()), this, SLOT(show()));
}

void RouterKeygen::getResults() {
	cleanLoadingAnimation();
	ui->calculateButton->setEnabled(true);
	if (calculator->hadError()) {
		ui->statusBar->showMessage(tr("Error while calculating."));
		delete calculator;
		calculator = NULL;
		return;
	}
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
	ui->passwordsLabel->setText(tr("Calculated Passwords for %1").arg(router->getSsidName()));
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
	ui->statusBar->showMessage(tr("Key copied"));
}

void RouterKeygen::forceRefreshToggle(int state) {
	wifiManager->setForceScan(state == Qt::Checked);
	settings->setValue(FORCE_REFRESH, state == Qt::Checked);
}

void RouterKeygen::backgroundRunToggle(bool state) {
	runInBackground = state;
	qApp->setQuitOnLastWindowClosed(!state);
	settings->setValue(RUN_IN_BACKGROUND, runInBackground);
}

void RouterKeygen::startUpRunToggle(bool state) {
	runOnStartUp = state;
	settings->setValue(RUN_ON_START_UP, runOnStartUp);
#ifdef Q_OS_UNIX
	QString newFile = "/home/" + QString(getenv("USER"))
			+ "/.config/autostart/routerkeygen.desktop";
	qDebug() << newFile;
	if (runOnStartUp) {
		QFile autoStart(":/routerkeygen.desktop");
		if (!autoStart.copy(newFile))
			qDebug() << "Error while copying file";
		QFile::setPermissions(newFile,
				QFile::ReadOwner | QFile::WriteOwner | QFile::ReadUser
						| QFile::WriteUser | QFile::ReadGroup | QFile::ReadGroup
						| QFile::ReadOther);
	} else {
		if (QFile::exists(newFile))
			if (!QFile::remove(newFile))
				qDebug() << "Error while removing file";
	}
#elif Q_OS_WIN
	 QSettings settings("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run",QSettings::NativeFormat);
	    if (runOnStartUp) {
	        settings.setValue("RouterKeygen", QCoreApplication::applicationFilePath().replace('/','\\'));
	    } else {
	        settings.remove("RouterKeygen");
	    }
#endif
}

void RouterKeygen::setLoadingAnimation(const QString& text) {
	loadingAnim->start();
	loading = new QLabel(ui->statusBar);
	loading->setMovie(loadingAnim);
	loadingText = new QLabel(text, ui->statusBar);
	ui->statusBar->clearMessage();
	ui->statusBar->addWidget(loading);
	ui->statusBar->addWidget(loadingText);
}
void RouterKeygen::cleanLoadingAnimation() {
	if (loading == NULL || loadingText == NULL)
		return;
	loadingAnim->stop();
	ui->statusBar->removeWidget(loading);
	ui->statusBar->removeWidget(loadingText);
	loading = NULL;
	loadingText = NULL;
}

const QString RouterKeygen::RUN_ON_START_UP = "RUN_ON_START_UP";
const QString RouterKeygen::RUN_IN_BACKGROUND = "RUN_IN_BACKGROUND";
const QString RouterKeygen::FORCE_REFRESH = "FORCE_REFRESH";
