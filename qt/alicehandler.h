#ifndef ALICEHANDLE_H
#define ALICEHANDLE_H
#include "alicemagicinfo.h"
#include <QtXml/QXmlDefaultHandler>
#include <QTreeWidget>

class AliceHandler : public QXmlDefaultHandler
{
    public:
        AliceHandler(QString alice);
        ~AliceHandler();
        bool readFile(const QString &fileName);
        bool isSupported();
        QVector<AliceMagicInfo *> & getSupportedAlice();
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
        QVector<AliceMagicInfo *> supportedAlice;
        QString alice;

};

#endif // ALICEHANDLE_H
