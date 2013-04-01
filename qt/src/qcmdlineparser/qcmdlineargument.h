#ifndef QCMDLINEARGUMENT_H
#define QCMDLINEARGUMENT_H

#include <QStringList>

class QCmdLineArgument
{
public:
    enum Action
    {
        StoreTrue,
        StoreValue,
        CountOccurences
    };

    QCmdLineArgument(const QString &name, Action action = StoreTrue, const QString &help = QString());
    QCmdLineArgument(const char *name, Action action = StoreTrue, const QString &help = QString());
    QCmdLineArgument(const QCmdLineArgument &other);
    QCmdLineArgument& operator=(const QCmdLineArgument &other);
    ~QCmdLineArgument();

    QCmdLineArgument& addAlias(const QString &alias);
    QStringList aliases() const;

    QString name() const;
    void setKeyName(const QString &keyName);
    QString keyName() const;
    Action action() const;
    void setValidator(const QRegExp &validator);
    bool validate(const QString &value) const;

    void setRequired(bool required);
    bool isRequired() const;

    void setHelp(const QString &text);
    QString help() const;

private:
    class QCmdLineOptionPrivate;
    QCmdLineOptionPrivate * const m_d;
};

#endif
