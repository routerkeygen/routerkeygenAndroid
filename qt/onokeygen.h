#ifndef ONOKEYGEN_H
#define ONOKEYGEN_H
#include "keygenthread.h"

class OnoKeygen : public KeygenThread
{
    private:
        QString padto64( QString val );
    public:
        OnoKeygen(WifiNetwork * router );
        void run();
};

#endif // ONOKEYGEN_H
