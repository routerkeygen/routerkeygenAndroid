/*
 * WirelessMatcher.cpp
 *
 *  Created on: 1 de Ago de 2012
 *      Author: ruka
 */

#include "WirelessMatcher.h"
#include "AliceConfigParser.h"
#include "TeleTuConfigParser.h"
#include "OTEHuaweiConfigParser.h"
#include "TecomKeygen.h"
#include "TeleTuKeygen.h"
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
#include "AxtelKeygen.h"
#include "AndaredKeygen.h"
#include "MegaredKeygen.h"
#include "MaxcomKeygen.h"
#include "InterCableKeygen.h"
#include "OteKeygen.h"
#include "OteBAUDKeygen.h"
#include "OteHuaweiKeygen.h"
#include "PBSKeygen.h"
#include "PtvKeygen.h"
#include "EasyBoxKeygen.h"
#include "CabovisaoSagemKeygen.h"
#include <QRegExp>

WirelessMatcher::WirelessMatcher() {
    supportedAlice = AliceConfigParser::readFile(":/config/alice.txt");
    supportedTeletu = TeleTuConfigParser::readFile(":/config/tele2.txt");
    supportedOTE = OTEHuaweiConfigParser::readFile(":/config/ote_huawei.txt");
}

WirelessMatcher::~WirelessMatcher() {
    QList<QString> keys = supportedAlice->keys();
	for (int i = 0; i < keys.size(); ++i) {
        QVector<AliceMagicInfo *> * supported = supportedAlice->value(
				keys.at(i));
		for (int j = 0; j < supported->size(); ++j)
			delete supported->at(j);
		delete supported;
	}
    supportedAlice->clear();
    delete supportedAlice;
    keys = supportedTeletu->keys();
    for (int i = 0; i < keys.size(); ++i) {
        QVector<TeleTuMagicInfo *> * supported = supportedTeletu->value(
                keys.at(i));
        for (int j = 0; j < supported->size(); ++j)
            delete supported->at(j);
        delete supported;
    }
    supportedTeletu->clear();
    delete supportedTeletu;
    delete supportedOTE;
}

Keygen * WirelessMatcher::getKeygen(QString ssid, QString mac, int level,
		QString enc) {
	//	if (enc.equals(""))
	//	enc = Keygen.OPEN;
    mac = mac.toUpper();

    if (ssid.count(QRegExp("(AXTEL|AXTEL-XTREMO)-[0-9a-fA-F]{4}"))==1) {
        QString ssidSubpart = ssid.right(4);
        QString macShort = mac.replace(":", "");
        if (macShort.length() == 12
                && ( ssidSubpart.toLower() == macShort.right(4).toLower()))
            return new AxtelKeygen(ssid, mac, level, enc);
    }

    if (ssid.startsWith("InterCable") && mac.startsWith("00:15"))
        return new InterCableKeygen(ssid, mac, level, enc);
	if (ssid.count(QRegExp("Discus--?[0-9a-fA-F]{6}")) == 1)
		return new DiscusKeygen(ssid, mac, level, enc);

	if (ssid.count(QRegExp("[eE]ircom[0-7]{4} ?[0-7]{4}")) == 1) {
		if (mac.length() == 0) {
			QString filteredSsid = ssid.replace(" ", "");
			QString end;
			bool status = false;
			int ssidNum = filteredSsid.right(8).toInt(&status, 8) ^ 0x000fcc;
			end.setNum(ssidNum, 16);
			while (end.size() < 6)
				end = "0" + end;
			end = end.toUpper();
			mac = "00:0F:CC:" + end.left(2) + ":" + end.mid(2, 2) + ":"
					+ end.right(2);
		}
		return new EircomKeygen(ssid, mac, level, enc);
	}

	/*
	 * This test MUST be done before the Thomson one because some SSID are
	 * common and this test checks for the MAC addresses
	 */
	if (ssid.count(QRegExp("(Arcor|EasyBox|Vodafone)(-| )[0-9a-fA-F]{6}")) == 1
			&& (mac.startsWith("00:12:BF") || mac.startsWith("00:1A:2A")
					|| mac.startsWith("00:1D:19") || mac.startsWith("00:23:08")
					|| mac.startsWith("00:26:4D") || mac.startsWith("50:7E:5D")
					|| mac.startsWith("1C:C6:3C") || mac.startsWith("74:31:70")
					|| mac.startsWith("7C:4F:B5") || mac.startsWith("88:25:2C")))
		return new EasyBoxKeygen(ssid, mac, level, enc);

	if (ssid.count(
			QRegExp(
					"(Thomson|Blink|SpeedTouch|O2Wireless|Orange-|INFINITUM|BigPond|Otenet|Bbox-|DMAX|privat|TN_private_|CYTA|Vodafone-|Optimus|OptimusFibra|MEO-)[0-9a-fA-F]{6}"))
			== 1)
		return new ThomsonKeygen(ssid, mac, level, enc);

	if (ssid.count(QRegExp("DLink-[0-9a-fA-F]{6}")) == 1)
		return new DlinkKeygen(ssid, mac, level, enc);

	if (ssid.count(
			QRegExp(
					"FASTWEB-1-(000827|0013C8|0017C2|00193E|001CA2|001D8B|002233|00238E|002553|00A02F|080018|3039F2|38229D|6487D7)[0-9A-Fa-f]{6}"))
			== 1) {
		if (mac.length() == 0) {
			QString end = ssid.right(12);
			mac = end.left(2) + ":" + end.mid(2, 2) + ":" + end.mid(4, 2) + ":"
					+ end.mid(6, 2) + ":" + end.mid(8, 2) + ":" + end.right(2);
		}
		return new PirelliKeygen(ssid, mac, level, enc);
	}

	if (ssid.count(QRegExp("FASTWEB-(1|2)-(002196|00036F)[0-9A-Fa-f]{6}"))
			== 1) {
		if (mac.length() == 0) {
			QString end = ssid.right(12);
			mac = end.left(2) + ":" + end.mid(2, 2) + ":" + end.mid(4, 2) + ":"
					+ end.mid(6, 2) + ":" + end.mid(8, 2) + ":" + end.right(2);
		}
		return new TelseyKeygen(ssid, mac, level, enc);
	}
	if (ssid.count(QRegExp("[aA]lice-[0-9]{8}")) == 1) {

        QVector<AliceMagicInfo *> * supported = supportedAlice->value(
                ssid.mid(6,3));
        if (supported != NULL && supported->size() > 0) {
			if (mac.length() < 6)
				mac = supported->at(0)->mac;
			return new AliceKeygen(ssid, mac, level, enc, supported);
		}
	}
    if (ssid.toLower().startsWith("teletu")) {
        QString filteredMac = mac.replace(":", "");
        if (filteredMac.length() != 12 &&
                (ssid.count(QRegExp("TeleTu_[0-9a-fA-F]{12}")) == 1)){
            mac = filteredMac = ssid.mid(7);
        }
        if (filteredMac.length() == 12) {
            QVector<TeleTuMagicInfo *> *  supported = supportedTeletu
                    ->value(filteredMac.left(6));
            if (supported != NULL && supported->size() > 0) {
                int macIntValue = filteredMac.mid(6).toInt(NULL,16);
                for (int i = 0; i < supported->size(); ++i ) {
                    if (macIntValue >= supported->at(i)->range[0]
                            && macIntValue <= supported->at(i)->range[1]) {
                        return new TeleTuKeygen(ssid, mac, level, enc,
                                supported->at(i));
                    }
                }
            }
        }
    }
	/* ssid must be of the form P1XXXXXX0000X or p1XXXXXX0000X */
	if (ssid.count(QRegExp("[Pp]1[0-9]{6}0{4}[0-9]")) == 1)
		return new OnoKeygen(ssid, mac, level, enc);

	if (ssid.count(QRegExp("(WLAN|JAZZTEL)_[0-9a-fA-F]{4}")) == 1) {
		if (mac.startsWith("00:1F:A4") || mac.startsWith("F4:3E:61")
				|| mac.startsWith("40:4A:03"))
			return new ZyxelKeygen(ssid, mac, level, enc);

		if (mac.startsWith("00:1B:20") || mac.startsWith("64:68:0C")
				|| mac.startsWith("00:1D:20") || mac.startsWith("00:23:F8")
				|| mac.startsWith("38:72:C0") || mac.startsWith("30:39:F2"))
			return new ComtrendKeygen(ssid, mac, level, enc);
	}

	if (ssid.count(QRegExp("SKY[0-9]{5}")) == 1
			&& (mac.startsWith("C4:3D:C7") || mac.startsWith("E0:46:9A")
					|| mac.startsWith("E0:91:F5") || mac.startsWith("00:09:5B")
					|| mac.startsWith("00:0F:B5") || mac.startsWith("00:14:6C")
					|| mac.startsWith("00:18:4D") || mac.startsWith("00:26:F2")
					|| mac.startsWith("C0:3F:0E") || mac.startsWith("30:46:9A")
					|| mac.startsWith("00:1B:2F") || mac.startsWith("A0:21:B7")
					|| mac.startsWith("00:1E:2A") || mac.startsWith("00:1F:33")
					|| mac.startsWith("00:22:3F") || mac.startsWith("00:24:B2")))
		return new SkyV1Keygen(ssid, mac, level, enc);

	if (ssid.count(QRegExp("TECOM-AH4(021|222)-[0-9a-zA-Z]{6}")) == 1)
		return new TecomKeygen(ssid, mac, level, enc);

	if (ssid.count(QRegExp("InfostradaWiFi-[0-9a-zA-Z]{6}")) == 1)
		return new InfostradaKeygen(ssid, mac, level, enc);

	if (ssid.startsWith("WLAN_") && ssid.length() == 7
			&& (mac.startsWith("00:01:38") || mac.startsWith("00:16:38")
					|| mac.startsWith("00:01:13") || mac.startsWith("00:01:1B")
					|| mac.startsWith("00:19:5B")))
		return new Wlan2Keygen(ssid, mac, level, enc);

	if (ssid.count(QRegExp("(WLAN|WiFi|YaCom)[0-9a-zA-Z]{6}")) == 1)
		return new Wlan6Keygen(ssid, mac, level, enc);

    if ((ssid.count(QRegExp("OTE[0-9a-fA-F]{4}"))==1) && mac.startsWith("00:13:33"))
        return new OteBAUDKeygen(ssid, mac, level, enc);

	if (ssid.count(QRegExp("OTE[0-9a-fA-F]{6}")) == 1)
		return new OteKeygen(ssid, mac, level, enc);

    if (ssid.toUpper().startsWith("OTE") && (mac.startsWith("E8:39:DF:F5")
       || mac.startsWith("E8:39:DF:F6") || mac.startsWith("E8:39:DF:FD"))) {
        QString filteredMac = mac.replace(":", "");
        int target = filteredMac.mid(8).toInt(NULL, 16);
        if (filteredMac.length() == 12
                && target > (OteHuaweiKeygen::MAGIC_NUMBER - supportedOTE->length()))
            return new OteHuaweiKeygen(ssid, mac, level, enc,
                    supportedOTE->at(OteHuaweiKeygen::MAGIC_NUMBER - target));
    }

    if (ssid.count(QRegExp("MAXCOM[0-9a-zA-Z]{4}")) == 1)
        return new MaxcomKeygen(ssid, mac, level, enc);

	if (ssid.count(QRegExp("PBS-[0-9a-fA-F]{6}")) == 1)
		return new PBSKeygen(ssid, mac, level, enc);

    if (ssid.count(QRegExp("(PTV-|ptv|ptv-)[0-9a-zA-Z]{6}")) == 1)
        return new PtvKeygen(ssid, mac, level, enc);

    if (ssid.count(QRegExp("Cabovisao-[0-9a-fA-F]{4}")) == 1) {
        if (mac.length() == 0 || mac.startsWith("C0:AC:54"))
            return new CabovisaoSagemKeygen(ssid, mac, level, enc);
    }

	if (ssid == "CONN-X")
		return new ConnKeygen(ssid, mac, level, enc);

	if (ssid == "Andared")
		return new AndaredKeygen(ssid, mac, level, enc);

	if (ssid.count(QRegExp("Megared[0-9a-fA-F]{4}")) == 1) {
		// the 4 characters of the SSID should match the final
		if (mac.length() == 0
				|| (ssid.right(4) == mac.replace(":", "").right(4)))
			return new MegaredKeygen(ssid, mac, level, enc);
	}


	if (ssid.length() == 5
			&& (mac.startsWith("00:1F:90") || mac.startsWith("A8:39:44")
					|| mac.startsWith("00:18:01") || mac.startsWith("00:20:E0")
                    || mac.startsWith("00:0F:B3") || mac.startsWith("00:1E:A7")
					|| mac.startsWith("00:15:05") || mac.startsWith("00:24:7B")
					|| mac.startsWith("00:26:62") || mac.startsWith("00:26:B8")))
		return new VerizonKeygen(ssid, mac, level, enc);

	if (ssid.count(QRegExp("INFINITUM[0-9a-zA-Z]{4}")) == 1
			&& (mac.startsWith("00:25:9E") || mac.startsWith("00:25:68")
					|| mac.startsWith("00:22:A1") || mac.startsWith("00:1E:10")
					|| mac.startsWith("00:18:82") || mac.startsWith("00:0F:F2")
					|| mac.startsWith("00:E0:FC") || mac.startsWith("28:6E:D4")
					|| mac.startsWith("54:A5:1B") || mac.startsWith("F4:C7:14")
					|| mac.startsWith("28:5F:DB") || mac.startsWith("30:87:30")
					|| mac.startsWith("4C:54:99") || mac.startsWith("40:4D:8E")
					|| mac.startsWith("64:16:F0") || mac.startsWith("78:1D:BA")
					|| mac.startsWith("84:A8:E4") || mac.startsWith("04:C0:6F")
					|| mac.startsWith("5C:4C:A9") || mac.startsWith("1C:1D:67")
					|| mac.startsWith("CC:96:A0") || mac.startsWith("20:2B:C1")))
		return new HuaweiKeygen(ssid, mac, level, enc);
	return NULL;
}
