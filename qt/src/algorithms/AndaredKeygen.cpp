/*
 * AndaredKeygen.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "AndaredKeygen.h"

AndaredKeygen::AndaredKeygen(QString & ssid, QString & mac) :
		Keygen(ssid, mac) {
}



QVector<QString> & AndaredKeygen::getKeys() {
	results.append(QString("6b629f4c299371737494c61b5a101693a2d4e9e1f3e1320f3ebf9ae379cecf32"));
	return results;
}
