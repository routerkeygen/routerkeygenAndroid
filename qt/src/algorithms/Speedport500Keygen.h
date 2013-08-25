#ifndef SPEEDPORT500KEYGEN_H
#define SPEEDPORT500KEYGEN_H
#include "Keygen.h"

class Speedport500Keygen :public Keygen

{
public:
    Speedport500Keygen(QString ssid, QString mac);
private:
    QVector<QString> & getKeys();
};

#endif // SPEEDPORT500KEYGEN_H
