/*
 * EasyBox.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef EASYBOX_H_
#define EASYBOX_H_

#include "Keygen.h"

class EasyBoxKeygen: public Keygen {
public:
	EasyBoxKeygen(QString & ssid, QString & mac, int level, QString enc);
    int getSupportState() const;
private:
	QVector<QString> & getKeys() ;
};

#endif /* EASYBOX_H_ */
