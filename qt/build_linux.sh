#!/bin/sh

QT4_FILE="qt4-4.8-win32-bin.tar.bz2"
QT4_URL="https://android-thomson-key-solver.googlecode.com/files/qt4-4.8-win32-bin.tar.bz2"


SRC_FOLDER=`pwd` #assumed to be the current folder, change to compile in another location
ROOT_FOLDER=`pwd`
BUILD_FOLDER=$ROOT_FOLDER/linux
cd $BUILD_FOLDER

mkdir -p $BUILD_FOLDER
cd $BUILD_FOLDER

cmake -DCMAKE_BUILD_TYPE=Release $SRC_FOLDER
make
cpack -G DEB
cpack -G RPM #in Ubuntu rpmbuild must be installed
