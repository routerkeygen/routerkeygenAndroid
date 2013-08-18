#ifndef DISCUSKEYGEN_H
#define DISCUSKEYGEN_H
#include "Keygen.h"

class DiscusKeygen : public Keygen
{
    public:
        DiscusKeygen(QString & ssid, QString & mac);

    private:
    	QVector<QString> & getKeys() ;
};

#endif // DISCUSKEYGEN_H
