/*
 * QWifiManager.h
 *
 *  Created on: 3 de Ago de 2012
 *      Author: ruka
 */

#ifndef QWIFIMANAGER_H_
#define QWIFIMANAGER_H_
#include <QObject>
#include <QVector>
#include "QScanResult.h"
#include <QProcess>

class QWifiManagerPrivate;
class QWifiManager: public QObject {
Q_OBJECT
public:
	QWifiManager() ;
	virtual ~QWifiManager();
	void startScan();
	QVector<QScanResult*> & getScanResults();
	enum SCAN_RESULT_STATE{
		SCAN_OK = 0,
		ERROR_NO_NM,
		ERROR_NO_WIFI,
		ERROR_NO_WIFI_ENABLED,
		ERROR
	};


	// Constants used for different security types
	const static QString PSK;
	const static QString WEP;
	const static QString EAP;
	const static QString OPEN;
	void setForceScan(bool);
signals:
	void scanFinished(int);
private slots:
	void implScanFinished(int);
private:
	bool forceRefresh;
	QProcess * scan;
	QWifiManagerPrivate * impl;
};

#endif /* QWIFIMANAGER_H_ */
