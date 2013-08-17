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
#include "OteHuaweiKeygen.h"
#include <QStringList>

OteHuaweiKeygen::OteHuaweiKeygen(QString & ssid, QString & mac,
        QString supported) :
        Keygen(ssid, mac), magicValues(supported) {

}

const int OteHuaweiKeygen::MAGIC_NUMBER = 65535;

QVector<QString> & OteHuaweiKeygen::getKeys() {
    QString mac = getMacAddress();
    if (mac.length() != 12) {
        throw ERROR;
    }
    QStringList magic = magicValues.split(" ");
    QString series = mac.left(2) + mac.mid(6, 2);
    int point;
    if (series == "E8FD" )
        point = 0;
    else if (series == "E8F5")
        point = 1;
    else if (series == "E8F6")
        point = 2;
    else
        return results;
    if (point >= magic.length())
        return results;
    QString pass = "000000" + magic.at(point);
    results.append(pass.mid(pass.length() - 8));
    return results;
}
