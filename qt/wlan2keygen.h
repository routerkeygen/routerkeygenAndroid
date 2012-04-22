#ifndef WLAN2KEYGEN_H
#define WLAN2KEYGEN_H
#include "keygenthread.h"

class Wlan2Keygen : public KeygenThread
{
    public:
        Wlan2Keygen( WifiNetwork * router );
        void run();
};

#endif // WLAN2KEYGEN_H
