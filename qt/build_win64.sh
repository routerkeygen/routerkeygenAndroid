#!/bin/sh

QT4_FILE="qt4-4.8-win32-bin.tar.bz2"
QT4_URL="https://android-thomson-key-solver.googlecode.com/files/qt4-4.8-win32-bin.tar.bz2"


ROOT_FOLDER=`pwd`
BUILD_FOLDER=$ROOT_FOLDER/win64
DOWNLOADS_FOLDER=$BUILD_FOLDER/downloads

# Get the dependencies, Qt
mkdir -p $DOWNLOADS_FOLDER
cd $DOWNLOADS_FOLDER

if [ ! -f $QT4_FILE ]; then
    wget $QT4_URL;
else
    echo "Qt4 OK";
fi

cd $BUILD_FOLDER

# bin and dlls
mkdir bin && mkdir include

# Qt
tar xvf $DOWNLOADS_FOLDER/$QT4_FILE -C . --strip-components=1
cd include && ln -sf qt4/src && cd ..

# make tools/moc.exe executable
chmod +x tools/moc.exe


cmake -DCMAKE_TOOLCHAIN_FILE=../cmake/toolchain-win64.cmake -DQT_MOC_EXECUTABLE=tools/moc.exe -DCMAKE_BUILD_TYPE=Release -DQT_QMAKE_EXECUTABLE=/usr/bin/qmake-qt4 ..
make
if [ "$?" = "0" ]; then
	make installer
else
	echo "Error while building" 1>&2
	exit 1
fi

