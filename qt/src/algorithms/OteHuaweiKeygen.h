#ifndef OTEHUAWEIKEYGEN_H
#define OTEHUAWEIKEYGEN_H
#include "Keygen.h"

class OteHuaweiKeygen : public Keygen
{
public:
    OteHuaweiKeygen(QString & ssid, QString & mac,  QString magicValues);
    const static int MAGIC_NUMBER;
private:
    QVector<QString> & getKeys();
    QString magicValues;

};

#endif // OTEHUAWEIKEYGEN_H
