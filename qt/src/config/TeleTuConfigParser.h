#ifndef TELETUHANDLE_H
#define TELETUHANDLE_H
#include "TeleTuMagicInfo.h"
#include <QMap>
#include <QVector>
#include <memory>

class TeleTuConfigParser
{
    private:
        TeleTuConfigParser(){}
        ~TeleTuConfigParser(){}
    public:
        static QMap<QString ,QVector<TeleTuMagicInfo *> *> * readFile(const QString &fileName);


};

#endif // TELETUHANDLE_H
