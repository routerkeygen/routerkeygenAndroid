/*
 * QWifiManager.cpp
 *
 *  Created on: 3 de Ago de 2012
 *      Author: ruka
 */

#include "QWifiManager.h"
#include <QDebug>


#ifdef Q_OS_UNIX
#ifdef Q_OS_MACX
#include "QWifiManagerPrivateMac.h"
QWifiManager::QWifiManager() :
        forceRefresh(false) , scan(NULL), impl(new QWifiManagerPrivateMac()){

    connect(impl, SIGNAL(scanFinished(int)), this, SLOT(implScanFinished(int)));}
#else
#include "QWifiManagerPrivateUnix.h"
QWifiManager::QWifiManager() :
        forceRefresh(false) , scan(NULL), impl(new QWifiManagerPrivateUnix()){

    connect(impl, SIGNAL(scanFinished(int)), this, SLOT(implScanFinished(int)));}
#endif
#endif

#ifdef Q_OS_WIN

#include "QWifiManagerPrivateWin.h"
QWifiManager::QWifiManager() :
        forceRefresh(false) , scan(NULL), impl(new QWifiManagerPrivateWin()){

    connect(impl, SIGNAL(scanFinished(int)), this, SLOT(implScanFinished(int)));}

#endif


QWifiManager::~QWifiManager() {
	if (scan != NULL) {
		scan->terminate(); //we are using SIGTERM here because we really want it dead!
		scan->waitForFinished();
		delete scan;
	}
}
void QWifiManager::startScan() {
#ifdef Q_OS_UNIX
	if (forceRefresh) {
		if ( scan != NULL ){
            if ( scan->state() == QProcess::NotRunning )
                delete scan;
            else{
                impl->startScan();
                return;
            }
		}
		scan = new QProcess(this);
		QStringList args;
		args << "iwlist" << "scan";
		scan->start("pkexec", args);
	}
#endif
	impl->startScan();
}

void QWifiManager::setForceScan(bool refresh) {
	forceRefresh = refresh;
}

QVector<QScanResult*> & QWifiManager::getScanResults() {
	return impl->getScanResults();
}


void QWifiManager::implScanFinished(int s){
	emit scanFinished(s);
}


const QString QWifiManager::PSK = "PSK";
const QString QWifiManager::WEP = "WEP";
const QString QWifiManager::EAP = "EAP";
const QString QWifiManager::OPEN = "Open";
