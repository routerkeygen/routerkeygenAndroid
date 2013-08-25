/*
 * InterCableKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "InterCableKeygen.h"

InterCableKeygen::InterCableKeygen(QString ssid, QString mac) :
		Keygen(ssid, mac) {
}

QVector<QString> & InterCableKeygen::getKeys() {
    QString mac = getMacAddress();
    if ( mac.length() != 12 )
        throw ERROR;
    results.append(mac.mid(2).toUpper());
    QString wep = "m" + getMacAddress().left(10);
    QString hex = getMacAddress().right(2);
    int intValue = hex.toInt(NULL, 16);
    intValue += 1; // we add 1 and then convert again to hex
    hex.setNum(intValue);
    wep += hex;
    results.append(wep.toLower());
	return results;
}
