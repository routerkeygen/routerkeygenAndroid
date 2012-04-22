#ifndef ROUTERKEYGEN_H
#define ROUTERKEYGEN_H
#include "keygenthread.h"
#include "wifinetwork.h"
#include <QMainWindow>

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
    void calculateKeys();
    void getResults();
private:
    Ui::RouterKeygen *ui;
    KeygenThread * calculator;
    QVector<QString> listKeys;
    WifiNetwork * router;
};

#endif // ROUTERKEYGEN_H
