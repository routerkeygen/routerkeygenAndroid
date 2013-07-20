/*
 * ConnKeygen.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef CONNKEYGEN_H_
#define CONNKEYGEN_H_

#include "Keygen.h"

class ConnKeygen: public Keygen {
public:
	ConnKeygen(QString & ssid, QString & mac, int level, QString enc);

private:
	QVector<QString> & getKeys() ;
};

#endif /* CONNKEYGEN_H_ */
