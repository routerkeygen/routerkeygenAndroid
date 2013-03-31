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
#include <QtGui/QApplication>
#include <QTranslator>
#include <QLocale>
#include <iostream>
#include "RouterKeygen.h"
#include "WirelessMatcher.h"
#include "Keygen.h"
#include "qcmdlineparser/qcmdlineparser.h"
#include "version.h"

int main(int argc, char *
         argv[])
{
    QApplication app(argc, argv);
    QTranslator translator;
    QApplication::setApplicationName(PROJECT_NAME);
    QApplication::setApplicationVersion(PROJECT_VERSION);
    QString qmFile = app.applicationName().toLower() + "_" + QLocale::system().name();
    if ( translator.load(qmFile,":/lang") )
        app.installTranslator(&translator);
    QCmdLineParser parser;
    parser.setApplicationName(QCoreApplication::applicationName());
    parser.addOption("-s", QCmdLineArgument::StoreValue, QObject::tr("SSID"), "--ssid");
    parser.addOption("-m", QCmdLineArgument::StoreValue, QObject::tr("MAC address"), "--mac");
    parser.addOption("--no-gui", QCmdLineArgument::StoreTrue, QObject::tr("No UI launch"));
    QString error = "";
    QVariantMap options = parser.parse(QCoreApplication::arguments(), &error);
    if ( !error.isEmpty() ){
        std::cout << error.toUtf8().data() << '\n' <<  parser.help().toUtf8().data();
        return -1;
    }
    if ( options.contains("s") || options.contains("m") ){
        WirelessMatcher m;
        QString mac = options.value("m", "").toString().toUpper();
        if ( mac.length()>0 && mac.count(QRegExp("^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$")) == 0 ){
            mac = "";
            std::cout << QObject::tr("Invalid MAC. It will not be used.").toUtf8().data() << '\n';
        }
        Keygen * keygen = m.getKeygen(options.value("s", "").toString(),mac );
        if ( keygen == NULL ){
            std::cout << QObject::tr("Unsupported network. Check the MAC address and the SSID.").toUtf8().data() << '\n';
            return -2;
        }
        std::cout << QObject::tr("Calculating keys. This can take a while.").toUtf8().data() << '\n';
        try{
            QVector<QString> results = keygen->getResults();
            if (results.isEmpty()) {
                std::cout << QObject::tr("No keys were calculated.").toUtf8().data() << '\n';
            }else{
                std::cout << QObject::tr("Calculated Passwords for %1").arg(keygen->getSsidName()).toUtf8().data() << '\n';
                for (int i = 0; i < results.size(); ++i)
                    std::cout <<  results.at(i).toLatin1().data() << '\n';
            }
            return 0;
        }catch (int e){
            std::cout << QObject::tr("Error while calculating.").toUtf8().data() << '\n';
            delete keygen;
            return -3;
        }
    }
    RouterKeygen w;
    if ( !options.value("no-gui", false).toBool() )
        w.showWithDialog();
    return app.exec();
}
