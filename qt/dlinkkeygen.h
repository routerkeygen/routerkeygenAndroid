#ifndef DLINKKEYGEN_H
#define DLINKKEYGEN_H
#include "keygenthread.h"

class DlinkKeygen : public KeygenThread
{
public:
    DlinkKeygen(WifiNetwork * router );
    void run();
};

#endif // DLINKKEYGEN_H
