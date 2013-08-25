/*
 * ConnKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "ConnKeygen.h"

ConnKeygen::ConnKeygen(QString ssid, QString mac) :
		Keygen(ssid, mac) {
}



QVector<QString> & ConnKeygen::getKeys() {
	results.append(QString("1234567890123"));
	return results;
}
