# -------------------------------------------------
# Project created by QtCreator 2011-04-12T00:20:13
# -------------------------------------------------
QT += core \
    gui \
    xml
unix:QT += dbus
TARGET = RouterKeygen
TEMPLATE = app
SOURCES += src/algorithms/EasyBoxKeygen.cpp \
    src/algorithms/PBSKeygen.cpp \
    src/algorithms/OteKeygen.cpp \
    src/algorithms/MegaredKeygen.cpp \
    src/algorithms/AndaredKeygen.cpp \
    src/algorithms/ConnKeygen.cpp \
    src/wifi/QWifiManager.cpp \
    src/algorithms/dlinkkeygen.cpp \
    src/algorithms/pirellikeygen.cpp \
    src/algorithms/wlan2keygen.cpp \
    src/algorithms/wlan6keygen.cpp \
    src/algorithms/zyxelkeygen.cpp \
    src/algorithms/skyv1keygen.cpp \
    src/algorithms/tecomkeygen.cpp \
    src/algorithms/telseykeygen.cpp \
    src/algorithms/thomsonkeygen.cpp \
    src/algorithms/verizonkeygen.cpp \
    src/algorithms/eircomkeygen.cpp \
    src/algorithms/huaweikeygen.cpp \
    src/algorithms/infostradakeygen.cpp \
    src/algorithms/keygen.cpp \
    src/algorithms/onokeygen.cpp \
    src/algorithms/alicekeygen.cpp \
    src/algorithms/comtrendkeygen.cpp \
    src/algorithms/discuskeygen.cpp \
    src/KeygenThread.cpp \
    src/WirelessMatcher.cpp \
    src/alicehandler.cpp \
    src/alicemagicinfo.cpp \
    src/division.c \
    src/main.cpp \
    src/routerkeygen.cpp \
    src/sha256.cpp
HEADERS += src/include/EasyBoxKeygen.h \
    src/include/OteKeygen.h \
    src/include/PBSKeygen.h \
    src/include/MegaredKeygen.h \
    src/include/AndaredKeygen.h \
    src/include/ConnKeygen.h \
    src/include/QScanResult.h \
    src/include/QWifiManager.h \
    src/include/onokeygen.h \
    src/include/sha256.h \
    src/include/Keygen.h \
    src/include/KeygenThread.h \
    src/include/WirelessMatcher.h \
    src/include/alicehandler.h \
    src/include/alicemagicinfo.h \
    src/include/routerkeygen.h \
    src/include/alicekeygen.h \
    src/include/comtrendkeygen.h \
    src/include/discuskeygen.h \
    src/include/dlinkkeygen.h \
    src/include/eircomkeygen.h \
    src/include/huaweikeygen.h \
    src/include/infostradakeygen.h \
    src/include/pirellikeygen.h \
    src/include/skyv1keygen.h \
    src/include/tecomkeygen.h \
    src/include/telseykeygen.h \
    src/include/thomsonkeygen.h \
    src/include/unknown.h \
    src/include/verizonkeygen.h \
    src/include/wlan2keygen.h \
    src/include/wlan6keygen.h \
    src/include/zyxelkeygen.h

INCLUDEPATH += src/include/
unix:INCLUDEPATH += /usr/include/NetworkManager
unix:LIBS += -lcrypto

win32:SOURCES +=  src/sha1/sha1dgst.c \
                src/sha1/sha1-586.win32.S
win32:HEADERS +=src/include/sha_locl.h \
    src/include/sha.h \
    src/include/opensslconf.h \
    src/include/md32_common.h

FORMS += forms/routerkeygen.ui
symbian { 
    TARGET.UID3 = 0xed94ef91
    QMAKE_CXXFLAGS.GCCE -= -fvisibility-inlines-hidden
    
    # other fix for the other option here: http://wiki.forum.nokia.com/index.php/How_to_use_GCCE_and_Open_C
    # TARGET.CAPABILITY +=
    TARGET.EPOCSTACKSIZE = 0x14000
    TARGET.EPOCHEAPSIZE = 0x020000 \
        0x800000
}
RESOURCES += resources.qrc
