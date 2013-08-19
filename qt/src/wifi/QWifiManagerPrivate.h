/*
 * QWifiManagerPrivate.h
 *
 *  Created on: 8 de Ago de 2012
 *      Author: ruka
 */

#ifndef QWIFIMANAGERPRIVATE_H_
#define QWIFIMANAGERPRIVATE_H_
#include <QObject>
#include <QVector>
#include <QSharedPointer>
#include "QScanResult.h"

class QWifiManagerPrivate: public QObject {
Q_OBJECT
public:
	QWifiManagerPrivate();
	virtual ~QWifiManagerPrivate();
	virtual void startScan() = 0;
    QVector<QSharedPointer<QScanResult> > & getScanResults();

signals:
	void scanFinished(int);

protected:
    QVector<QSharedPointer<QScanResult> > scanResults;
	void clearPreviousScanResults();
};

#endif /* QWIFIMANAGERPRIVATE_H_ */
