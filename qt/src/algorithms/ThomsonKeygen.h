#ifndef THOMSONKEYGEN_H
#define THOMSONKEYGEN_H
#include "Keygen.h"
#include <QCryptographicHash>
class ThomsonKeygen: public Keygen {

public:
	ThomsonKeygen(QString & ssid, QString & mac);
    int getSupportState() const;
	~ThomsonKeygen(){}
private:
	QVector<QString> & getKeys();

};

#endif // THOMSONKEYGEN_H
