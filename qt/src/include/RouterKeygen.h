#ifndef ROUTERKEYGEN_H
#define ROUTERKEYGEN_H
#include "Keygen.h"
#include "WirelessMatcher.h"
#include <QMainWindow>
#include <QLabel>
#include <QSystemTrayIcon>
#include <QMenu>
#include "KeygenThread.h"
#include "AboutDialog.h"
#include "QWifiManager.h"
#include <QSettings>
#include <QCompleter>

namespace Ui {
    class RouterKeygen;
}

class RouterKeygen : public QMainWindow
{
    Q_OBJECT

public:
    explicit RouterKeygen(QWidget *parent = 0);
    virtual ~RouterKeygen();
    void showWithDialog();

protected:
    bool eventFilter(QObject *obj, QEvent *event);

private slots:
    void rightButtonClicked(QObject *obj,const QPoint &p);
    void copyKey();
    void forceRefreshToggle(int);
    void backgroundRunToggle(bool);
    void startUpRunToggle(bool);
    void refreshNetworks();
    void manualCalculation();
    void scanFinished(int);
    void tableRowSelected(int, int);
    void getResults();
    void donatePaypal();
    void donateGooglePlay();
    void showAboutDialog();

private:
    void addNetworkToTray(const QString & ssid, int level, bool locked );
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
    QCompleter *completer;
    AboutDialog *  aboutDialog;
    QAction * startUpAction;
    QAction * runInBackgroundAction;
    bool runInBackground;
    bool runOnStartUp;
    //SETTINGS VALUES
    QSettings * settings;
    QStringList wordList;

    const static QString RUN_ON_START_UP;
    const static QString RUN_IN_BACKGROUND;
    const static QString FORCE_REFRESH;
    const static QString VERSION;
};

#endif // ROUTERKEYGEN_H
