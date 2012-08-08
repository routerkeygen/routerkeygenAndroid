/*
 * QWifiManager.cpp
 *
 *  Created on: 3 de Ago de 2012
 *      Author: ruka
 */

#include "QWifiManager.h"
#include <QDebug>


#ifdef Q_OS_UNIX
#include "QWifiManagerPrivateUnix.h"
QWifiManager::QWifiManager() :
        forceRefresh(false) , scan(NULL), impl(new QWifiManagerPrivateUnix()){

    connect(impl, SIGNAL(scanFinished(int)), this, SLOT(implScanFinished(int)));}

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
			delete scan;
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
