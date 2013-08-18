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
#include "SkyV1Keygen.h"
#include <QCryptographicHash>

SkyV1Keygen::SkyV1Keygen(QString & ssid, QString & mac) :
		Keygen(ssid, mac) {
}
const QString SkyV1Keygen::ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

QVector<QString> & SkyV1Keygen::getKeys() {
	QString mac = getMacAddress();
	if (mac.size() != 12)
		throw ERROR;
    QByteArray hash = QCryptographicHash::hash(mac.toLatin1(),
			QCryptographicHash::Md5);

	QString key = "";
	for (int i = 1; i <= 15; i += 2) {
		unsigned char index = hash[i];
		index %= 26;
		key += ALPHABET.at(index);
	}
	this->results.append(key);
	return results;
}
