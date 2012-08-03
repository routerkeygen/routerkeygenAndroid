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
#include "QWifiManager.h"

RouterKeygen::RouterKeygen(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::RouterKeygen)
{
    ui->setupUi(this);
    connect( ui->refreshScan , SIGNAL( clicked() ), this , SLOT( refreshNetworks() ) );
    connect( &manager , SIGNAL( scanFinished(int) ), this , SLOT( scanFinished(int) ) );
    manager.startScan();
    this->setWindowIcon(QIcon(":/images/icon.png"));
    /*Auto-Complete!*/
#if 0
    QStringList wordList;
    wordList << "TECOM-AH4222-" << "TECOM-AH4021-" << "Thomson" << "WLAN" << "WLAN_"
            << "eircom" << "InfostradaWiFi-" << "SKY" << "DLink-" << "WiFi" << "YaCom"
          << "Discus--" << "FASTWEB-1-";
    QCompleter *completer = new QCompleter(wordList, this);
    completer->setCaseSensitivity(Qt::CaseInsensitive);
    completer->setCompletionMode(QCompleter::PopupCompletion);
    ui->inputSSID->setCompleter(completer);
#endif
    this->router = NULL;
    this->calculator = NULL;

}

RouterKeygen::~RouterKeygen()
{
    delete ui;
    if ( !router )
    	delete router;
    if ( calculator->isRunning() )
    	router->stop();
    delete calculator;
}
#if 0
void RouterKeygen::refreshNetworks()
{//TECOM-AH4222-527A92
   ///router= new WifiNetwork(ui->inputSSID->text(), "00:1F:90:E2:7E:61");
   //router= new WifiNetwork(ui->inputSSID->text());
	if ( router != NULL )
		delete router;
    router= matcher.getKeygen(ui->inputSSID->text(), "00:23:8e:48:e7:d4", 0, "");
    if ( !router  )
    {
        ui->listWidget->insertItem(0,  "Not supported");
        return;
    }
    this->calculator = new KeygenThread(router);
    connect( this->calculator , SIGNAL( finished() ), this , SLOT( getResults() ) );
    ui->calcButton->setEnabled(false);
    this->calculator->start();
}
#endif
void RouterKeygen::refreshNetworks()
{
    ui->refreshScan->setEnabled(false);
    manager.startScan();
}

void  RouterKeygen::scanFinished(int code){
	ui->refreshScan->setEnabled(true);
	if ( code == QWifiManager::SCAN_OK ){
	    ui->networkslist->clear();
    	QVector<QScanResult*> networks = manager.getScanResults();
    	ui->networkslist->setRowCount(networks.size());
    	ui->networkslist->setColumnCount(2);
        for( int i = 0; i < networks.size(); ++i){
            ui->networkslist->setItem(i, 0, new QTableWidgetItem(networks.at(i)->ssid));
            ui->networkslist->setItem(i, 1, new QTableWidgetItem(networks.at(i)->bssid));
        }
	}
	else
		ui->statusBar->showMessage("Error");

}

void RouterKeygen::getResults()
{
  /*  ui->listWidget->clear();
    ui->calcButton->setEnabled(true);
    listKeys = this->calculator->getResults();
    if ( listKeys.isEmpty() )
    {
        ui->listWidget->insertItem(0, "No results.");
    }
    for ( int i = 0 ; i < listKeys.size() ;++i)
        ui->listWidget->insertItem(0,listKeys.at(i) );
    manager.startScan();
    QVector<QScanResult*> networks = manager.getScanResults();
    foreach ( QScanResult * scanResult , networks)
    	ui->listWidget->insertItem(0, scanResult->ssid );
    delete calculator;
    calculator = NULL;
*/
}
