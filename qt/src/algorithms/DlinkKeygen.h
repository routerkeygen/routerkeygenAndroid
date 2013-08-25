#ifndef DLINKKEYGEN_H
#define DLINKKEYGEN_H
#include "Keygen.h"

class DlinkKeygen: public Keygen {
public:
	DlinkKeygen(QString ssid, QString mac);
private:
	QVector<QString> & getKeys() ;
	static char hash[];
};

#endif // DLINKKEYGEN_H
