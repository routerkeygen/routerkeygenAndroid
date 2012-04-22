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
#include "alicehandler.h"
#include <iostream>

AliceHandler::AliceHandler(QString a) : alice(a)
{

}
AliceHandler::~AliceHandler(){
  //  cleanInfo();
}
bool AliceHandler::isSupported(){
    return !this->supportedAlice.isEmpty();
}

QVector<AliceMagicInfo *> & AliceHandler::getSupportedAlice(){
    return this->supportedAlice;
}


bool AliceHandler::startElement(const QString &  ,
                              const QString & localName ,
                              const QString &,
                              const QXmlAttributes &attributes)
{
    int magic[2];
    QString serial;
    QString mac;
    bool status;
    if ( alice.toLower() == localName.toLower() )
    {
            serial = attributes.value("sn");
            mac = attributes.value("mac");
            magic[0] = attributes.value("q").toInt(&status, 10);
            magic[1] = attributes.value("k").toInt(&status, 10);
            supportedAlice.append(new AliceMagicInfo(alice, magic, serial, mac));
    }

    return true;
}


bool AliceHandler::fatalError(const QXmlParseException &exception)
{
    std::cerr << "Parse error at line " << exception.lineNumber()
              << ", " << "column " << exception.columnNumber() << ": "
              << qPrintable(exception.message()) << std::endl;
    return false;
}
bool AliceHandler::readFile(const QString &fileName)
{
    if ( !supportedAlice.isEmpty() )
        cleanInfo();
    QFile file(fileName);
    QXmlInputSource inputSource(&file);
    QXmlSimpleReader reader;
    reader.setContentHandler(this);
    reader.setErrorHandler(this);
    return reader.parse(inputSource);
}

void AliceHandler::cleanInfo(){
    for ( int i = 0 ; i < supportedAlice.size();++i )
        delete supportedAlice.at(i);
    supportedAlice.clear();
}
