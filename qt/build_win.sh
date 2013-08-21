#!/bin/sh

MXE_ROOT=/home/ruka/Projetos/mxe
SRC_FOLDER=`pwd` #assumed to be the current folder, change to compile in another location
ROOT_FOLDER=`pwd`
BUILD_FOLDER=$ROOT_FOLDER/win
mkdir -p $BUILD_FOLDER
cd $BUILD_FOLDER

cmake -DCMAKE_TOOLCHAIN_FILE=$MXE_ROOT/usr/i686-pc-mingw32/share/cmake/mxe-conf.cmake  -DQT_LUPDATE_EXECUTABLE=lupdate -DQT_LRELEASE_EXECUTABLE=lrelease $SRC_FOLDER
if [ "$?" = "0" ]; then	
	make
else
	echo "Could not create Makefiles" 1>&2
	exit 1
fi
if [ "$?" = "0" ]; then
	make installer
else
	echo "Error while building" 1>&2
	exit 1
fi
