#ifndef SKYV1KEYGEN_H
#define SKYV1KEYGEN_H
#include "keygenthread.h"

class SkyV1Keygen : public KeygenThread
{
    private:
        QString ALPHABET;

    public:
        SkyV1Keygen( WifiNetwork * router );
        void run();
};

#endif // SKYV1KEYGEN_H
