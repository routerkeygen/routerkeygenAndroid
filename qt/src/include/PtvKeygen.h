/*
 * PTVKEYGEN.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef PTVKEYGEN_H_
#define PTVKEYGEN_H_
#include "Keygen.h"
class PtvKeygen : public Keygen{
public:
    PtvKeygen(QString & ssid, QString & mac, int level, QString enc);

private:
	QVector<QString> & getKeys() ;
};

#endif /* PTVKEYGEN_H_ */
