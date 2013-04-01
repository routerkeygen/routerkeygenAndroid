#include "qcmdlineargument.h"
#include <QDebug>
#include <QRegExp>

class QCmdLineArgument::QCmdLineOptionPrivate
{
public:
    QCmdLineOptionPrivate(const QString &name, QCmdLineArgument::Action action, const QString &help)
        : names(name), help(help), action(action), isRequired(false)
    {
    }

    QStringList names;
    QString keyName;
    QString help;
    QCmdLineArgument::Action action;
    QRegExp validator;
    bool isRequired;
};

QCmdLineArgument::QCmdLineArgument(const QString &name, QCmdLineArgument::Action action, const QString &help)
    : m_d(new QCmdLineOptionPrivate(name, action, help))
{
}

QCmdLineArgument::QCmdLineArgument(const char *name, QCmdLineArgument::Action action, const QString &help)
    : m_d(new QCmdLineOptionPrivate(name, action, help))
{
}

QCmdLineArgument::QCmdLineArgument(const QCmdLineArgument &other) : m_d(new QCmdLineOptionPrivate(*other.m_d))
{
}

QCmdLineArgument& QCmdLineArgument::operator=(const QCmdLineArgument &other)
{
    *m_d = *other.m_d;
    return *this;
}

QCmdLineArgument::~QCmdLineArgument()
{
    delete m_d;
}

QString QCmdLineArgument::keyName() const
{
    if (m_d->keyName.isNull()) {
        QString tmp = m_d->names.first();
        while (tmp.startsWith('-'))
            tmp.remove(0, 1);
        m_d->keyName = tmp;
    }
    return m_d->keyName;
}

void QCmdLineArgument::setKeyName(const QString &keyName)
{
    m_d->keyName = keyName;
}

QString QCmdLineArgument::name() const
{
    return m_d->names.first();
}

QCmdLineArgument& QCmdLineArgument::addAlias(const QString &alias)
{
    m_d->names << alias;
    return *this;
}

QStringList QCmdLineArgument::aliases() const
{
    return m_d->names;
}

QCmdLineArgument::Action QCmdLineArgument::action() const
{
    return m_d->action;
}

void QCmdLineArgument::setValidator(const QRegExp &validator)
{
    m_d->validator = validator;
}

bool QCmdLineArgument::validate(const QString &value) const
{
    if (m_d->validator.isEmpty())
        return true;
#ifndef NDEBUG
    if (!m_d->validator.isValid())
        qWarning() << "Invalid regex used to validate argument" << name();
#endif
    return m_d->validator.exactMatch(value);
}

void QCmdLineArgument::setRequired(bool required)
{
    m_d->isRequired = required;
}

bool QCmdLineArgument::isRequired() const
{
    return m_d->isRequired;
}

void QCmdLineArgument::setHelp(const QString& text)
{
    m_d->help = text;
}

QString QCmdLineArgument::help() const
{
    return m_d->help;
}

