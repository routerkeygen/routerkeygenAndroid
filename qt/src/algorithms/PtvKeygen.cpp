/*
 * PtvKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "PtvKeygen.h"

PtvKeygen::PtvKeygen(QString & ssid, QString & mac) :
		Keygen(ssid, mac) {
}



QVector<QString> & PtvKeygen::getKeys() {
    QString mac = getMacAddress();
    if ( mac.size() != 12 )
            throw ERROR;
    results.append(mac.toUpper());
	return results;
}

