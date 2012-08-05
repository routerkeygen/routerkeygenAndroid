/*
 * OteKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "OteKeygen.h"

OteKeygen::OteKeygen(QString & ssid, QString & mac, int level,
		QString enc) :
		Keygen(ssid, mac, level, enc) {
}


QVector<QString> & OteKeygen::getKeys() {
	results.append("b075d5"+getSsidName().right(6));
	return results;
}



