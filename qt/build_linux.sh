#!/bin/sh

SRC_FOLDER=`pwd` #assumed to be the current folder, change to compile in another location
ROOT_FOLDER=`pwd`
BUILD_FOLDER=$ROOT_FOLDER/linux
mkdir -p $BUILD_FOLDER
cd $BUILD_FOLDER

cmake -DCMAKE_BUILD_TYPE=Release -DQT_QMAKE_EXECUTABLE=/usr/bin/qmake-qt4 $SRC_FOLDER
if [ "$?" = "0" ]; then	
	make
else
	echo "Could not create Makefiles" 1>&2
	exit 1
fi
if [ "$?" = "0" ]; then
	cpack -G DEB
	cpack -G RPM #in Ubuntu rpmbuild must be installed
else
	echo "Error while building" 1>&2
	exit 1
fi
