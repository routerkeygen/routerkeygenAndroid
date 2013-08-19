background.png#!/bin/sh

SRC_FOLDER=`pwd` #assumed to be the current folder, change to compile in another location
ROOT_FOLDER=`pwd`
BUILD_FOLDER=$ROOT_FOLDER/mac
BACKGROUND_NAME=background.png
BACKGROUND_LOCATION=$ROOT_FOLDER/share/$BACKGROUND_NAME
DMG_NAME=RouterKeygen_V1_0_0
TITLE=RouterKeygen
APPLICATION_NAME="$TITLE".app


mkdir -p $BUILD_FOLDER
cd $BUILD_FOLDER

cmake -DCMAKE_BUILD_TYPE=Release -DQT_QMAKE_EXECUTABLE=/Users/angela/QtSDK/Desktop/Qt/4.8.1/gcc/bin/qmake $SRC_FOLDER
if [ "$?" = "0" ]; then	
	make
else
	echo "Could not create Makefiles" 1>&2
	exit 1
fi

if [ "$?" = "0" ]; then
    /Users/angela/QtSDK/QtSources/4.8.1/bin/macdeployqt bin/routerkeygen.app
    mv bin/routerkeygen.app bin/RouterKeygen.app
else
    echo "Error while building" 1>&2
    exit 1
fi


mkdir -p bin/.background
cp "${BACKGROUND_LOCATION}" bin/.background
hdiutil create -srcfolder bin -volname "${TITLE}" -fs HFS+ \
-fsargs "-c c=64,a=16,e=16" -format UDRW -size 100000k pack.temp.dmg

device=$(hdiutil attach -readwrite -noverify -noautoopen "pack.temp.dmg" | \
egrep '^/dev/' | sed 1q | awk '{print $1}')
sleep 5

echo '
tell application "Finder"
tell disk "'${TITLE}'"
open
set current view of container window to icon view
set toolbar visible of container window to false
set statusbar visible of container window to false
set the bounds of container window to {400, 100, 885, 430}
set theViewOptions to the icon view options of container window
set arrangement of theViewOptions to not arranged
set icon size of theViewOptions to 128
set background picture of theViewOptions to file ".background:'${BACKGROUND_NAME}'"
make new alias file at container window to POSIX file "/Applications" with properties {name:"Applications"}
set position of item "'${APPLICATION_NAME}'" of container window to {110, 180}
set position of item "Applications" of container window to {375, 180}
close
open
update without registering applications
delay 5
end tell
end tell
' | osascript

chmod -Rf go-w /Volumes/"${TITLE}"
sync
sync
hdiutil detach ${device}
rm -f "${DMG_NAME}".dmg
hdiutil convert pack.temp.dmg -format UDBZ -imagekey bzip2-level=9 -o "${DMG_NAME}"
rm -f pack.temp.dmg
