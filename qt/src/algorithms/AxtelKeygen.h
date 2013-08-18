/*
 * AxtelKeygen.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef AXTELKEYGEN_H_
#define AXTELKEYGEN_H_

#include "Keygen.h"

class AxtelKeygen: public Keygen {
public:
    AxtelKeygen(QString & ssid, QString & mac);

private:
	QVector<QString> & getKeys() ;
};

#endif /* AXTELKEYGEN_H_ */
