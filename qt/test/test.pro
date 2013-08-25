QT  += core \
    testlib
QT -= gui
TARGET = RouterKeygenTest
TEMPLATE = app
SOURCES += \
    ../src/algorithms/*.cpp \
    ../src/WirelessMatcher.cpp \
    ../src/sha/sha256.cpp\
    ../src/config/*.cpp \
    ../src/wifi/QScanResult.cpp \
    AlgorithmsTest.cpp

HEADERS += ../src/algorithms/*.h \
    ../src/config/*.h \
    ../src/sha/sha256.h \
    ../src/wifi/QScanResult.h \
    ../src/WirelessMatcher.h

INCLUDEPATH += ../src/
win32{
    SOURCES += ../src/sha/sha1dgst.c \
        ../src/sha/sha1-586.win32.S
    HEADERS += src/sha/sha_locl.h \
        ../src/sha/sha.h \
        ../src/sha/opensslconf.h \
        ../src/sha/md32_common.h
}
unix{
    LIBS += -lcrypto
}


RESOURCES += ../resources/resources.qrc

