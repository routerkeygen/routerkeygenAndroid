/*
 * OteBAUDKeygen.h
 *
 *  Created on: 5 de Ago de 2012
 *      Author: ruka
 */

#ifndef OTEBAUDKEYGEN_H_
#define OTEBAUDKEYGEN_H_

#include "Keygen.h"

class OteBAUDKeygen: public Keygen {
public:
    OteBAUDKeygen(QString & ssid, QString & mac, int level, QString enc);

private:
	QVector<QString> & getKeys() ;
};

#endif /* OteBAUDKeygen_H_ */
