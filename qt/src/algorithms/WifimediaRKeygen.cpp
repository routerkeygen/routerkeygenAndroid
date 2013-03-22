/*
 * WifimediaRKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "WifimediaRKeygen.h"

WifimediaRKeygen::WifimediaRKeygen(QString & ssid, QString & mac, int level,
		QString enc) :
		Keygen(ssid, mac, level, enc) {
}

QVector<QString> & WifimediaRKeygen::getKeys() {
    QString mac = getMacAddress();
    if ( mac.size() != 12 )
            throw ERROR;
    QString possibleKey = mac.left(11).toLower() + "0";
    results.append(possibleKey);
    results.append(possibleKey.toUpper());
	return results;
}

