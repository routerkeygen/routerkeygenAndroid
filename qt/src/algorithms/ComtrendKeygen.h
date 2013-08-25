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
#ifndef COMTRENDKEYGEN_H
#define COMTRENDKEYGEN_H
#include "Keygen.h"
#include <QCryptographicHash>

class ComtrendKeygen: public Keygen {
private:
	const static QString magic;
	QVector<QString> & getKeys() ;
	QCryptographicHash * hash;
public:
	ComtrendKeygen(QString ssid, QString mac);
	~ComtrendKeygen();
};

#endif //  COMTRENDKEYGEN_H
