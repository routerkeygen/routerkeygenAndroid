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
#include "verizonkeygen.h"

VerizonKeygen::VerizonKeygen(QString & ssid, QString & mac, int level,
		QString enc) :
		Keygen(ssid, mac, level, enc){}

QVector<QString> & VerizonKeygen::getKeys() {
    QChar inverse[5];
    QString ssid = getSsidName();
    inverse[0] = ssid.at(4);
    inverse[1] = ssid.at(3);
    inverse[2] = ssid.at(2);
    inverse[3] = ssid.at(1);
    inverse[4] = ssid.at(0);
    bool test;
    int resultInt = QString::fromRawData(inverse , 5).toInt(&test, 36);
    if ( !test )
    {
        throw ERROR; //TODO: error message
    }
    QString result;
    result.setNum(resultInt , 16);
    while ( result.size() < 6 )
        result = "0" + result;
    QString mac = getMacAddress();
    if ( mac.isEmpty() )
    {
        results.append("1801" + result.toUpper());
        results.append("1F90" + result.toUpper());
    }
    else
        results.append(mac.mid(2,4) + result.toUpper());
    return results;
}
