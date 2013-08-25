#ifndef ALICEGERMANYKEYGEN_H
#define ALICEGERMANYKEYGEN_H
#include <QCryptographicHash>
#include "Keygen.h"

class AliceGermanyKeygen : public Keygen
{
public:
    AliceGermanyKeygen(QString ssid, QString mac);
    ~AliceGermanyKeygen();
    int getSupportState() const;
private:
    QVector<QString> & getKeys();
    QCryptographicHash * hash;
};

#endif // ALICEGERMANYKEYGEN_H
