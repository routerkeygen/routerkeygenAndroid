#ifndef TECOMKEYGEN_H
#define TECOMKEYGEN_H
#include "keygenthread.h"

class TecomKeygen : public KeygenThread
{
    public:
        TecomKeygen( WifiNetwork * router );
        void run();
};

#endif // TECOMKEYGEN_H
