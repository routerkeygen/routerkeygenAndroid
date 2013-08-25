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
#ifndef CABOVISAOSAGEMKEYGEN_H
#define CABOVISAOSAGEMKEYGEN_H
#include "Keygen.h"
#include <QCryptographicHash>

class CabovisaoSagemKeygen: public Keygen {
public:
	CabovisaoSagemKeygen(QString ssid, QString mac);
	~CabovisaoSagemKeygen();
private:
	QVector<QString> & getKeys();
    const static QString keyBase;
	QString ssidIdentifier;
};

#endif // CabovisaoSagemKeygen_H
