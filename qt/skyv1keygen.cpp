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
#include "skyv1keygen.h"

SkyV1Keygen::SkyV1Keygen( WifiNetwork * router ) : KeygenThread(router) ,
                    ALPHABET("ABCDEFGHIJKLMNOPQRSTUVWXYZ") {}

void SkyV1Keygen::run(){
    if ( router == NULL)
        return;
    if ( router->getMac().size() != 12 )
    {
            //TODO:error messages
            return;
    }
   QByteArray hash = QCryptographicHash::hash( router->getMac().toAscii() ,
                                QCryptographicHash::Md5 );

    if ( stopRequested )
        return;
    QString key = "";
    for ( int i = 1 ; i <= 15 ; i += 2 )
    {
            unsigned char index = hash[i];
            index %= 26;
            key += ALPHABET.at(index);
    }
    this->results.append(key);
}
