#ifndef ROUTERKEYGEN_H
#define ROUTERKEYGEN_H
#include "Keygen.h"
#include "WirelessMatcher.h"
#include <QMainWindow>
#include "KeygenThread.h"
#include "QWifiManager.h"

namespace Ui {
    class RouterKeygen;
}

class RouterKeygen : public QMainWindow
{
    Q_OBJECT

public:
    explicit RouterKeygen(QWidget *parent = 0);
    ~RouterKeygen();
public slots:
    void refreshNetworks();
    void scanFinished(int);
    void getResults();
private:
    Ui::RouterKeygen *ui;
    QVector<QString> listKeys;
    WirelessMatcher matcher;
    Keygen * router;
    KeygenThread * calculator;
    QWifiManager manager;
};

#endif // ROUTERKEYGEN_H
