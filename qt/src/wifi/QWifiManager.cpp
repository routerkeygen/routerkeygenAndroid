/*
 * QWifiManager.cpp
 *
 *  Created on: 3 de Ago de 2012
 *      Author: ruka
 */

#include "QWifiManager.h"
#include <QDebug>

#ifdef Q_OS_LINUX
#include <QtDBus/QDBusConnection>
#include <QtDBus/QDBusInterface>
#include <QtDBus/QDBusReply>
#include <NetworkManager.h>
#endif

QWifiManager::QWifiManager() :
		forceRefresh(false) {
}

void QWifiManager::startScan() {
#ifdef Q_OS_LINUX
	if (forceRefresh) {
		scan = new QProcess(this);
		QStringList args;
        args << "iwlist" << "scan";
		connect(scan, SIGNAL(finished(int)), this, SLOT(forcedRefreshFinished()));
        scan->start("pkexec", args);
	} else
		forcedRefreshFinished();
#endif

#ifdef Q_OS_WIN
    scan = new QProcess(this);
    QStringList args;
    args << "wlan" << "show" << "network" <<"mode=bssid";
    connect(scan, SIGNAL(finished(int)), this, SLOT(forcedRefreshFinished()));
    scan->start("netsh", args);
#endif
}

void QWifiManager::forcedRefreshFinished() {

#ifdef Q_OS_WIN
    QString reply(scan->readAllStandardOutput());
    qDebug() << reply;
    QStringList lines = reply.split("\n");
    QString ssid,bssid,enc;
    int level;
    clearPreviousScanResults();
    for ( int i = 0 ; i < lines.size() ; ++i ){
        if ( lines.at(i).contains("BSSID") ){
            bssid = lines.at(i).mid(lines.at(i).indexOf(":")+2,17).toUpper();
            i++;
            int pos = lines.at(i).indexOf(":")+2;
            level = lines.at(i).mid(pos,lines.at(i).indexOf("%")-pos).toInt(NULL,10);
            scanResults.append(new QScanResult(ssid, bssid, enc, 0, level));
        }
        else if ( lines.at(i).contains("SSID") ){
            ssid = lines.at(i).mid(lines.at(i).indexOf(":")+2).remove("\n");
            i+=2;
            enc = lines.at(i).mid(lines.at(i).indexOf(":")+2);
        }

    }
    emit scanFinished(SCAN_OK);
#endif
#ifdef Q_OS_LINUX
	QDBusInterface networkManager(NM_DBUS_SERVICE, NM_DBUS_PATH,
			NM_DBUS_INTERFACE, QDBusConnection::systemBus());
	if (!networkManager.isValid()) {
		emit scanFinished(ERROR_NO_NM);
		return;
	}
	QDBusReply<QList<QDBusObjectPath> > devices = networkManager.call(
			"GetDevices");
	if (!devices.isValid()) {
		emit scanFinished(ERROR);
		return;
	}

	bool foundWifi = false;
	foreach (const QDBusObjectPath& connection, devices.value()) {
		qDebug() << connection.path();
		QDBusInterface device(NM_DBUS_SERVICE, connection.path(),
				NM_DBUS_INTERFACE_DEVICE, QDBusConnection::systemBus());
		if (!device.isValid()) {
			emit scanFinished(ERROR);
			return;
		}

		QVariant deviceType = device.property("DeviceType");
		if (!deviceType.isValid()) {
			emit scanFinished(ERROR);
			return;
		}
		qDebug() << deviceType.toUInt();
		if (deviceType.toUInt() == NM_DEVICE_TYPE_WIFI) {
			foundWifi = true;
			QVariant deviceState = device.property("State");
			if (!deviceState.isValid()) {
				emit scanFinished(ERROR);
				return;
			}
			if (deviceState.toUInt() <= NM_DEVICE_STATE_UNAVAILABLE)
				continue; // we are only interested in enabled wifi devices

			QDBusInterface wirelessDevice(NM_DBUS_SERVICE, connection.path(),
					NM_DBUS_INTERFACE_DEVICE_WIRELESS,
					QDBusConnection::systemBus());
			if (!wirelessDevice.isValid()) {
				emit scanFinished(ERROR);
				return;
			}
			QDBusReply<QList<QDBusObjectPath> > accessPoints =
					wirelessDevice.call("GetAccessPoints");
			if (!accessPoints.isValid()) {
				emit scanFinished(ERROR);
				return;
			}

			clearPreviousScanResults();
			foreach (const QDBusObjectPath& connection, accessPoints.value()) {
				qDebug() << connection.path();
				QDBusInterface acessPoint(NM_DBUS_SERVICE, connection.path(),
						NM_DBUS_INTERFACE_ACCESS_POINT,
						QDBusConnection::systemBus());
				QVariant mac = acessPoint.property("HwAddress");
				QVariant ssid = acessPoint.property("Ssid");
				QVariant frequency = acessPoint.property("Frequency");
				QVariant strengh = acessPoint.property("Strength");

				if (!ssid.isValid() || !mac.isValid() || !frequency.isValid()
						|| !strengh.isValid()) {
					emit scanFinished(ERROR);
					return;
				}

				qDebug() << ssid.toString() << "  " << mac.toString() << " "
						<< frequency.toString() << "Mhz Strength:"
						<< strengh.toUInt();
				scanResults.append(
						new QScanResult(ssid.toString(), mac.toString(), "",
								frequency.toInt(), strengh.toInt()));
			}
			emit scanFinished(SCAN_OK);
			return;
		}

	}
	if (foundWifi)
		emit scanFinished(ERROR_NO_WIFI_ENABLED);
	else
		emit scanFinished(ERROR_NO_WIFI);

#endif
}

void QWifiManager::setForceScan(bool refresh){
	forceRefresh = refresh;
}

QVector<QScanResult*> & QWifiManager::getScanResults() {
	return scanResults;
}

void QWifiManager::clearPreviousScanResults() {
	foreach ( QScanResult * scanResult, scanResults )
		delete scanResult;
	scanResults.clear();
}

