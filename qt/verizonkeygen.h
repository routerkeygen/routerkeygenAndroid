#ifndef VERIZONKEYGEN_H
#define VERIZONKEYGEN_H
#include "keygenthread.h"

class VerizonKeygen : public KeygenThread
{
    public:
        VerizonKeygen( WifiNetwork * router );
        void run();

};

#endif // VERIZONKEYGEN_H
