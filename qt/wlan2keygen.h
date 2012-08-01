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
#ifndef WLAN2KEYGEN_H
#define WLAN2KEYGEN_H
#include "Keygen.h"

class Wlan2Keygen : public Keygen
{
    public:
        Wlan2Keygen(QString & ssid, QString & mac, int level, QString enc);
    private:
    	QVector<QString> & getKeys() ;

};

#endif // WLAN2KEYGEN_H
