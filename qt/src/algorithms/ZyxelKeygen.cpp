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
#include "ZyxelKeygen.h"

ZyxelKeygen::ZyxelKeygen(QString ssid, QString mac) :
		Keygen(ssid, mac) {
	this->hash = new QCryptographicHash(QCryptographicHash::Md5);
}
ZyxelKeygen::~ZyxelKeygen(){
	delete hash;
}


QVector<QString> & ZyxelKeygen::getKeys() {
	if (getMacAddress().size() != 12)
		throw ERROR;
	this->hash->reset();
	QString macMod = getMacAddress().left(8) + getSsidName().right(4);
    this->hash->addData(macMod.toLower().toLatin1());
    QString result = QString::fromLatin1(this->hash->result().toHex().data());
	result.truncate(20);
	this->results.append(result.toUpper());
	return results;
}
