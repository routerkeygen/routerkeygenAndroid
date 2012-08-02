#ifndef ALICEHANDLE_H
#define ALICEHANDLE_H
#include "alicemagicinfo.h"
#include <QtXml/QXmlDefaultHandler>
#include <QTreeWidget>
#include <QMap>
#include <QVector>
#include <memory>

class AliceHandler : public QXmlDefaultHandler
{
    public:
        AliceHandler(){}
        ~AliceHandler(){}
        bool readFile(const QString &fileName);
        bool isSupported();
        QMap<QString, QVector<AliceMagicInfo *> *> getSupportedAlice();
    protected:
        bool startElement(const QString &,
                          const QString &localName,
                          const QString &,
                          const QXmlAttributes &attributes);
        bool endElement(const QString &,
                        const QString &,
                        const QString &){return true;} /*Not used*/
        bool characters(const QString &){ return true;}/*Not used*/
        bool fatalError(const QXmlParseException &exception);

    private:
        void cleanInfo();
        QMap<QString ,QVector<AliceMagicInfo *> *> supportedAlice;

};

#endif // ALICEHANDLE_H
