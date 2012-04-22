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

VerizonKeygen::VerizonKeygen( WifiNetwork * router ) : KeygenThread(router) {}

void VerizonKeygen::run(){
    QChar * inverse = new QChar[5];
    inverse[0] = router->getSSID().at(4);
    inverse[1] = router->getSSID().at(3);
    inverse[2] = router->getSSID().at(2);
    inverse[3] = router->getSSID().at(1);
    inverse[4] = router->getSSID().at(0);
    bool test;
    int resultInt = QString::fromRawData(inverse , 5).toInt(&test, 36);
    if ( !test )
    {
        return; //TODO: error message
    }
    QString result;
    result.setNum(resultInt , 16);
    while ( result.size() < 6 )
        result = "0" + result;
    if ( router->getMac().isEmpty() )
    {
        results.append("1801" + result.toUpper());
        results.append("1F90" + result.toUpper());
    }
    else
        results.append(router->getMac().mid(2,4) + result.toUpper());
}
