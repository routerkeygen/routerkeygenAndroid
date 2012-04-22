#ifndef WLAN4KEYGEN_H
#define WLAN4KEYGEN_H
#include "keygenthread.h"

class Wlan4Keygen : public KeygenThread
{
    private:
        QString magic;
    public:
        Wlan4Keygen( WifiNetwork * router );
        void run();
};

#endif // WLAN4KEYGEN_H
