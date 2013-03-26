#include "OteHuaweiKeygen.h"
#include <QStringList>

OteHuaweiKeygen::OteHuaweiKeygen(QString & ssid, QString & mac, int level, QString enc,
        QString supported) :
        Keygen(ssid, mac, level, enc), magicValues(supported) {

}

const int OteHuaweiKeygen::MAGIC_NUMBER = 65535;

QVector<QString> & OteHuaweiKeygen::getKeys() {
    QString mac = getMacAddress();
    if (mac.length() != 12) {
        throw ERROR;
    }
    QStringList magic = magicValues.split(" ");
    QString series = mac.left(2) + mac.mid(6, 2);
    int point;
    if (series == "E8FD" )
        point = 0;
    else if (series == "E8F5")
        point = 1;
    else if (series == "E8F6")
        point = 2;
    else
        return results;
    if (point >= magic.length())
        return results;
    QString pass = "000000" + magic.at(point);
    results.append(pass.mid(pass.length() - 8));
    return results;
}
