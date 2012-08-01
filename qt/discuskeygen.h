#ifndef DISCUSKEYGEN_H
#define DISCUSKEYGEN_H
#include "Keygen.h"

class DiscusKeygen : public Keygen
{
    public:
        DiscusKeygen(QString & ssid, QString & mac, int level, QString enc);

    private:
    	QVector<QString> & getKeys() ;
};

#endif // DISCUSKEYGEN_H
