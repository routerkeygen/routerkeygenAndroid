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
#include "OTEHuaweiConfigParser.h"
#include <QFile>
#include <QTextStream>
#include <QString>


QStringList * OTEHuaweiConfigParser::readFile(const QString &fileName) {
    QFile file(fileName);
    if(!file.open(QIODevice::ReadOnly)) {
        return NULL;
    }
    QTextStream in(&file);
    QStringList * linesInfo =  new QStringList();
    while(!in.atEnd()) {
        QString line = in.readLine();
        linesInfo->append(line);
    }
    file.close();
    return linesInfo;
}
