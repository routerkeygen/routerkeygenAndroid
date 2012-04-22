#ifndef THOMSONKEYGEN_H
#define THOMSONKEYGEN_H
#include "keygenthread.h"

class ThomsonKeygen : public KeygenThread
{
    private:
        char * table;
        char * entry;
        int len;
        bool thomson3g;
        bool nativeTry;
        bool localCalc();
        void nativeCalc();
    public:
        ThomsonKeygen( WifiNetwork * router , bool thomson3g);
        ~ThomsonKeygen();
        void run();
};

#endif // THOMSONKEYGEN_H
