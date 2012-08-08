/*
 * QWifiManagerPrivate.cpp
 *
 *  Created on: 8 de Ago de 2012
 *      Author: ruka
 */

#include "QWifiManagerPrivate.h"

QWifiManagerPrivate::QWifiManagerPrivate() {

}

QWifiManagerPrivate::~QWifiManagerPrivate() {
}

QVector<QScanResult*> & QWifiManagerPrivate::getScanResults() {
	return scanResults;
}

void QWifiManagerPrivate::clearPreviousScanResults() {
	foreach ( QScanResult * scanResult, scanResults )
		delete scanResult;
	scanResults.clear();
}
