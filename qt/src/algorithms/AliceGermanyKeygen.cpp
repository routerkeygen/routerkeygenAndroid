#include "AliceGermanyKeygen.h"
#include <QRegExp>

AliceGermanyKeygen::AliceGermanyKeygen(QString ssid, QString mac) :
        Keygen(ssid, mac) {
    this->hash = new QCryptographicHash(QCryptographicHash::Md5);
}
AliceGermanyKeygen::~AliceGermanyKeygen(){
    delete hash;
}
int AliceGermanyKeygen::getSupportState() const{
    if ( getSsidName().count(QRegExp("^ALICE-WLAN[0-9a-fA-F]{2}$")) == 1 )
        return SUPPORTED;
    return UNLIKELY;
}

QVector<QString> & AliceGermanyKeygen::getKeys() {
    QString mac = getMacAddress();
    if (mac.size() != 12)
        throw ERROR;
    QString macEth = mac.right(6);
    int macEthInt = macEth.toInt(NULL, 16)-1;
    if ( macEthInt < 0 )
        macEthInt = 0xFFFFFF;
    macEth.setNum(macEthInt, 16);
    while ( macEth.size() < 6 )
        macEth = "0"+ macEth;
    macEth = mac.left(6) + macEth;
    this->hash->reset();
    this->hash->addData(macEth.toLower().toLatin1());
    QString result = QString::fromLatin1(this->hash->result().toHex().data());
    result.truncate(12);
    this->results.append(QString(result.toLatin1().toBase64()));
    return results;
}
