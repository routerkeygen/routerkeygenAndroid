/*
 * QScanResult.h
 *
 *  Created on: 3 de Ago de 2012
 *      Author: ruka
 */

#ifndef QSCANRESULT_H_
#define QSCANRESULT_H_
#include <QString>
struct QScanResult {
	QScanResult(QString ssid, QString bssid, QString capabilities, int freq,
			int level) :
			ssid(ssid), bssid(bssid), capabilities(capabilities),
			frequency(freq), level(level) {
	}
	QString ssid;
	QString bssid;
	QString capabilities;
	int frequency;
	int level;
};

#endif /* QSCANRESULT_H_ */
