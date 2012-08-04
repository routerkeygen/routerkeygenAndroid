#ifndef ROUTERKEYGEN_H
#define ROUTERKEYGEN_H
#include "Keygen.h"
#include "WirelessMatcher.h"
#include <QMainWindow>
#include <QLabel>
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
    void manualCalculation();
    void scanFinished(int);
    void tableRowSelected(int, int);
    void getResults();

protected:
    bool eventFilter(QObject *obj, QEvent *event);

private slots:
    void rightButtonClicked(QObject *obj,const QPoint &p);
    void copyKey();

private:
    void calc(QString ssid, QString mac );
    Ui::RouterKeygen *ui;
    QVector<QString> listKeys;
    WirelessMatcher matcher;
    Keygen * router;
    KeygenThread * calculator;
    QWifiManager manager;
    QMovie * loadingAnim;
    QLabel * loading;
    QLabel * loadingText;
};

#endif // ROUTERKEYGEN_H
