#ifndef ONOKEYGEN_H
#define ONOKEYGEN_H
#include "Keygen.h"

class OnoKeygen : public Keygen
{
    public:
        OnoKeygen(QString & ssid, QString & mac, int level, QString enc);
    private:
    	QVector<QString> & getKeys();
        QString padto64( QString val );
};

#endif // ONOKEYGEN_H
