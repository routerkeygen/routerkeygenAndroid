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
#include "tecomkeygen.h"
#include "thomsonkeygen.h"
#include "verizonkeygen.h"
#include "infostradakeygen.h"
#include "eircomkeygen.h"
#include "skyv1keygen.h"
#include "wlan2keygen.h"
#include "wlan4keygen.h"
#include "wlan6keygen.h"
#include "discuskeygen.h"
#include "dlinkkeygen.h"
#include "pirellikeygen.h"
#include "telseykeygen.h"
#include "onokeygen.h"
#include "huaweikeygen.h"
#include "alicekeygen.h"
#include <QCompleter>
#include <QStringList>

RouterKeygen::RouterKeygen(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::RouterKeygen)
{
    ui->setupUi(this);
    connect( ui->calcButton , SIGNAL( clicked() ), this , SLOT( calculateKeys() ) );
    this->setWindowIcon(QIcon(":/images/icon.png"));
    /*Auto-Complete!*/
    QStringList wordList;
    wordList << "TECOM-AH4222-" << "TECOM-AH4021-" << "Thomson" << "WLAN" << "WLAN_"
            << "eircom" << "InfostradaWiFi-" << "SKY" << "DLink-" << "WiFi" << "YaCom"
            << "Discus--" << "FASTWEB-1-";
    QCompleter *completer = new QCompleter(wordList, this);
    completer->setCaseSensitivity(Qt::CaseInsensitive);
    completer->setModelSorting(QCompleter::CaseInsensitivelySortedModel);
    ui->inputSSID->setCompleter(completer);
    this->calculator = NULL;
    this->router = NULL;

}

RouterKeygen::~RouterKeygen()
{
    delete ui;
    if ( calculator != NULL )
    {
        calculator->stop();
        delete calculator;
    }
    delete router;
}

void RouterKeygen::calculateKeys()
{//TECOM-AH4222-527A92
   ///router= new WifiNetwork(ui->inputSSID->text(), "00:1F:90:E2:7E:61");
   //router= new WifiNetwork(ui->inputSSID->text());
    if ( calculator != NULL )
    {
        if ( calculator->isRunning() )
            return;
    }
    delete router;
    router= new WifiNetwork(ui->inputSSID->text(), "00:23:8e:48:e7:d4");
    if ( !router->isSupported() )
    {
        ui->listWidget->insertItem(0,  "Not supported");
        return;
    }
    switch ( router->getType() )
    {
    case WifiNetwork::THOMSON:
                                this->calculator = new ThomsonKeygen(router , false );
                                break;
    case  WifiNetwork::EIRCOM:
                                this->calculator = new EircomKeygen(router);
                                break;
    case  WifiNetwork::VERIZON:
                                this->calculator = new VerizonKeygen(router);
                                break;
    case  WifiNetwork::TECOM:
                                this->calculator = new TecomKeygen(router);
                                break;
    case  WifiNetwork::INFOSTRADA:
                                this->calculator = new InfostradaKeygen(router);
                                break;
    case  WifiNetwork::SKY_V1:
                                this->calculator = new SkyV1Keygen(router);
                                break;
    case WifiNetwork::WLAN2:
                                this->calculator = new Wlan2Keygen(router);
                                break;
    case  WifiNetwork::WLAN4:
                                this->calculator = new Wlan4Keygen(router);
                                break;
    case  WifiNetwork::WLAN6:
                                this->calculator = new Wlan6Keygen(router);
                                break;
    case  WifiNetwork::DISCUS:
                                this->calculator = new DiscusKeygen(router);
                                break;
    case  WifiNetwork::DLINK:
                                this->calculator = new DlinkKeygen(router);
                                break;
    case  WifiNetwork::PIRELLI:
                                this->calculator = new PirelliKeygen(router);
                                break;
    case  WifiNetwork::TELSEY:
                                this->calculator = new TelseyKeygen(router);
                                break;
    case  WifiNetwork::ONO_WEP:
                                this->calculator = new OnoKeygen(router);
                                break;
    case  WifiNetwork::HUAWEI:
                                this->calculator = new HuaweiKeygen(router);
                                break;
    case  WifiNetwork::ALICE:
                                this->calculator = new AliceKeygen(router);
                                break;
    default:    this->calculator = NULL;
                break;
    }
    if ( this->calculator == NULL )
        return;
    connect( this->calculator , SIGNAL( finished() ), this , SLOT( getResults() ) );
    this->calculator->start();
}


void RouterKeygen::getResults()
{
    ui->listWidget->clear();
    listKeys = this->calculator->getResults();
    if ( listKeys.isEmpty() )
    {
        ui->listWidget->insertItem(0, "No results.");
    }
    for ( int i = 0 ; i < listKeys.size() ;++i)
        ui->listWidget->insertItem(0,listKeys.at(i) );
    delete calculator;
    calculator = NULL;

}
