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
#include "TeleTuConfigParser.h"
#include <iostream>
#include <stdio.h>
#include <QFile>
#include <QTextStream>
#include <QStringList>

QMap<QString ,QVector<TeleTuMagicInfo *> *> * TeleTuConfigParser::readFile(const QString &fileName) {
    QFile file(fileName);
    if(!file.open(QIODevice::ReadOnly)) {
        return NULL;
    }
    QTextStream in(&file);
    int range[2];
    QMap<QString ,QVector<TeleTuMagicInfo *> *> * supportedTeleTus = new QMap<QString ,QVector<TeleTuMagicInfo *> *>();
    while(!in.atEnd()) {
        QString line = in.readLine();
        QStringList infos = line.split(" ");
        QString name = infos[0];
        QVector<TeleTuMagicInfo*> * supported = supportedTeleTus
                ->value(name);
        if (supported == NULL) {
            supported = new QVector<TeleTuMagicInfo*>();
            supportedTeleTus->insert(name, supported);
        }
        range[0] = infos[1].toInt(NULL,16); // from
        range[1] = infos[2].toInt(NULL,16); // to
        QString serial = infos[3];
        int base = infos[4].toInt(NULL,16);
        int divider = infos[5].toInt(NULL,10);
        supported->append(new TeleTuMagicInfo(range, serial, base, divider));
    }
    file.close();
    return supportedTeleTus;
}
