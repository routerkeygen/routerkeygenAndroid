/*
 * PBSKeygen.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef PBSKEYGEN_H_
#define PBSKEYGEN_H_

#include "Keygen.h"

class PBSKeygen: public Keygen {
public:
	PBSKeygen(QString & ssid, QString & mac, int level, QString enc);

private:
	QVector<QString> & getKeys() ;
	const static QString lookup;
    const static unsigned char saltSHA256[/*32*/];
};

#endif /* PBSKEYGEN_H_ */
