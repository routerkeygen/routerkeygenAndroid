#ifndef TELETUMAGICINFO_H
#define TELETUMAGICINFO_H
#include <QString>

struct TeleTuMagicInfo{
    int range[2];
    int base;
    QString serial;
    int divider;
    TeleTuMagicInfo( int range[2], QString serial, int base, int divider);
};

#endif // TELETUMAGICINFO_H
