/*
 * QWifiManagerPrivateMac.h
 *
 *  Created on: 8 de Ago de 2012
 *      Author: ruka
 */

#ifndef QWIFIMANAGERPRIVATEMAC_H_
#define QWIFIMANAGERPRIVATEMAC_H_

#include "QWifiManagerPrivate.h"
#include <QProcess>
#include <QTimerEvent>

class QWifiManagerPrivateMac: public QWifiManagerPrivate {
Q_OBJECT
public:
    QWifiManagerPrivateMac();
    virtual ~QWifiManagerPrivateMac();
	void startScan();

protected:
    void timerEvent(QTimerEvent *event);


private slots:
	void parseResults();

private:
	QProcess * scan;
    int timerId;
};

#endif /* QWIFIMANAGERPRIVATEMAC_H_ */
