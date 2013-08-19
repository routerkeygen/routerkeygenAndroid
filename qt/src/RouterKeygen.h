#ifndef ROUTERKEYGEN_H
#define ROUTERKEYGEN_H
#include <QMainWindow>
#include <QLabel>
#include <QSystemTrayIcon>
#include <QMenu>
#include <QSettings>
#include <QCompleter>
#include <QNetworkReply>
#include <QSharedPointer>

namespace Ui {
    class RouterKeygen;
}

class QScanResult;
class QWifiManager;
class AboutDialog;
class WelcomeDialog;
class UpdateDialog;
class Keygen;
class KeygenThread;
class WirelessMatcher;
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
    void supportedNetworkRowSelected(int, int);
    void unlikelyNetworkRowSelected(int, int);
    void unsupportedNetworkRowSelected(int, int);
    void getResults();
    void donatePaypal();
    void feedback();
    void donateGooglePlay();
    void showAboutDialog();
    void checkUpdates();
    void onNetworkReply(QNetworkReply*);

private:
    void addNetworkToTray(const QString & ssid, int level, bool locked );
    void setLoadingAnimation(const QString& text);
    void cleanLoadingAnimation();
    void calc(QScanResult * wifi );
    void enableUI(bool enable);
    Ui::RouterKeygen *ui;
    QVector<QString> listKeys;
    QVector<QSharedPointer<QScanResult> > wifiNetworks;
    QScanResult * manualWifi;
    WirelessMatcher * matcher;
    KeygenThread * calculator;
    QWifiManager * wifiManager;
    QMovie * loadingAnim;
    QLabel * loading;
    QLabel * loadingText;
    AboutDialog *  aboutDialog;
    WelcomeDialog * welcomeDialog;
    QSystemTrayIcon *trayIcon;
    QMenu *trayMenu;
    QCompleter *completer;
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
    const static QString WELCOME_DIALOG;
    const static unsigned int SECONDS_IN_WEEK;
};

#endif // ROUTERKEYGEN_H
