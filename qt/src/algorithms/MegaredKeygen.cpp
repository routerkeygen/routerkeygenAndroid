/*
 * MegaredKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "MegaredKeygen.h"

MegaredKeygen::MegaredKeygen(QString ssid, QString mac) :
		Keygen(ssid, mac) {
}



QVector<QString> & MegaredKeygen::getKeys() {
	results.append(getMacAddress().right(10));
	return results;
}

