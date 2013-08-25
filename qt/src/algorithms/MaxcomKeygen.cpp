/*
 * MaxcomKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "MaxcomKeygen.h"

MaxcomKeygen::MaxcomKeygen(QString ssid, QString mac) :
		Keygen(ssid, mac) {
}



QVector<QString> & MaxcomKeygen::getKeys() {
    QString mac = getMacAddress();
    if ( mac.size() != 12 )
            throw ERROR;
    results.append(mac.toUpper());
	return results;
}

