/*
 * MAXCOMKEYGEN.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef MAXCOMKEYGEN_H_
#define MAXCOMKEYGEN_H_
#include "Keygen.h"
class MaxcomKeygen : public Keygen{
public:
    MaxcomKeygen(QString & ssid, QString & mac);

private:
	QVector<QString> & getKeys() ;
};

#endif /* MAXCOMKEYGEN_H_ */
