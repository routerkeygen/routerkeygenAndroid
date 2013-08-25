/*
 * AndaredKeygen.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef ANDAREDKEYGEN_H_
#define ANDAREDKEYGEN_H_

#include "Keygen.h"

class AndaredKeygen: public Keygen {
public:
	AndaredKeygen(QString ssid, QString mac);

private:
	QVector<QString> & getKeys() ;
};

#endif /* ANDAREDKEYGEN_H_ */
