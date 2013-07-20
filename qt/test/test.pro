QT  += core \
    testlib
QT -= gui
TARGET = RouterKeygenTest
TEMPLATE = app
SOURCES += \
    ../src/algorithms/*.cpp \
    ../src/WirelessMatcher.cpp \
    ../src/division.c \
    ../src/sha256.cpp\
    ../src/config/*.cpp \
    AlgorithmsTest.cpp

HEADERS += ../src/include/*Keygen.h \
    ../src/include/*MagicInfo.h \
    ../src/include/*ConfigParser.h \
    ../src/include/sha256.h \
    ../src/include/WirelessMatcher.h \
    ../src/include/unknown.h

INCLUDEPATH += ../src/include/
win32{
    SOURCES += ../src/sha1/sha1dgst.c \
        ../src/sha1/sha1-586.win32.S
    HEADERS += src/include/sha_locl.h \
        ../src/include/sha.h \
        ../src/include/opensslconf.h \
        ../src/include/md32_common.h
}
unix{
    LIBS += -lcrypto
}


RESOURCES += ../resources/resources.qrc

