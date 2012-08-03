/*
 * QWifiManager.h
 *
 *  Created on: 3 de Ago de 2012
 *      Author: ruka
 */

#ifndef QWIFIMANAGER_H_
#define QWIFIMANAGER_H_
#include <QObject>
#include <QtDBus/QDBusConnection>
#include <QtDBus/QDBusInterface>
#include <QtDBus/QDBusReply>
#include <QVector>
#include <NetworkManager.h>
#include "QScanResult.h"

class QWifiManager: public QObject {
Q_OBJECT
public:
	QWifiManager() {
	}
	virtual ~QWifiManager() {
	}
	void startScan();
	QVector<QScanResult*> & getScanResults();
	enum SCAN_RESULT_STATE{
		SCAN_OK = 0,
		ERROR_NO_NM,
		ERROR_NO_WIFI,
		ERROR_NO_WIFI_ENABLED,
		ERROR
	};
signals:
	void scanFinished(int);

private:
	void clearPreviousScanResults();
	QVector<QScanResult*> scanResults;
};

#endif /* QWIFIMANAGER_H_ */
