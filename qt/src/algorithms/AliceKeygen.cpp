/*
 * Copyright 2012 Rui Araújo, Luís Fonseca
 *
 * This file is part of Router Keygen.
 *
 * Router Keygen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Router Keygen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Router Keygen.  If not, see <http://www.gnu.org/licenses/>.
 */
#include "AliceKeygen.h"
#include "config/AliceMagicInfo.h"
#include "sha/sha256.h"
AliceItalyKeygen::AliceItalyKeygen(QString & ssid, QString & mac, int level, QString enc,
		QVector<AliceMagicInfo *> * supported) :
		Keygen(ssid, mac, level, enc), supportedAlice(supported) {

}

const QString AliceItalyKeygen::preInitCharset =
		"0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvWxyz0123";
const unsigned char AliceItalyKeygen::specialSeq[/*32*/] = { 0x64, 0xC6, 0xDD, 0xE3, 0xE5,
		0x79, 0xB6, 0xD9, 0x86, 0x96, 0x8D, 0x34, 0x45, 0xD2, 0x3B, 0x15, 0xCA,
		0xAF, 0x12, 0x84, 0x02, 0xAC, 0x56, 0x00, 0x05, 0xCE, 0x20, 0x75, 0x91,
		0x3F, 0xDC, 0xE8 };

QVector<QString> & AliceItalyKeygen::getKeys() {

	if (supportedAlice->isEmpty())
		throw ERROR;
	SHA256 sha;
	char hash[32];

	bool status;

	for (int j = 0; j < supportedAlice->size(); ++j) {/*For pre AGPF 4.5.0sx*/
		QString serialStr = supportedAlice->at(j)->serial + "X";
        int k = supportedAlice->at(j)->magic[0];
        int Q = supportedAlice->at(j)->magic[1];
		int serial = (getSsidName().right(8).toInt(&status, 10) - Q) / k;
		QString tmp = "";
		tmp.setNum(serial);
		for (int i = 0; i < 7 - tmp.length(); i++) {
			serialStr += "0";
		}
		serialStr += tmp;

		char mac[6];
		QString key = "";

		QString macS = getMacAddress();
		if (macS.size() == 12) {

			for (int i = 0; i < 12; i += 2)
				mac[i / 2] = (macS.mid(i, 1).toInt(&status, 16) << 4)
						+ macS.mid(i + 1, 1).toInt(&status, 16);

			/* Compute the hash */
			sha.reset();
			sha.addData(specialSeq, (unsigned long) sizeof(specialSeq));
            sha.addData(serialStr.toLatin1().data(), serialStr.size());
			sha.addData(mac, (unsigned long) sizeof(mac));
			sha.result((unsigned char *) hash);

			for (int i = 0; i < 24; ++i) {
				key += preInitCharset.at(hash[i] & 0xFF);
			}
			if (!results.contains(key))
				results.append(key);
		}

		/*For post AGPF 4.5.0sx*/
		QString macEth = macS.left(6);
        for (int extraNumber = 0;extraNumber < 10; ++extraNumber) {
			QString calc = "";
			calc.setNum(extraNumber);
			calc += getSsidName().right(8);
			calc.setNum(calc.toInt(&status, 10), 16);
			calc = calc.toUpper();
			if (macEth.at(5) == calc.at(0)) {
				macEth += calc.right(6);
				break;
            }
		}

		for (int i = 0; i < 12; i += 2)
			mac[i / 2] = (macEth.mid(i, 1).toInt(&status, 16) << 4)
					+ macEth.mid(i + 1, 1).toInt(&status, 16);
		/* Compute the hash */
		sha.reset();
		sha.addData(specialSeq, (unsigned long) sizeof(specialSeq));
        sha.addData(serialStr.toLatin1().data(), serialStr.size());
		sha.addData(mac, (unsigned long) sizeof(mac));
		sha.result((unsigned char *) hash);

		key = "";
		for (int i = 0; i < 24; ++i)
			key += preInitCharset.at(hash[i] & 0xFF);
		if (!results.contains(key))
			results.append(key);
	}
	return results;

}
