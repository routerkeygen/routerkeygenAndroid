/*
 * WirelessMatcher.h
 *
 *  Created on: 1 de Ago de 2012
 *      Author: ruka
 */

#ifndef WIRELESSMATCHER_H_
#define WIRELESSMATCHER_H_
#include <QString>
#include "Keygen.h"
#include "AliceMagicInfo.h"
#include "TeleTuMagicInfo.h"
#include <QStringList>
#include <QVector>
#include <QMap>
class WirelessMatcher {
public:
    WirelessMatcher();virtual
    ~WirelessMatcher();
	Keygen * getKeygen(QString ssid, QString mac, int level, QString enc);
private:
    QMap<QString ,QVector<AliceMagicInfo *> *> * supportedAlice;
    QMap<QString ,QVector<TeleTuMagicInfo *> *> * supportedTeletu;
    QStringList * supportedOTE;

};

#endif /* WIRELESSMATCHER_H_ */
