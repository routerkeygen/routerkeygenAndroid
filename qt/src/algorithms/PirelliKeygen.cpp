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
#include "PirelliKeygen.h"

PirelliKeygen::PirelliKeygen(QString & ssid, QString & mac) :
		Keygen(ssid, mac) {
	this->hash = new QCryptographicHash(QCryptographicHash::Md5);
	ssidIdentifier = ssid.right(12);
}

PirelliKeygen::~PirelliKeygen() {
	delete hash;
}
const unsigned char PirelliKeygen::saltMD5[] = { 0x22, 0x33, 0x11, 0x34, 0x02, 0x81,
		0xFA, 0x22, 0x11, 0x41, 0x68, 0x11, 0x12, 0x01, 0x05, 0x22, 0x71, 0x42,
		0x10, 0x66 };

QVector<QString> & PirelliKeygen::getKeys() {
	bool status = false;
	char macBytes[6];
	for (int i = 0; i < 12; i += 2)
		macBytes[i / 2] = (ssidIdentifier.mid(i, 1).toInt(&status, 16) << 4)
				+ ssidIdentifier.mid(i + 1, 1).toInt(&status, 16);
	if (!status)
		throw ERROR;
	hash->reset();
	hash->addData(macBytes, 6);
    hash->addData((const char *)saltMD5, 20);
	QByteArray resultHash = hash->result();
	char key[5];
	/*Grouping in five groups fo five bits*/
	key[0] = (resultHash.at(0) & 0xF8) >> 3;
	key[1] = ((resultHash.at(0) & 0x07) << 2)
			| ((resultHash.at(1) & 0xC0) >> 6);
	key[2] = (resultHash.at(1) & 0x3E) >> 1;
	key[3] = ((resultHash.at(1) & 0x01) << 4)
			| ((resultHash.at(2) & 0xF0) >> 4);
	key[4] = ((resultHash.at(2) & 0x0F) << 1)
			| ((resultHash.at(3) & 0x80) >> 7);
	for (int i = 0; i < 5; ++i)
		if (key[i] >= 0x0A)
			key[i] += 0x57;
    results.append(QString::fromLatin1(QByteArray(key, 5).toHex().data()));
	return results;
}
