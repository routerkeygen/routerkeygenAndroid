#ifndef KEYGENTHREAD_H
#define KEYGENTHREAD_H
#include <QCryptographicHash>
#include <QThread>
#include <QVector>
#include "wifinetwork.h"

class KeygenThread : public QThread
{
    public:
        KeygenThread( WifiNetwork * router );
        QVector<QString> getResults() const;
        void stop();
        bool isStopped();
    protected:
        QCryptographicHash * hash;
        QVector<QString> results;
        WifiNetwork * router;
        bool stopRequested;


};

#endif // KEYGENTHREAD_H
