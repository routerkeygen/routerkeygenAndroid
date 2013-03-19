#include <QtTest/QtTest>
#include <typeinfo>
#include <QString>
#include <QVector>
#include "TecomKeygen.h"
#include "ThomsonKeygen.h"
#include "VerizonKeygen.h"
#include "InfostradaKeygen.h"
#include "EircomKeygen.h"
#include "Skyv1Keygen.h"
#include "Wlan2Keygen.h"
#include "ComtrendKeygen.h"
#include "ZyxelKeygen.h"
#include "Wlan6Keygen.h"
#include "DiscusKeygen.h"
#include "DlinkKeygen.h"
#include "PirelliKeygen.h"
#include "TelseyKeygen.h"
#include "OnoKeygen.h"
#include "HuaweiKeygen.h"
#include "AliceKeygen.h"
#include "ConnKeygen.h"
#include "AndaredKeygen.h"
#include "MegaredKeygen.h"
#include "OteKeygen.h"
#include "OteBAUDKeygen.h"
#include "PBSKeygen.h"
#include "EasyBoxKeygen.h"
#include "CabovisaoSagemKeygen.h"
#include "WirelessMatcher.h"
#include <QDebug>

class AlgorithmsTest: public QObject
{

    Q_OBJECT
    WirelessMatcher matcher;
private slots:
       void testDiscus() {
            Keygen * keygen = matcher.getKeygen("Discus--DA1CC5", "00:1C:A2:DA:1C:C5", 0, "");
            QCOMPARE(typeid(*keygen), typeid(DiscusKeygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE(results.size(),1);
            QCOMPARE(results.at(0),QString("YW0150565"));
        }


        void testHuawei() {
            Keygen * keygen = matcher.getKeygen("INFINITUM1be2", "64:16:F0:35:1C:FD", 0, "");
            QCOMPARE(typeid(*keygen), typeid(HuaweiKeygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE(results.size(),1);
            QCOMPARE(results.at(0),QString("3432333133"));
        }


        void testDlink() {
            Keygen * keygen = matcher.getKeygen("DLink-123456", "12:34:56:78:9a:bc", 0, "");
            QCOMPARE(typeid(*keygen), typeid(DlinkKeygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE(results.size(),1);
            QCOMPARE( results.at(0),QString("6r8qwaYHSNdpqdYw6aN8"));
        }


        void testEircom() {
            Keygen * keygen = matcher.getKeygen("eircom2633 7520", "00:0f:cc:59:b0:9c", 0, "");
            QCOMPARE(typeid(*keygen), typeid(EircomKeygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE(results.size(),1);
            QCOMPARE( results.at(0),QString("29b2e9560b3a83a187ec5f2057"));
        }


        void testAlice() {
            Keygen * keygen = matcher.getKeygen("Alice-37588990", "00:23:8e:48:e7:d4", 0, "");
            QCOMPARE(typeid(*keygen), typeid(AliceKeygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE(results.size(),4);
            QCOMPARE(results.at(0),QString("djfveeeqyasxhhcqar8ypkcv"));
            QCOMPARE(results.at(1),QString("fsvcl1ujd3coikm49qowthn8"));
            QCOMPARE(results.at(2),QString("y7xysqmqs9jooa7rersi7ayi"));
            QCOMPARE(results.at(3),QString("9j4hm3ojq4brfdy6wcsuglwu"));
        }

        void testEasyBox() {
            Keygen * keygen =  matcher.getKeygen("Arcor-910B02", "00:12:BF:91:0B:EC", 0, "");
            QCOMPARE(typeid(*keygen), typeid(EasyBoxKeygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE(results.size(),1);
            QCOMPARE(results.at(0),QString("F9C8C9DEF"));
        }

        void testOTE() {
            Keygen * keygen = matcher.getKeygen("OTE37cb4c", "B0:75:D5:37:CB:4C", 0, "");
            QCOMPARE(typeid(*keygen), typeid(OteKeygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE(results.size(),1);
            QCOMPARE( results.at(0),QString("b075d537cb4c"));
        }


        void testOTEBAUD() {
            Keygen * keygen = matcher.getKeygen("OTEcb4c", "00:13:33:37:CB:4C", 0, "");
            QCOMPARE(typeid(*keygen), typeid(OteBAUDKeygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE(results.size(),1);
            QCOMPARE(results.at(0),QString("000133337cb4c"));
        }



        void testCONN() {
            Keygen * keygen = matcher.getKeygen("CONN-X", "", 0, "");
            QCOMPARE(typeid(*keygen), typeid(ConnKeygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE(results.size(),1);
            QCOMPARE(results.at(0),QString("1234567890123"));
        }



        void testPBS() {
            Keygen * keygen = matcher.getKeygen("PBS-11222E", "38:22:9D:11:22:2E", 0, "");
            QCOMPARE(typeid(*keygen), typeid(PBSKeygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE(results.size(),1);
            QCOMPARE( results.at(0),QString("PcL2PgUcX0VhV"));
        }



        void testMegared() {
            Keygen * keygen = matcher.getKeygen("Megared60EC", "FC:75:16:9F:60:EC", 0, "");
            QCOMPARE(typeid(*keygen), typeid(MegaredKeygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE(results.size(),1);
            QCOMPARE(results.at(0), QString("75169F60EC"));
        }


        void testLWAN6X() {
            Keygen * keygen = matcher.getKeygen("WLAN123456", "11:22:33:44:55:66", 0, "");
            QCOMPARE(typeid(*keygen), typeid(Wlan6Keygen) );
            QVector<QString> results = keygen->getResults();
            QCOMPARE( results.size(),10);
            QCOMPARE(results.at(0), QString("5630556304607"));
            QCOMPARE(results.at(1), QString("5730446305616"));
            QCOMPARE(results.at(2), QString("5430776306625"));
            QCOMPARE(results.at(3), QString("5530666307634"));
            QCOMPARE(results.at(4), QString("5230116300643"));
            QCOMPARE(results.at(5), QString("5330006301652"));
            QCOMPARE(results.at(6), QString("5030336302661"));
            QCOMPARE(results.at(7), QString("5130226303670"));
            QCOMPARE(results.at(8), QString("5E30DD630C68F"));
            QCOMPARE(results.at(9), QString("5F30CC630D69E"));
        }

};


QTEST_MAIN(AlgorithmsTest)
#include "AlgorithmsTest.moc"
