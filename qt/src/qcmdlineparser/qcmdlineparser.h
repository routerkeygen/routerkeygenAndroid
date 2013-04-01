#ifndef QCMDLINEPARSER_H
#define QCMDLINEPARSER_H

#include <QVariant>
// need qcoreapplication header just for Q_DECLARE_TR_FUNCTIONS macro
#include <QCoreApplication>
#include "qcmdlineargument.h"

class Q_CORE_EXPORT QCmdLineParser
{
    Q_DECLARE_TR_FUNCTIONS(QCmdLineParser);
public:
    QCmdLineParser(const QString &description = QString());
    ~QCmdLineParser();

    /**
     * Parse the args and return a nice QVarianMap, if \p error = 0
     * and an error occur the error message is printed on stdout and
     * the program exits with error code = 1, otherwise the error
     * message is stored on error pointer.
     */
    QVariantMap parse(int argc, const char **argv, QString *error = 0) const;
    QVariantMap parse(const QStringList &args, QString *error = 0) const;
    void addOption(const QCmdLineArgument &option);

    // This is only needed when calling usage() or help() methods by your own
    // the automatic stuff will use argv[0] unless you set the application name.
    void setApplicationName(const QString &name);

    // disable -h and --help options
    void disableHelpOption();

    // overload for syntax sugar
    void addOption(const char *option, QCmdLineArgument::Action action, const QString &help = QString(), const QString &alias = QString());
    void addOption(const char *option, const QString &help = QString());

    QString usage() const;
    void setUsage(const QString &usage);

    QString help() const;

    // NOTE:
    // IMO is better to provide more syntax sugars, because the act of create a
    // QCmdLineOption object and set their properties is a bit boring.
private:
    Q_DISABLE_COPY(QCmdLineParser)

    class QCmdLineParserPrivate;
    QCmdLineParserPrivate * const m_d;
};

#endif
