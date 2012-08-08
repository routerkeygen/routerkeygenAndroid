/*
 * QWifiManagerPrivateUnix.h
 *
 *  Created on: 8 de Ago de 2012
 *      Author: ruka
 */

#ifndef QWIFIMANAGERPRIVATEUNIX_H_
#define QWIFIMANAGERPRIVATEUNIX_H_

#include "QWifiManagerPrivate.h"

#include <QtDBus/QDBusConnection>
#include <QtDBus/QDBusInterface>
#include <QtDBus/QDBusReply>
#include <NetworkManager.h>

class QWifiManagerPrivateUnix: public QWifiManagerPrivate {
Q_OBJECT
public:
	QWifiManagerPrivateUnix();
	virtual ~QWifiManagerPrivateUnix();
	void startScan();
private slots:
	void updateAccessPoints();

private:
	QDBusInterface * wirelessDevice;
};

#endif /* QWIFIMANAGERPRIVATEUNIX_H_ */
