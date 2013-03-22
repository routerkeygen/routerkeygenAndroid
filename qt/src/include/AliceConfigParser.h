#ifndef ALICEHANDLE_H
#define ALICEHANDLE_H
#include "AliceMagicInfo.h"
#include <QMap>
#include <QVector>
#include <memory>

class AliceConfigParser
{
    private:
        AliceConfigParser(){}
        ~AliceConfigParser(){}
    public:
        static QMap<QString ,QVector<AliceMagicInfo *> *> * readFile(const QString &fileName);


};

#endif // ALICEHANDLE_H
