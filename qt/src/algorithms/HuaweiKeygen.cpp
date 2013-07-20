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
#include "HuaweiKeygen.h"
#include <QRegExp>

HuaweiKeygen::HuaweiKeygen(QString & ssid, QString & mac, int level,
		QString enc) :
		Keygen(ssid, mac, level, enc){}


int HuaweiKeygen::getSupportState() const{
    if ( getSsidName().count(QRegExp("^INFINITUM[0-9a-zA-Z]{4}$")) == 1 )
        return SUPPORTED;
    return UNLIKELY;
}

QVector<QString> & HuaweiKeygen::getKeys() {
    int a0[]= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    int a1[]= {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    int a2[]= {0,13,10,7,5,8,15,2,10,7,0,13,15,2,5,8};
    int a3[]= {0,1,3,2,7,6,4,5,15,14,12,13,8,9,11,10};
    int a5[]= {0,4,8,12,0,4,8,12,0,4,8,12,0,4,8,12};
    int a7[]= {0,8,0,8,1,9,1,9,2,10,2,10,3,11,3,11};
    int a8[]= {0,5,11,14,6,3,13,8,12,9,7,2,10,15,1,4};
    int a10[]= {0,14,13,3,11,5,6,8,6,8,11,5,13,3,0,14};
    int a14[]= {0,1,3,2,7,6,4,5,14,15,13,12,9,8,10,11};
    int a15[]= {0,1,3,2,6,7,5,4,13,12,14,15,11,10,8,9};
    int n5[]= {0,5,1,4,6,3,7,2,12,9,13,8,10,15,11,14};
    int n6[]= {0,14,4,10,11,5,15,1,6,8,2,12,13,3,9,7};
    int n7[]= {0,9,0,9,5,12,5,12,10,3,10,3,15,6,15,6};
    int n11[]= {0,14,13,3,9,7,4,10,6,8,11,5,15,1,2,12};
    int n12[]= {0,13,10,7,4,9,14,3,10,7,0,13,14,3,4,9};
    int n13[]= {0,1,3,2,6,7,5,4,15,14,12,13,9,8,10,11};
    int n14[]= {0,1,3,2,4,5,7,6,12,13,15,14,8,9,11,10};
    int n31[]= {0,10,4,14,9,3,13,7,2,8,6,12,11,1,15,5};
    int key[]= {30,31,32,33,34,35,36,37,38,39,61,62,63,64,65,66};
    QString macAd = getMacAddress();
    if ( macAd.size() != 12 )
            throw ERROR;
    int mac[12];
    bool status;
    for ( int i = 0 ; i < 12 ; ++i)
    {
            mac[i] =  macAd.mid(i, 1).toInt(&status, 16);
            if ( !status )
                throw ERROR;
    }
    int ya=(a2[mac[0]])^(n11[mac[1]])^(a7[mac[2]])^(a8[mac[3]])^(a14[mac[4]])^
                    (a5[mac[5]])^(a5[mac[6]])^(a2[mac[7]])^(a0[mac[8]])^(a1[mac[9]])^
                    (a15[mac[10]])^(a0[mac[11]])^13;
    int yb=(n5[mac[0]])^(n12[mac[1]])^(a5[mac[2]])^(a7[mac[3]])^(a2[mac[4]])^
                    (a14[mac[5]])^(a1[mac[6]])^(a5[mac[7]])^(a0[mac[8]])^(a0[mac[9]])^
                    (n31[mac[10]])^(a15[mac[11]])^4;
    int yc=(a3[mac[0]])^(a5[mac[1]])^(a2[mac[2]])^(a10[mac[3]])^(a7[mac[4]])^
                    (a8[mac[5]])^(a14[mac[6]])^(a5[mac[7]])^(a5[mac[8]])^(a2[mac[9]])^
                    (a0[mac[10]])^(a1[mac[11]])^7;
    int yd=(n6[mac[0]])^(n13[mac[1]])^(a8[mac[2]])^(a2[mac[3]])^(a5[mac[4]])^
                    (a7[mac[5]])^(a2[mac[6]])^(a14[mac[7]])^(a1[mac[8]])^(a5[mac[9]])^
                    (a0[mac[10]])^(a0[mac[11]])^14;
    int ye=(n7[mac[0]])^(n14[mac[1]])^(a3[mac[2]])^(a5[mac[3]])^(a2[mac[4]])^
                    (a10[mac[5]])^(a7[mac[6]])^(a8[mac[7]])^(a14[mac[8]])^(a5[mac[9]])^
                    (a5[mac[10]])^(a2[mac[11]])^7;
    QString num , keyString ="";
    num.setNum(key[ya]);
    keyString += num;
    num.setNum(key[yb]);
    keyString += num;
    num.setNum(key[yc]);
    keyString += num;
    num.setNum(key[yd]);
    keyString += num;
    num.setNum(key[ye]);
    keyString += num;
    results.append(keyString);
    return results;

}
