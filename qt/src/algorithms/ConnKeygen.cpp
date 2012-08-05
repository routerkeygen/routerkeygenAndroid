/*
 * ConnKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "ConnKeygen.h"

ConnKeygen::ConnKeygen(QString & ssid, QString & mac, int level,
		QString enc) :
		Keygen(ssid, mac, level, enc) {
}



QVector<QString> & ConnKeygen::getKeys() {
	results.append(QString("1234567890123"));
	return results;
}
