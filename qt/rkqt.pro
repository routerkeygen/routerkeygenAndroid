# -------------------------------------------------
# Project created by QtCreator 2011-04-12T00:20:13
# -------------------------------------------------
QT += core \
    gui \
    xml
TARGET = RouterKeygen
TEMPLATE = app
SOURCES += src/algorithms/dlinkkeygen.cpp \
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
HEADERS += src/include/onokeygen.h \
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
FORMS += forms/routerkeygen.ui
LIBS += -lcrypto
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
