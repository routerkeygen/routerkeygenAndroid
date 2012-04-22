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
#include "eircomkeygen.h"
#include <QByteArray>
EircomKeygen::EircomKeygen( WifiNetwork * router ) : KeygenThread(router){}


void EircomKeygen::run(){

    if ( router == NULL)
            return;
    if ( router->getMac().size() != 12 )
    {
            //TODO:error messages
            return;
    }
    bool status = false;
    QString result = "Although your world wonders me, ";
    if ( !status )
    {
            //TODO:error messages
            return;
    }
    result = QString::fromAscii(QCryptographicHash::hash(
                                result.toAscii() ,
                                QCryptographicHash::Sha1 )
                                      .toHex().data());
    result.truncate(26);
    results.append(result);
    return;
}
 QString EircomKeygen::dectoString( int mac){
        QByteArray ret;
        while ( mac > 0 ){
                switch (mac %10){
                        case 0: ret.prepend("Zero");
                                        break;
                        case 1: ret.prepend("One");
                                        break;
                        case 2: ret.prepend("Two");
                                        break;
                        case 3: ret.prepend("Three");
                                        break;
                        case 4: ret.prepend("Four");
                                        break;
                        case 5: ret.prepend("Five");
                                        break;
                        case 6: ret.prepend("Six");
                                        break;
                        case 7: ret.prepend("Seven");
                                        break;
                        case 8: ret.prepend("Eight");
                                        break;
                        case 9: ret.prepend("Nine");
                                        break;
                }
               // mac /=10;
        }
        return ret;
}
