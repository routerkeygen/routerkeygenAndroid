#ifndef ALICEKEYGEN_H
#define ALICEKEYGEN_H
#include "keygenthread.h"


class AliceKeygen : public KeygenThread
{
    public:
        AliceKeygen(WifiNetwork * router );
        void run();
};

#endif // ALICEKEYGEN_H
