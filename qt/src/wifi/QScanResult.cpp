#include "QScanResult.h"
#include "algorithms/Keygen.h"
#include "WirelessMatcher.h"

QScanResult::QScanResult(QString ssid, QString bssid, QString capabilities, int freq,
        int level) :
        ssid(ssid), bssid(bssid.toUpper()), capabilities(capabilities),
        frequency(freq), level(level) , keygens(NULL){
}

QScanResult::~QScanResult(){
    if ( keygens != NULL ){
        for ( int i = 0 ; i < keygens->size(); ++i )
            delete keygens->at(i);
        delete keygens;
    }
}

QString QScanResult::getMacAddress() const {
    return bssid;
}

QString QScanResult::getSsidName() const {
    return ssid;
}

QString QScanResult::getEncryption() const {
    return capabilities;
}

int QScanResult::getLevel() const {
    return level;
}

int QScanResult::getFrequency() const {
    return frequency;
}

QVector<Keygen *> * QScanResult::getKeygens() const {
    return keygens;
}

void QScanResult::checkSupport(WirelessMatcher & matcher){
    keygens = matcher.getKeygens(ssid, bssid);
}

int QScanResult::getSupportState() const{
    if ( keygens == NULL || keygens->size() == 0)
        return Keygen::UNSUPPORTED;
    for ( int i = 0; i < keygens->size(); ++i){
        if ( keygens->at(i)->getSupportState() == Keygen::SUPPORTED )
            return Keygen::SUPPORTED;
    }
    return Keygen::UNLIKELY;
}

const QString QScanResult::PSK = "PSK";
const QString QScanResult::WEP = "WEP";
const QString QScanResult::EAP = "EAP";
const QString QScanResult::OPEN = "Open";

bool QScanResult::isLocked() const{
    return OPEN != getScanResultSecurity(this);
}

/**
 * @return The security of a given {@link ScanResult}.
 */
QString QScanResult::getScanResultSecurity(const QScanResult * scanResult) {
    QString cap = scanResult->capabilities;
    QString securityModes[] = {WEP, PSK, EAP};
    for (int i = sizeof(securityModes)/sizeof(QString)-1; i >= 0; i--) {
        if (cap.contains(securityModes[i])) {
            return securityModes[i];
        }
    }
    return OPEN;
}
