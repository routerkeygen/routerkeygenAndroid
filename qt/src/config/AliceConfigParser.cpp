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
#include "AliceConfigParser.h"
#include <iostream>
#include <stdio.h>
#include <QFile>
#include <QTextStream>
#include <QStringList>

QMap<QString ,QVector<AliceMagicInfo *> *> * AliceConfigParser::readFile(const QString &fileName) {
    QFile file(fileName);
    if(!file.open(QIODevice::ReadOnly)) {
        return NULL;
    }
    QTextStream in(&file);
    int magic[2];
    QMap<QString ,QVector<AliceMagicInfo *> *> * supportedAlices = new QMap<QString ,QVector<AliceMagicInfo *> *>();
    while(!in.atEnd()) {
        QString line = in.readLine();
        QStringList infos = line.split(",");
        QString name = infos[0];
        QVector<AliceMagicInfo*> * supported = supportedAlices->value(name);
        if (supported == NULL) {
            supported = new QVector<AliceMagicInfo*>();
            supportedAlices->insert(name, supported);
        }
        QString serial = infos[1];
        magic[0] = infos[2].toInt(NULL,10); // k
        magic[1] = infos[3].toInt(NULL,10); // q
        QString mac = infos[4];
        supported->append(new AliceMagicInfo(name, magic, serial, mac));
    }
    file.close();
    return supportedAlices;
}
