#ifndef HUAWEIKEYGEN_H
#define HUAWEIKEYGEN_H
#include "keygenthread.h"

class HuaweiKeygen : public KeygenThread
{
    public:
        HuaweiKeygen(WifiNetwork * router );
        void run();
};

#endif // HUAWEIKEYGEN_H
