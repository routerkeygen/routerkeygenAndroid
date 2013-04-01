QT  += core \
    testlib
QT -= gui
TARGET = RouterKeygenTest
TEMPLATE = app
SOURCES += \
    ../src/algorithms/AxtelKeygen.cpp \
    ../src/algorithms/ZyxelKeygen.cpp \
    ../src/algorithms/Wlan6Keygen.cpp \
    ../src/algorithms/Wlan2Keygen.cpp \
    ../src/algorithms/VerizonKeygen.cpp \
    ../src/algorithms/ThomsonKeygen.cpp \
    ../src/algorithms/TelseyKeygen.cpp \
    ../src/algorithms/TecomKeygen.cpp \
    ../src/algorithms/TeletuKeygen.cpp \
    ../src/algorithms/PirelliKeygen.cpp \
    ../src/algorithms/Skyv1Keygen.cpp \
    ../src/algorithms/OnoKeygen.cpp \
    ../src/algorithms/InfostradaKeygen.cpp \
    ../src/algorithms/InterCableKeygen.cpp \
    ../src/algorithms/HuaweiKeygen.cpp \
    ../src/algorithms/EircomKeygen.cpp \
    ../src/algorithms/DlinkKeygen.cpp \
    ../src/algorithms/DiscusKeygen.cpp \
    ../src/algorithms/ComtrendKeygen.cpp \
    ../src/algorithms/AliceKeygen.cpp \
    ../src/algorithms/Keygen.cpp \
    ../src/algorithms/EasyBoxKeygen.cpp \
    ../src/algorithms/PBSKeygen.cpp \
    ../src/algorithms/PtvKeygen.cpp \
    ../src/algorithms/OteKeygen.cpp \
    ../src/algorithms/OteBAUDKeygen.cpp \
    ../src/algorithms/MegaredKeygen.cpp \
    ../src/algorithms/MaxcomKeygen.cpp \
    ../src/algorithms/AndaredKeygen.cpp \
    ../src/algorithms/ConnKeygen.cpp \
    ../src/algorithms/CabovisaoSagemKeygen.cpp \
    ../src/WirelessMatcher.cpp \
    ../src/division.c \
    ../src/sha256.cpp\
    ../src/config/AliceConfigParser.cpp \
    ../src/config/AliceMagicInfo.cpp \
    ../src/config/TeleTuConfigParser.cpp \
    ../src/config/TeleTuMagicInfo.cpp \
    ../src/algorithms/WifimediaRKeygen.cpp \
    ../src/config/OTEHuaweiConfigParser.cpp \
    ../src/algorithms/OteHuaweiKeygen.cpp \
    AlgorithmsTest.cpp

HEADERS += ../src/include/ZyxelKeygen.h \
    ../src/include/Wlan6Keygen.h \
    ../src/include/Wlan2Keygen.h \
    ../src/include/VerizonKeygen.h \
    ../src/include/ThomsonKeygen.h \
    ../src/include/TelseyKeygen.h \
    ../src/include/TecomKeygen.h \
    ../src/include/TeletuKeygen.h \
    ../src/include/Skyv1Keygen.h \
    ../src/include/PirelliKeygen.h \
    ../src/include/OnoKeygen.h \
    ../src/include/InfostradaKeygen.h \
    ../src/include/HuaweiKeygen.h \
    ../src/include/EircomKeygen.h \
    ../src/include/DlinkKeygen.h \
    ../src/include/DiscusKeygen.h \
    ../src/include/ComtrendKeygen.h \
    ../src/include/AliceMagicInfo.h \
    ../src/include/AliceConfigParser.h \
    ../src/include/TeleTuConfigParser.h \
    ../src/include/TeleTuMagicInfo.h \
    ../src/include/AliceKeygen.h \
    ../src/include/AxtelKeygen.h \
    ../src/include/EasyBoxKeygen.h \
    ../src/include/OteKeygen.h \
    ../src/include/OteBAUDKeygen.h \
    ../src/include/PBSKeygen.h \
    ../src/include/PtvKeygen.h \
    ../src/include/MegaredKeygen.h \
    ../src/include/MaxcomKeygen.h \
    ../src/include/AndaredKeygen.h \
    ../src/include/ConnKeygen.h \
    ../src/include/sha256.h \
    ../src/include/Keygen.h \
    ../src/include/WirelessMatcher.h \
    ../src/include/unknown.h \
    ../src/include/CabovisaoSagemKeygen.h \
    ../src/include/WifimediaRKeygen.h \
    ../src/include/OTEHuaweiConfigParser.h \
    ../src/include/OteHuaweiKeygen.h

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

