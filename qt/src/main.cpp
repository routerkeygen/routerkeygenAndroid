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
#include <QtGui/QApplication>
#include "routerkeygen.h"
#include <cstring>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    RouterKeygen w;
#if defined(Q_WS_S60)
    w.showMaximized();
#else
    if ( argc > 1 ){
    	if ( strcmp("-h", argv[1]) != 0)
    		w.show();
    }
    else
    	w.show();
#endif

    return a.exec();
}
