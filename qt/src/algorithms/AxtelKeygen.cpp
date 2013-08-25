/*
 * AxtelKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "AxtelKeygen.h"

AxtelKeygen::AxtelKeygen(QString ssid, QString mac) :
		Keygen(ssid, mac) {
}



QVector<QString> & AxtelKeygen::getKeys() {
    QString mac = getMacAddress();
    if ( mac.length() != 12 )
        throw ERROR;
    results.append(mac.mid(2).toUpper());
	return results;
}
