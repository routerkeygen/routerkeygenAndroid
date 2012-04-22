#ifndef ALICEMAGICINFO_H
#define ALICEMAGICINFO_H
#include <QString>

struct AliceMagicInfo
{
    QString alice;
    int magic[2];
    QString serial;
    QString mac;
    AliceMagicInfo(  QString alice , int magic[2] , QString serial , QString mac );
};

#endif // ALICEMAGICINFO_H
