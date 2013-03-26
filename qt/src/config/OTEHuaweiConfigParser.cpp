#include "OTEHuaweiConfigParser.h"
#include <QFile>
#include <QTextStream>
#include <QString>


QStringList * OTEHuaweiConfigParser::readFile(const QString &fileName) {
    QFile file(fileName);
    if(!file.open(QIODevice::ReadOnly)) {
        return NULL;
    }
    QTextStream in(&file);
    QStringList * linesInfo =  new QStringList();
    while(!in.atEnd()) {
        QString line = in.readLine();
        linesInfo->append(line);
    }
    file.close();
    return linesInfo;
}
