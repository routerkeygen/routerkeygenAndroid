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
#include "OnoKeygen.h"
#include <QCryptographicHash>

OnoKeygen::OnoKeygen(QString ssid, QString mac) :
        Keygen(ssid, mac) {
}

QVector<QString> & OnoKeygen::getKeys() {
	QString ssid = getSsidName();
	if (ssid.size() != 13)
		throw ERROR;
	QString val = "";
	val.setNum(ssid.at(12).digitValue() + 1, 10);
	if (val.size() < 2)
		val = ssid.left(11) + "0" + val;
	else
		val = ssid.left(11) + val;
	int pseed[4];
	pseed[0] = 0;
	pseed[1] = 0;
	pseed[2] = 0;
	pseed[3] = 0;
	int randNumber = 0;
	for (int i = 0; i < val.length(); i++) {
        pseed[i % 4] ^= (int) val.at(i).toLatin1();
	}
	randNumber = pseed[0] | (pseed[1] << 8) | (pseed[2] << 16)
			| (pseed[3] << 24);
	short tmp = 0;
	QString key = "", aux = "";

	for (int j = 0; j < 5; j++) {
		randNumber = (randNumber * 0x343fd + 0x269ec3) & 0xffffffff;
		tmp = (short) ((randNumber >> 16) & 0xff);
		aux.setNum(tmp, 16);
		key += aux.toUpper();
	}
	results.append(key);
    key = QString::fromLatin1(
            QCryptographicHash::hash(padto64(val).toLatin1(),
					QCryptographicHash::Md5).toHex().data());
	key.truncate(26);
	results.append(key.toUpper());
	return results;
}

QString OnoKeygen::padto64(QString val) {
	if (val == "")
		return "";
	QString ret = "";
	for (int i = 0; i < (1 + (64 / (val.length()))); ++i)
		ret += val;
	return ret.left(64);
}
