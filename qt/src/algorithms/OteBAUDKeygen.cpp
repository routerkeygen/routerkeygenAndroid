/*
 * OteBAUDKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "OteBAUDKeygen.h"

OteBAUDKeygen::OteBAUDKeygen(QString & ssid, QString & mac, int level,
		QString enc) :
		Keygen(ssid, mac, level, enc) {
}


QVector<QString> & OteBAUDKeygen::getKeys() {
    if (getMacAddress().length() != 12) {
        throw ERROR;
    }
    results.append("0"+getMacAddress().toLower());
	return results;
}



