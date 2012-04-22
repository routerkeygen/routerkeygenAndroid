#ifndef WLAN6KEYGEN_H
#define WLAN6KEYGEN_H
#include "keygenthread.h"

class Wlan6Keygen : public KeygenThread
{
    public:
        Wlan6Keygen( WifiNetwork * router);
        void run();
};

#endif // WLAN6KEYGEN_H
