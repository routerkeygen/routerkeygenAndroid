/*
 * OteKeygen.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef OTEKEYGEN_H_
#define OTEKEYGEN_H_

#include "Keygen.h"

class OteKeygen: public Keygen {
public:
	OteKeygen(QString & ssid, QString & mac);

private:
	QVector<QString> & getKeys() ;
};

#endif /* OTEKEYGEN_H_ */
