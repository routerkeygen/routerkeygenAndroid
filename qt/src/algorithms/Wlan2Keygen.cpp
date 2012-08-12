/*
 * Copyright 2012 Rui Araújo, Luís Fonseca
 *
 * This file is part of Router Keygen.
 *
 * Router Keygen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Router Keygen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Router Keygen.  If not, see <http://www.gnu.org/licenses/>.
 */
#include "Wlan2Keygen.h"

Wlan2Keygen::Wlan2Keygen(QString & ssid, QString & mac, int level, QString enc) :
		Keygen(ssid, mac, level, enc) {
}

QVector<QString> & Wlan2Keygen::getKeys() {
	QChar key[26];

	QString mac = getMacAddress();

	if (mac.size() != 12)
		throw ERROR;

	key[0] = mac.at(10);
	key[1] = mac.at(11);
	key[2] = mac.at(0);
	key[3] = mac.at(1);
	key[4] = mac.at(8);
	key[5] = mac.at(9);
	key[6] = mac.at(2);
	key[7] = mac.at(3);
	key[8] = mac.at(4);
	key[9] = mac.at(5);
	key[10] = mac.at(6);
	key[11] = mac.at(7);
	key[12] = mac.at(10);
	key[13] = mac.at(11);
	key[14] = mac.at(8);
	key[15] = mac.at(9);
	key[16] = mac.at(2);
	key[17] = mac.at(3);
	key[18] = mac.at(4);
	key[19] = mac.at(5);
	key[20] = mac.at(6);
	key[21] = mac.at(7);
	key[22] = mac.at(0);
	key[23] = mac.at(1);
	key[24] = mac.at(4);
	key[25] = mac.at(5);

	QChar begin = getSsidName().right(2).at(1);
	if (!begin.isDigit()) {
		QString cadena(key, 2);
		bool test;
		int value = QString::fromRawData(key, 2).toInt(&test, 16);
		if (!test) {
			throw ERROR;
		}
		value--;
		cadena.setNum(value, 16);
		if (cadena.size() < 2)
			cadena = "0" + cadena;
		key[0] = cadena.at(0);
		key[1] = cadena.at(1);
	}

	results.append(QString::fromRawData(key, 26));
	return results;
}
