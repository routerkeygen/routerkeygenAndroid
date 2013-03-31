
#include "qcmdlineparser.h"
#include <QLinkedList>
#include <QMap>
#include <QDebug>
#include <QTextStream>
#include <QStringList>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <numeric>

class QCmdLineParser::QCmdLineParserPrivate
{
public:
    QCmdLineParserPrivate();

    QString m_description;
    QString m_usage;
    QString m_applicationName;
    bool m_hasCustomUsageString;
    bool m_helpEnabled;
    QMap<QString, int> m_optionalArgIndex;
    QList<QCmdLineArgument> m_optionalArgs;
    QLinkedList<QCmdLineArgument> m_positionalArg;

    int checkOptimalArg(const QCmdLineArgument *arg, QVariantMap &result, QString *error, const QStringList &args, int i, int j = -1);
    QString usage(const QString &applicationName) const;
    void addOptionalArg(const QCmdLineArgument &option);
    const QCmdLineArgument* findOptionalArg(const QString& name);
};

QCmdLineParser::QCmdLineParserPrivate::QCmdLineParserPrivate()
{
    QCmdLineArgument helpArg("-h", QCmdLineArgument::StoreTrue, "show this help message and exit");
    helpArg.addAlias("--help");
    addOptionalArg(helpArg);
}

void QCmdLineParser::QCmdLineParserPrivate::addOptionalArg(const QCmdLineArgument& option)
{
    int idx = m_optionalArgs.count();
    m_optionalArgs.push_back(option);
    m_optionalArgIndex[option.name()] = idx;
    foreach (const QString& alias, option.aliases())
        m_optionalArgIndex[alias] = idx;
}

const QCmdLineArgument* QCmdLineParser::QCmdLineParserPrivate::findOptionalArg(const QString& name)
{
    QMap<QString, int>::const_iterator it = m_optionalArgIndex.find(name);
    if (it != m_optionalArgIndex.end()) {
        return &m_optionalArgs[it.value()];
    }
    return 0;
}

QCmdLineParser::QCmdLineParser(const QString &description) : m_d(new QCmdLineParserPrivate)
{
    m_d->m_description = description;
    m_d->m_hasCustomUsageString = false;
    m_d->m_helpEnabled = true;
}

QCmdLineParser::~QCmdLineParser()
{
    delete m_d;
}

QVariantMap QCmdLineParser::parse(int argc, const char **argv, QString *error) const
{
    // Copy all arguments to a QStringList, ok this is slow but if no errors happen during
    // the parse all argv elements will be converted to QStrings anyway, so let's do it now
    // to ease our job.
    QStringList args;
    QString tmp;
    for (int i = 0; i < argc; ++i) {
        tmp = QString::fromLocal8Bit(argv[i]);

        // Separate --foo=bar into --foo and bar
        if (tmp.startsWith('-')) {
            int idx = 1;
            while (idx != -1) {
                idx = tmp.indexOf('=', idx);
                if (idx > 0) {
                    if (tmp[idx - 1] != '\\') {
                        args << tmp.mid(0, idx);
                        tmp.remove(0, idx + 1);
                        break;
                    } else {
                        tmp.remove(idx - 1, 1); // remove the equal escape
                    }
                }
            }
        }
        args << tmp;
    }
    return parse(args, error);
}

QVariantMap QCmdLineParser::parse(const QStringList& args, QString* error) const
{
    Q_ASSERT(!args.isEmpty());

    QString errorFound;
    QVariantMap result;
    for (int i = 1; i < args.size() && errorFound.isNull(); ++i) {
        const QString& argName = args[i];
        if (argName[0] == '-') {
            // Try to search for the entire string
            const QCmdLineArgument* argObj = m_d->findOptionalArg(args[i]);
            if (argObj) {
                i += m_d->checkOptimalArg(argObj, result, &errorFound, args, i);
            } else {
                QString fakeStr("-?");
                for (int j = 1; j < argName.length(); ++j) {
                    fakeStr[1] = argName[j];
                    argObj = m_d->findOptionalArg(fakeStr);
                    if (argObj) {
                        int k = m_d->checkOptimalArg(argObj, result, &errorFound, args, i, j + 1);
                        // if it's not the last char, sum k to i, otherwise sum to j
                        if (argObj->action() == QCmdLineArgument::StoreValue && (j + 1 < argName.length()))
                            break;
                        else
                            i += k;
                    } else {
                        errorFound = tr("Unknown argument: %1").arg(argName);
                    }
                }
                // Check if this is concatened argument, e.g. -fx (two args, f and x)
            }
        } else {
            // positional arguments! but for while it's a unrecognized argument
            errorFound = tr("Unrecognized argument: %1").arg(args[i]);
        }
    }

    if (!errorFound.isNull()) {
        if (error) {
            *error = errorFound;
            result.clear();
        } else {
            QString appName = m_d->m_applicationName.isNull() ? args.first() : m_d->m_applicationName;
            std::puts(m_d->usage(appName).toLocal8Bit().constData());
            errorFound.prepend(": ");
            errorFound.prepend(args.first());
            std::puts(errorFound.toLocal8Bit().constData());
            std::exit(1);
        }
    }

    if (m_d->m_helpEnabled
        && (result.contains("h") || result.contains("help"))) {
        std::puts(help().toLocal8Bit().constData());
        exit(0);
    }
    return result;
}

int QCmdLineParser::QCmdLineParserPrivate::checkOptimalArg(const QCmdLineArgument *arg, QVariantMap& result,
                                                           QString *error, const QStringList &args,
                                                           int i, int j)
{
    switch (arg->action()) {
    case QCmdLineArgument::StoreTrue:
        result[arg->keyName()] = true;
        return 0;
    case QCmdLineArgument::CountOccurences:
    {
        QVariant& a = result[arg->keyName()];
        a.setValue(a.toInt() + 1);
        return 0;
    }
    case QCmdLineArgument::StoreValue:
        // If there's no more arguments to eat or
        // we are concatened arguments and are the last one, throw a error!
        const bool imTheLastArg = (i + 1) >= args.length();
        if ((j == -1 && imTheLastArg) || (j + 1 < args[i].length() && imTheLastArg)) {
            *error = tr("Argument %1: expected one argument").arg(arg->name());
        } else {
            const QString value = (j == -1 || (j + 1 >= args[i].length())) ? args[++i] : args[i].mid(j);
            if (arg->validate(value))
                result[arg->keyName()] = value;
            else
                *error = tr("Invalid format for argument %1").arg(arg->name());
            return 1;
        }
        return 0;
    }
    return 0;
}

void QCmdLineParser::disableHelpOption()
{
    if (!m_d->m_helpEnabled)
        return;

    m_d->m_helpEnabled = false;
    m_d->m_optionalArgs.removeFirst();
    m_d->m_optionalArgIndex.remove("-h");
    m_d->m_optionalArgIndex.remove("--help");
    // re-index everybody
    foreach (QString str, m_d->m_optionalArgIndex.keys())
        m_d->m_optionalArgIndex[str]--;
}

void QCmdLineParser::addOption(const char* option, QCmdLineArgument::Action action, const QString &help, const QString &alias)
{
    addOption(QCmdLineArgument(option, action, help).addAlias(alias));
}

void QCmdLineParser::addOption(const char *option, const QString &help)
{
    addOption(QCmdLineArgument(option, QCmdLineArgument::StoreTrue, help));
}

void QCmdLineParser::addOption(const QCmdLineArgument &option)
{
    const QString optName = option.name();
    if (optName.startsWith('-')) {
        m_d->addOptionalArg(option);
    } else {
        m_d->m_positionalArg << option;
    }
}

void QCmdLineParser::setApplicationName(const QString &name)
{
    m_d->m_applicationName = name;
}

void QCmdLineParser::setUsage(const QString &usage)
{
    m_d->m_hasCustomUsageString = true;
    m_d->m_usage = usage;
}

QString QCmdLineParser::usage() const
{
    return m_d->usage(m_d->m_applicationName);
}

QString QCmdLineParser::QCmdLineParserPrivate::usage(const QString& applicationName) const
{
    if (m_hasCustomUsageString)
        return m_usage;

    // build the usage string, guess this don't need to be cached, because it will be called just one or
    // two times
    QString usage;
    QTextStream s(&usage);
    s << tr("Usage: %1").arg(applicationName);

    foreach (const QCmdLineArgument arg, m_optionalArgs) {
        s << " [" << arg.name();
        if (arg.action() == QCmdLineArgument::StoreValue)
            s << ' ' << arg.keyName().toUpper();
        s << ']';
    }

    return usage;
}

static int _accumulateStringSize(int v, const QString& elem)
{
    return v + elem.count();
}

QString QCmdLineParser::help() const
{
    QString help;
    QTextStream s(&help);
    s.setFieldAlignment(QTextStream::AlignLeft);

    s << usage() << "\n\n";
    if (!m_d->m_description.isEmpty())
        s << m_d->m_description << "\n\n";
    s << tr("Optional arguments:\n");

    int fieldWidth = 0;
    QList<QCmdLineArgument>::const_iterator it = m_d->m_optionalArgs.begin();
    for (; it != m_d->m_optionalArgs.end(); ++it) {
        QStringList aliases = it->aliases();
        int aliasesCount = aliases.count();
        int width = std::accumulate(aliases.begin(), aliases.end(), 0, &_accumulateStringSize);
        // add the ", "'s
        width += (aliasesCount - 1) * 2;
        if (it->action() == QCmdLineArgument::StoreValue)
            width += (1 + it->keyName().size()) * it->aliases().count();
        fieldWidth = std::max(fieldWidth, width);
    }

    // Print the options
    it = m_d->m_optionalArgs.begin();
    QString keyName;
    for (; it != m_d->m_optionalArgs.end(); ++it) {
        bool isStorable = it->action() == QCmdLineArgument::StoreValue;
        if (isStorable)
            keyName = it->keyName().toUpper();

        QStringList args;
        foreach(QString name, it->aliases()) {
            args << name;
            if (isStorable) {
                args.last().append(' ');
                args.last().append(keyName);
            }
        }
        s << "  ";
        s.setFieldWidth(fieldWidth);
        s << args.join(", ");
        s.setFieldWidth(0);
        s << "  " << it->help() << '\n';
    }
    return help;
}

