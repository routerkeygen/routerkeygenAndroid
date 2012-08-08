/*
 * QWifiManagerPrivateWin.h
 *
 *  Created on: 8 de Ago de 2012
 *      Author: ruka
 */

#ifndef QWIFIMANAGERPRIVATEWIN_H_
#define QWIFIMANAGERPRIVATEWIN_H_

#include "QWifiManagerPrivate.h"
#include <QProcess>
#include <QTimerEvent>

class QWifiManagerPrivateWin: public QWifiManagerPrivate {
Q_OBJECT
public:
	QWifiManagerPrivateWin();
	virtual ~QWifiManagerPrivateWin();
	void startScan();

protected:
    void timerEvent(QTimerEvent *event);


private slots:
	void parseResults();

private:
	QProcess * scan;
    int timerId;
};

#endif /* QWIFIMANAGERPRIVATEWIN_H_ */
