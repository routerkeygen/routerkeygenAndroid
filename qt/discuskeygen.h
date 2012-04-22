#ifndef DISCUSKEYGEN_H
#define DISCUSKEYGEN_H
#include "keygenthread.h"

class DiscusKeygen : public KeygenThread
{
    public:
        DiscusKeygen(WifiNetwork * router );
        void run();
};

#endif // DISCUSKEYGEN_H
