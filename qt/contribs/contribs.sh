#!/bin/sh

QT4_FILE="qt4-4.8-win32-bin.tar.bz2"
QT4_URL="http://rohityadav.in/files/contribs/qt4-4.8-win32-bin.tar.bz2"

ROOT_FOLDER=`pwd`
DOWNLOADS_FOLDER=$ROOT_FOLDER/downloads

# Get the dependencies, Qt
mkdir -p $DOWNLOADS_FOLDER
cd $DOWNLOADS_FOLDER

if [ ! -f $QT4_FILE ]; then
    #wget $QT4_URL ;
    echo "Qt4";
else
    echo "Qt4 OK";
fi

cd $ROOT_FOLDER

# bin and dlls
mkdir bin && mkdir include

# Qt
tar xvf $DOWNLOADS_FOLDER/$QT4_FILE -C . --strip-components=1
cd include && ln -sf qt4/src && cd ..

# make tools/moc.exe executable
chmod +x tools/moc.exe
