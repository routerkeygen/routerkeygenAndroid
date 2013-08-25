/*
 * WIFIMEDIARKEYGEN.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef WIFIMEDIARKEYGEN_H_
#define WIFIMEDIARKEYGEN_H_
#include "Keygen.h"
class WifimediaRKeygen : public Keygen{
public:
    WifimediaRKeygen(QString ssid, QString mac);

private:
	QVector<QString> & getKeys() ;
};

#endif /* WIFIMEDIARKEYGEN_H_ */
