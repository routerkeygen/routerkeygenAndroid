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
#include "RouterKeygen.h"
#include <cstring>

int main(int argc, char *
         argv[])
{
    QApplication app(argc, argv);
    QTranslator translator;
    QApplication::setApplicationName("RouterKeygen");
    QApplication::setApplicationVersion("1.0.0");
    QString qmFile = app.applicationName().toLower() + "_" + QLocale::system().name();
    if ( translator.load(qmFile,":/lang") )
        app.installTranslator(&translator);
    RouterKeygen w;
#if defined(Q_WS_S60)
    w.showMaximized();
#else
    if ( argc > 1 ){
    	if ( strcmp("-h", argv[1]) != 0)
            w.showWithDialog();
        else{

        }
    }
    else
        w.showWithDialog();
#endif

    return app.exec();
}
