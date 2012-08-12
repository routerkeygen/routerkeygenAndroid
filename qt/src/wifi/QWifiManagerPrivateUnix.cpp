/*
 * QWifiManagerPrivateUnix.cpp
 *
 *  Created on: 8 de Ago de 2012
 *      Author: ruka
 */

#include "QWifiManagerPrivateUnix.h"
#include "QWifiManager.h"

#include <QDebug>

QWifiManagerPrivateUnix::QWifiManagerPrivateUnix() :
		wirelessDevice(NULL) {
}

QWifiManagerPrivateUnix::~QWifiManagerPrivateUnix() {
	if (wirelessDevice != NULL)
		delete wirelessDevice;
}

void QWifiManagerPrivateUnix::startScan() {
	if (wirelessDevice != NULL) {
		updateAccessPoints();
		return;
	}
	QDBusInterface networkManager(NM_DBUS_SERVICE, NM_DBUS_PATH,
			NM_DBUS_INTERFACE, QDBusConnection::systemBus());
	if (!networkManager.isValid()) {
		emit scanFinished(QWifiManager::ERROR_NO_NM);
		return;
	}
	QDBusReply<QList<QDBusObjectPath> > devices = networkManager.call(
			"GetDevices");
	if (!devices.isValid()) {
		emit scanFinished(QWifiManager::ERROR);
		return;
	}

	bool foundWifi = false;
	foreach (const QDBusObjectPath& connection, devices.value()) {
		qDebug() << connection.path();
		QDBusInterface device(NM_DBUS_SERVICE, connection.path(),
				NM_DBUS_INTERFACE_DEVICE, QDBusConnection::systemBus());
		if (!device.isValid()) {
			emit scanFinished(QWifiManager::ERROR);
			return;
		}

		QVariant deviceType = device.property("DeviceType");
		if (!deviceType.isValid()) {
			emit scanFinished(QWifiManager::ERROR);
			return;
		}
		qDebug() << deviceType.toUInt();
		if (deviceType.toUInt() == NM_DEVICE_TYPE_WIFI) {
			foundWifi = true;
			QVariant deviceState = device.property("State");
			if (!deviceState.isValid()) {
				emit scanFinished(QWifiManager::ERROR);
				return;
			}
			if (deviceState.toUInt() <= NM_DEVICE_STATE_UNAVAILABLE)
				continue; // we are only interested in enabled wifi devices
			wirelessDevice = new QDBusInterface(NM_DBUS_SERVICE,
					connection.path(), NM_DBUS_INTERFACE_DEVICE_WIRELESS,
					QDBusConnection::systemBus());
			if (!wirelessDevice->isValid()) {
				emit scanFinished(QWifiManager::ERROR);
				return;
			}
			QDBusConnection::systemBus().connect(NM_DBUS_SERVICE,
					connection.path(), NM_DBUS_INTERFACE_DEVICE_WIRELESS,
					"AccessPointAdded", this, SLOT(updateAccessPoints()));

			QDBusConnection::systemBus().connect(NM_DBUS_SERVICE,
					connection.path(), NM_DBUS_INTERFACE_DEVICE_WIRELESS,
					"AccessPointRemoved", this, SLOT(updateAccessPoints()));

			QDBusConnection::systemBus().connect(NM_DBUS_SERVICE,
					connection.path(), NM_DBUS_INTERFACE_DEVICE_WIRELESS,
					"PropertiesChanged", this, SLOT(updateAccessPoints()));

			QDBusReply<QList<QDBusObjectPath> > accessPoints =
					wirelessDevice->call("GetAccessPoints");
			if (!accessPoints.isValid()) {
				emit scanFinished(QWifiManager::ERROR);
				return;
			}
			clearPreviousScanResults();
			foreach (const QDBusObjectPath& connection, accessPoints.value()) {
				qDebug() << connection.path();
				QDBusInterface accessPoint(NM_DBUS_SERVICE, connection.path(),
						NM_DBUS_INTERFACE_ACCESS_POINT,
						QDBusConnection::systemBus());
				QVariant mac = accessPoint.property("HwAddress");
				QVariant ssid = accessPoint.property("Ssid");
				QVariant frequency = accessPoint.property("Frequency");
				QVariant strengh = accessPoint.property("Strength");
				QVariant capabilitiesWPA = accessPoint.property("WpaFlags");
				QVariant capabilitiesRSN = accessPoint.property("RsnFlags");
				QVariant flags = accessPoint.property("Flags");

				if (!ssid.isValid() || !mac.isValid() || !frequency.isValid()
						|| !strengh.isValid() || !capabilitiesRSN.isValid()
						|| !capabilitiesWPA.isValid() || !flags.isValid() ) {
					emit scanFinished(QWifiManager::ERROR);
					return;
				}
				unsigned int capabilities = capabilitiesWPA.toUInt()
						| capabilitiesRSN.toUInt() | flags.toUInt();
				QString enc;
				if (capabilities
						& (NM_802_11_AP_SEC_PAIR_TKIP
								| NM_802_11_AP_SEC_PAIR_CCMP
								| NM_802_11_AP_SEC_GROUP_TKIP
								| NM_802_11_AP_SEC_GROUP_CCMP
								| NM_802_11_AP_SEC_KEY_MGMT_PSK
								| NM_802_11_AP_SEC_KEY_MGMT_802_1X))
					enc = QWifiManager::PSK;
				else if (capabilities
						& (NM_802_11_AP_SEC_PAIR_WEP40
								| NM_802_11_AP_SEC_PAIR_WEP104
								| NM_802_11_AP_SEC_GROUP_WEP40
								| NM_802_11_AP_SEC_GROUP_WEP104))
					enc = QWifiManager::WEP;
				else
					enc = QWifiManager::OPEN;
				qDebug() << ssid.toString() << "  " << mac.toString() << " "
						<< frequency.toString() << "Mhz Strength:"
						<< strengh.toUInt();
				scanResults.append(
						new QScanResult(ssid.toString(), mac.toString(), enc,
								frequency.toInt(), strengh.toInt()));
			}
			emit scanFinished(QWifiManager::SCAN_OK);
			return;
		}
	}
	if (foundWifi)
		emit scanFinished(QWifiManager::ERROR_NO_WIFI_ENABLED);
	else
		emit scanFinished(QWifiManager::ERROR_NO_WIFI);
}

void QWifiManagerPrivateUnix::updateAccessPoints() {
	if (!wirelessDevice->isValid()) {
		delete wirelessDevice;
		wirelessDevice = NULL;
		startScan();
		return;
	}

	QDBusInterface device(NM_DBUS_SERVICE, wirelessDevice->path(),
			NM_DBUS_INTERFACE_DEVICE, QDBusConnection::systemBus());
	if (!device.isValid()) {
		emit scanFinished(QWifiManager::ERROR);
		return;
	}
	QVariant deviceState = device.property("State");
	if (!deviceState.isValid()) {
		emit scanFinished(QWifiManager::ERROR);
		return;
	}
	if (deviceState.toUInt() <= NM_DEVICE_STATE_UNAVAILABLE) {
		emit scanFinished(QWifiManager::ERROR_NO_WIFI_ENABLED);
		return; // we are only interested in enabled wifi devices
	}
	QDBusReply<QList<QDBusObjectPath> > accessPoints = wirelessDevice->call(
			"GetAccessPoints");
	if (!accessPoints.isValid()) {
		emit scanFinished(QWifiManager::ERROR);
		return;
	}
	clearPreviousScanResults();
	foreach (const QDBusObjectPath& connection, accessPoints.value()) {
		qDebug() << connection.path();
		QDBusInterface accessPoint(NM_DBUS_SERVICE, connection.path(),
				NM_DBUS_INTERFACE_ACCESS_POINT, QDBusConnection::systemBus());
		QVariant mac = accessPoint.property("HwAddress");
		QVariant ssid = accessPoint.property("Ssid");
		QVariant frequency = accessPoint.property("Frequency");
		QVariant strengh = accessPoint.property("Strength");
		QVariant capabilitiesWPA = accessPoint.property("WpaFlags");
		QVariant capabilitiesRSN = accessPoint.property("RsnFlags");
		QVariant flags = accessPoint.property("Flags");

		if (!ssid.isValid() || !mac.isValid() || !frequency.isValid()
				|| !strengh.isValid() || !capabilitiesRSN.isValid()
				|| !capabilitiesWPA.isValid() || !flags.isValid()) {
			emit scanFinished(QWifiManager::ERROR);
			return;
		}
		unsigned int capabilities = capabilitiesWPA.toUInt()
				| capabilitiesRSN.toUInt() | flags.toUInt();
		QString enc;
		if (capabilities
				& (NM_802_11_AP_SEC_PAIR_TKIP | NM_802_11_AP_SEC_PAIR_CCMP
						| NM_802_11_AP_SEC_GROUP_TKIP
						| NM_802_11_AP_SEC_GROUP_CCMP
						| NM_802_11_AP_SEC_KEY_MGMT_PSK
						| NM_802_11_AP_SEC_KEY_MGMT_802_1X))
			enc = QWifiManager::PSK;
		else if (capabilities
				& (NM_802_11_AP_SEC_PAIR_WEP40 | NM_802_11_AP_SEC_PAIR_WEP104
						| NM_802_11_AP_SEC_GROUP_WEP40
						| NM_802_11_AP_SEC_GROUP_WEP104))
			enc = QWifiManager::WEP;
		else
			enc = QWifiManager::OPEN;
		qDebug() << ssid.toString() << "  " << mac.toString() << " "
				<< frequency.toString() << "Mhz Strength:" << strengh.toUInt();
		scanResults.append(
				new QScanResult(ssid.toString(), mac.toString(), enc,
						frequency.toInt(), strengh.toInt()));
	}
	emit scanFinished(QWifiManager::SCAN_OK);
}

