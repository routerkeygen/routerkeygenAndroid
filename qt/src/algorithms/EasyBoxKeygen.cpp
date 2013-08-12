/*
 * EasyBox.cpp
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#include "EasyBoxKeygen.h"
#include <QRegExp>

EasyBoxKeygen::EasyBoxKeygen(QString & ssid, QString & mac, int level,
		QString enc) :
		Keygen(ssid, mac, level, enc) {
}

int EasyBoxKeygen::getSupportState() const{
    if ( getSsidName().count(QRegExp("^(Arcor|EasyBox|Vodafone|WLAN)(-| )[0-9a-fA-F]{6}$")) == 1 )
        return SUPPORTED;
    return UNLIKELY;
}
QVector<QString> & EasyBoxKeygen::getKeys() {
	QString mac = getMacAddress();
	if (mac.length() != 12) {
		throw ERROR;
	}
	bool status;
	QString C1 = "";
	C1.setNum(mac.right(4).toInt(&status, 16),10);

	while (C1.length() < 5)
		C1 = "0" + C1;

	unsigned char S7 = C1.mid(1,1).toInt(&status, 16);
	unsigned char S8 = C1.mid(2,1).toInt(&status, 16);
	unsigned char S9 = C1.mid(3,1).toInt(&status, 16);
	unsigned char S10 = C1.mid(4,1).toInt(&status, 16);
	unsigned char M9 = mac.mid(8,1).toInt(&status, 16);
	unsigned char M10 = mac.mid(9,1).toInt(&status, 16);
	unsigned char M11 = mac.mid(10,1).toInt(&status, 16);
	unsigned char M12 = mac.mid(11,1).toInt(&status, 16);
	if ( !status  )
		throw ERROR;

    unsigned int K1 = (S7 + S8 + M11 + M12) & 0x0f;
    unsigned int K2 = (M9 + M10 +S9 + S10) & 0x0f;

	QString X1;X1.setNum(K1 ^ S10, 16);
	QString X2;X2.setNum(K1 ^ S9, 16);
	QString X3;X3.setNum(K1 ^ S8, 16);
	QString Y1;Y1.setNum(K2 ^ M10, 16);
	QString Y2;Y2.setNum(K2 ^ M11, 16);
	QString Y3;Y3.setNum(K2 ^ M12, 16);
	QString Z1;Z1.setNum(M11 ^ S10, 16);
	QString Z2;Z2.setNum(M12 ^ S9, 16);
	QString Z3;Z3.setNum(K1 ^ K2, 16);

	QString wpaKey = X1 + Y1 + Z1 + X2 + Y2 + Z2 + X3 + Y3 + Z3;
	results.append(wpaKey.toUpper());
	return results;
}

