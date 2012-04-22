#ifndef TELSEYKEYGEN_H
#define TELSEYKEYGEN_H
#include "keygenthread.h"
#include <stdint.h>

class TelseyKeygen : public KeygenThread
{
    private:
        unsigned int * scrambler(QString mac);
        uint32_t hashword(const uint32_t * k, size_t length,  uint32_t initval);
    public:
        TelseyKeygen( WifiNetwork * router );
        void run();
};

#endif // TELSEYKEYGEN_H
