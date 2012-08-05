/*
 * QWifiManager.cpp
 *
 *  Created on: 3 de Ago de 2012
 *      Author: ruka
 */

#include "QWifiManager.h"
#include <QDebug>

#ifdef Q_OS_LINUX
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
		scan->start("pkexec", args);
		connect(scan, SIGNAL(finished(int)), this, SLOT(forcedRefreshFinished()));
	} else
		forcedRefreshFinished();
#endif

#ifdef Q_OS_WIN
	emit scanFinished(ERROR_NO_NM);
#endif
}

void QWifiManager::forcedRefreshFinished() {

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

