#ifndef OTEHUAWEICONFIGPARSER_H
#define OTEHUAWEICONFIGPARSER_H
#include <QStringList>

class OTEHuaweiConfigParser
{
private:
    OTEHuaweiConfigParser(){}
    ~OTEHuaweiConfigParser(){}
public:
    static QStringList * readFile(const QString &fileName);
};

#endif // OTEHUAWEICONFIGPARSER_H
