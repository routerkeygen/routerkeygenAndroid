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
#include "DlinkKeygen.h"

DlinkKeygen::DlinkKeygen(QString & ssid, QString & mac, int level,
		QString enc) :
		Keygen(ssid, mac, level, enc) {}

char DlinkKeygen::hash[] = { 'X', 'r', 'q', 'a', 'H', 'N', 'p', 'd', 'S', 'Y',
			'w', '8', '6', '2', '1', '5' };

QVector<QString> & DlinkKeygen::getKeys() {
    QString mac = getMacAddress();
    if ( mac.size() < 12 )
        throw ERROR;
    char key[20];
    key[0]=mac.at(11).toLatin1();
    key[1]=mac.at(0).toLatin1();

    key[2]=mac.at(10).toLatin1();
    key[3]=mac.at(1).toLatin1();

    key[4]=mac.at(9).toLatin1();
    key[5]=mac.at(2).toLatin1();

    key[6]=mac.at(8).toLatin1();
    key[7]=mac.at(3).toLatin1();

    key[8]=mac.at(7).toLatin1();
    key[9]=mac.at(4).toLatin1();

    key[10]=mac.at(6).toLatin1();
    key[11]=mac.at(5).toLatin1();

    key[12]=mac.at(1).toLatin1();
    key[13]=mac.at(6).toLatin1();

    key[14]=mac.at(8).toLatin1();
    key[15]=mac.at(9).toLatin1();

    key[16]=mac.at(11).toLatin1();
    key[17]=mac.at(2).toLatin1();

    key[18]=mac.at(4).toLatin1();
    key[19]=mac.at(10).toLatin1();
    char newkey[21];
    char t;
    int index = 0;
    for (int i=0; i < 20 ; i++)
    {
        t=key[i];
        if ((t >= '0') && (t <= '9'))
            index = t-'0';
        else
        {
            t= QChar::toUpper((ushort)t);
            if ((t >= 'A') && (t <= 'F'))
                index = t-'A'+10;
            else
            {
               throw ERROR;
            }
        }
        newkey[i]=hash[index];
    }
    newkey[20] = '\0';
    results.append(QString(newkey));
    return results;
}
