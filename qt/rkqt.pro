# -------------------------------------------------
# Project created by QtCreator 2011-04-12T00:20:13
# -------------------------------------------------
QT += core gui
TARGET = RouterKeygen
TEMPLATE = app
SOURCES += src/RouterKeygen.cpp \
    src/algorithms/ZyxelKeygen.cpp \
    src/algorithms/Wlan6Keygen.cpp \
    src/algorithms/Wlan2Keygen.cpp \
    src/algorithms/VerizonKeygen.cpp \
    src/algorithms/ThomsonKeygen.cpp \
    src/algorithms/TelseyKeygen.cpp \
    src/algorithms/TecomKeygen.cpp \
    src/algorithms/PirelliKeygen.cpp \
    src/algorithms/Skyv1Keygen.cpp \
    src/algorithms/OnoKeygen.cpp \
    src/algorithms/InfostradaKeygen.cpp \
    src/algorithms/HuaweiKeygen.cpp \
    src/algorithms/EircomKeygen.cpp \
    src/algorithms/DlinkKeygen.cpp \
    src/algorithms/DiscusKeygen.cpp \
    src/algorithms/ComtrendKeygen.cpp \
    src/algorithms/AliceKeygen.cpp \
    src/algorithms/Keygen.cpp \
    src/wifi/QWifiManagerPrivate.cpp \
    src/algorithms/EasyBoxKeygen.cpp \
    src/algorithms/PBSKeygen.cpp \
    src/algorithms/OteKeygen.cpp \
    src/algorithms/OteBAUDKeygen.cpp \
    src/algorithms/MegaredKeygen.cpp \
    src/algorithms/AndaredKeygen.cpp \
    src/algorithms/ConnKeygen.cpp \
    src/wifi/QWifiManager.cpp \
    src/KeygenThread.cpp \
    src/WirelessMatcher.cpp \
    src/division.c \
    src/main.cpp \
    src/sha256.cpp \
    src/algorithms/CabovisaoSagemKeygen.cpp \
    src/config/AliceConfigParser.cpp \
    src/config/AliceMagicInfo.cpp

HEADERS += src/include/ZyxelKeygen.h \
    src/include/Wlan6Keygen.h \
    src/include/Wlan2Keygen.h \
    src/include/VerizonKeygen.h \
    src/include/ThomsonKeygen.h \
    src/include/TelseyKeygen.h \
    src/include/TecomKeygen.h \
    src/include/Skyv1Keygen.h \
    src/include/RouterKeygen.h \
    src/include/PirelliKeygen.h \
    src/include/OnoKeygen.h \
    src/include/InfostradaKeygen.h \
    src/include/HuaweiKeygen.h \
    src/include/EircomKeygen.h \
    src/include/DlinkKeygen.h \
    src/include/DiscusKeygen.h \
    src/include/ComtrendKeygen.h \
    src/include/AliceMagicInfo.h \\
    src/include/AliceConfigParser.h \
    src/include/AliceKeygen.h \
    src/wifi/QWifiManagerPrivate.h \
    src/include/EasyBoxKeygen.h \
    src/include/OteKeygen.h \
    src/include/OteBAUDKeygen.h \
    src/include/PBSKeygen.h \
    src/include/MegaredKeygen.h \
    src/include/AndaredKeygen.h \
    src/include/ConnKeygen.h \
    src/include/QScanResult.h \
    src/include/QWifiManager.h \
    src/include/sha256.h \
    src/include/Keygen.h \
    src/include/KeygenThread.h \
    src/include/WirelessMatcher.h \
    src/include/unknown.h \
    src/include/CabovisaoSagemKeygen.h

INCLUDEPATH += src/include/

win32{
    SOURCES += src/wifi/QWifiManagerPrivateWin.cpp \
        src/sha1/sha1dgst.c \
        src/sha1/sha1-586.win32.S
    HEADERS += src/wifi/QWifiManagerPrivateWin.h \
        src/include/sha_locl.h \
        src/include/sha.h \
        src/include/opensslconf.h \
        src/include/md32_common.h
}
unix:!macx{
    QT += dbus
    SOURCES += src/wifi/QWifiManagerPrivateUnix.cpp
    HEADERS += src/wifi/QWifiManagerPrivateUnix.h
    INCLUDEPATH += /usr/include/NetworkManager
    LIBS += -lcrypto
}


macx{
    SOURCES += src/wifi/QWifiManagerPrivateMac.cpp
    OBJECTIVE_SOURCES += src/mac/macloginitemsmanager.mm
    HEADERS += src/wifi/QWifiManagerPrivateMac.h\
    src/include/macloginitemsmanager.h
    LIBS += -lcrypto -framework Cocoa
}

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
TRANSLATIONS = lang/routerkeygen_pt.ts

HEADERS +=

SOURCES +=
