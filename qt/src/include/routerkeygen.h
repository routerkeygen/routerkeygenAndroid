#ifndef ROUTERKEYGEN_H
#define ROUTERKEYGEN_H
#include "Keygen.h"
#include "WirelessMatcher.h"
#include <QMainWindow>
#include <QLabel>
#include <QSystemTrayIcon>
#include <QMenu>
#include "KeygenThread.h"
#include "QWifiManager.h"
#include <QSettings>

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
    void forceRefreshToggle(int);
    void backgroundRunToggle(bool);

private:
    void addNetworkToTray(const QString & ssid, int level);
    void setLoadingAnimation(const QString& text);
    void cleanLoadingAnimation();
    void calc(QString ssid, QString mac );
    Ui::RouterKeygen *ui;
    QVector<QString> listKeys;
    WirelessMatcher matcher;
    Keygen * router;
    KeygenThread * calculator;
    QWifiManager * wifiManager;
    QMovie * loadingAnim;
    QLabel * loading;
    QLabel * loadingText;
    QSystemTrayIcon *trayIcon;
    QMenu *trayMenu;
    bool runInBackground;
    //SETTINGS VALUES
    QSettings * settings;
    const static QString RUN_IN_BACKGROUND;
    const static QString FORCE_REFRESH;
};

#endif // ROUTERKEYGEN_H
