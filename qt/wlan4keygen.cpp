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
#include "wlan4keygen.h"

Wlan4Keygen::Wlan4Keygen( WifiNetwork * router ) : KeygenThread(router) ,
                            magic("bcgbghgg"){
    this->hash = new QCryptographicHash(QCryptographicHash::Md5);
}

void Wlan4Keygen::run(){
    if ( router == NULL)
        return;
    if ( router->getMac().size() != 12 )
    {
        //TODO:error messages
        return;
    }
    this->hash->reset();
    if (!router->getMac().startsWith("001FA4"))
        this->hash->addData(magic.toAscii());
    QString macMod = router->getMac().left(8) + router->getSSIDsubpart();
    if (!router->getMac().startsWith("001FA4"))
        this->hash->addData(macMod.toUpper().toAscii());
    else
        this->hash->addData(macMod.toLower().toAscii());
    if (!router->getMac().startsWith("001FA4"))
        this->hash->addData(router->getMac().toAscii());
    QString result = QString::fromAscii(this->hash->result().toHex().data());
    result.truncate(20);
    if (!router->getMac().startsWith("001FA4"))
        this->results.append(result);
    else
        this->results.append(result.toUpper());
    if ( stopRequested )
        return;
}
