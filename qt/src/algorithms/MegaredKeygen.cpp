/*
 * MegaredKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "MegaredKeygen.h"

MegaredKeygen::MegaredKeygen(QString & ssid, QString & mac, int level,
		QString enc) :
		Keygen(ssid, mac, level, enc) {
}



QVector<QString> & MegaredKeygen::getKeys() {
	results.append(getMacAddress().right(10));
	return results;
}

