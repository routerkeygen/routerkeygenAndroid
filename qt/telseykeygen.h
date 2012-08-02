#ifndef TELSEYKEYGEN_H
#define TELSEYKEYGEN_H
#include "Keygen.h"
#include <cstring>
#include <stdint.h>

class TelseyKeygen: public Keygen {

public:
	TelseyKeygen(QString & ssid, QString & mac, int level, QString enc);
private:
	QVector<QString> & getKeys();
	unsigned int * scrambler(QString mac);
	uint32_t hashword(const uint32_t * k, size_t length, uint32_t initval);
};

#endif // TELSEYKEYGEN_H
