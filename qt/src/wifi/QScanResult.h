/*
 * QScanResult.h
 *
 *  Created on: 3 de Ago de 2012
 *      Author: ruka
 */

#ifndef QSCANRESULT_H_
#define QSCANRESULT_H_
#include <QString>
#include <QVector>

class WirelessMatcher;
class Keygen;

class QScanResult {
public:
    QScanResult(QString ssid, QString bssid, QString capabilities = "", int freq = 0, int level= 0) ;
    virtual ~QScanResult();
    bool isLocked() const;

    QString getMacAddress() const;
    QString getSsidName() const;
    int getLevel() const;
    int getFrequency() const;
    QString getEncryption() const;
    int getSupportState() const;
    QVector<Keygen *> * getKeygens() const;


    void checkSupport(WirelessMatcher & matcher);
private:
    static QString getScanResultSecurity(const QScanResult * scanResult);

    // Constants used for different security types
    const static QString PSK;
    const static QString WEP;
    const static QString EAP;
    const static QString OPEN;
	QString ssid;
	QString bssid;
	QString capabilities;
	int frequency;
	int level;
    QVector<Keygen*> * keygens;
};

#endif /* QSCANRESULT_H_ */
