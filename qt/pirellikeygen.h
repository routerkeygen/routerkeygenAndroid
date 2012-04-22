#ifndef PIRELLIKEYGEN_H
#define PIRELLIKEYGEN_H
#include "keygenthread.h"

class PirelliKeygen : public KeygenThread
{
    public:
        PirelliKeygen(WifiNetwork * router );
        void run();
};

#endif // PIRELLIKEYGEN_H
