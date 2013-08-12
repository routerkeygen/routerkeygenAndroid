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
#include "TeleTuKeygen.h"
#include "config/TeleTuMagicInfo.h"
#include <QCryptographicHash>

TeleTuKeygen::TeleTuKeygen(QString & ssid, QString & mac, int level,
        QString enc, TeleTuMagicInfo * m) :
    Keygen(ssid, mac, level, enc) , magicInfo(m){}

QVector<QString> & TeleTuKeygen::getKeys() {
    QString mac = getMacAddress();
    if ( mac.size() < 12 )
        throw ERROR;
    int serialInt = ( mac.mid(6).toInt(NULL,16) - magicInfo->base) / magicInfo->divider;
    QString serialEnd;
    serialEnd.setNum(serialInt);
    while (serialEnd.length() < 7) {
        serialEnd = "0" + serialEnd;
    }
    results.append(magicInfo->serial + "Y" + serialEnd);
    return results;

}
