#include "Speedport500Keygen.h"

Speedport500Keygen::Speedport500Keygen(QString ssid, QString mac) :
        Keygen(ssid, mac) {}

QVector<QString> & Speedport500Keygen::getKeys() {
    QString mac = getMacAddress();
    if (mac.size() != 12)
        throw ERROR;
    QString ssid = getSsidName();
    QString block = ssid.at(10) + mac.right(3);
    QString beginning = "SP-";
    beginning += ssid.at(9);
    for (char x = '0'; x <= '9'; ++x)
        for (char y = '0'; y <= '9'; ++y)
            for (char z = '0'; z <= '9'; ++z)
                this->results.append(beginning + z + block + x +y + z );
    return results;
}
