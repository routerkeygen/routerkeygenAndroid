/*
 * InterCableKeygen.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef INTERCABLEKEYGEN_H_
#define INTERCABLEKEYGEN_H_

#include "Keygen.h"

class InterCableKeygen: public Keygen {
public:
    InterCableKeygen(QString ssid, QString mac);

private:
	QVector<QString> & getKeys() ;
};

#endif /* INTERCABLEKEYGEN_H_ */
