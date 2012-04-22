#ifndef INFOSTRADAKEYGEN_H
#define INFOSTRADAKEYGEN_H
#include "keygenthread.h"

class InfostradaKeygen : public KeygenThread
{
    public:
        InfostradaKeygen(WifiNetwork * router );
        void run();
};

#endif // INFOSTRADAKEYGEN_H
