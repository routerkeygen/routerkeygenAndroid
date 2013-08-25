/*
 * Copyright 2013 Rui Araújo, Luís Fonseca
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
#include "OteBAUDKeygen.h"

OteBAUDKeygen::OteBAUDKeygen(QString ssid, QString mac) :
		Keygen(ssid, mac) {
}


QVector<QString> & OteBAUDKeygen::getKeys() {
    if (getMacAddress().length() != 12) {
        throw ERROR;
    }
    results.append("0"+getMacAddress().toLower());
	return results;
}



