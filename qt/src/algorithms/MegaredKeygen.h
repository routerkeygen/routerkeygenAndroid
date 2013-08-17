/*
 * MegaredKeygen.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef MEGAREDKEYGEN_H_
#define MEGAREDKEYGEN_H_
#include "Keygen.h"
class MegaredKeygen : public Keygen{
public:
	MegaredKeygen(QString & ssid, QString & mac);

private:
	QVector<QString> & getKeys() ;
};

#endif /* MEGAREDKEYGEN_H_ */
