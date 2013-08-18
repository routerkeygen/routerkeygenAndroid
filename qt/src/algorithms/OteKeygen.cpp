/*
 * OteKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "OteKeygen.h"

OteKeygen::OteKeygen(QString & ssid, QString & mac) :
		Keygen(ssid, mac) {
}


QVector<QString> & OteKeygen::getKeys() {
    if (getMacAddress().length() == 12) {
        results.append(getMacAddress().toLower());
    } else {
        QString ssidIdentifier = getSsidName().right(6);
        results.append("c87b5b" + ssidIdentifier);
        results.append("fcc897" + ssidIdentifier);
        results.append("681ab2" + ssidIdentifier);
        results.append("b075d5" + ssidIdentifier);
        results.append("384608" + ssidIdentifier);
    }
	return results;
}



