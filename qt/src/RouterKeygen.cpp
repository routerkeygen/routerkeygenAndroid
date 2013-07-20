/*
 * Copyright 2013 Rui Araújo, Luís Fonseca
 *
 * This file is part of Router Keygen.
 *
 * Router Keygen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Router Keygen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Router Keygen.  If not, see <http://www.gnu.org/licenses/>.
 */
#include "RouterKeygen.h"
#include "ui_routerkeygen.h"
#include <QMessageBox>
#include "mac/macloginitemsmanager.h"
#include <QCompleter>
#include <QStringList>
#include <QMovie>
#include <QListWidgetItem>
#include <QContextMenuEvent>
#include <QMouseEvent>
#include <QMenu>
#include <QIcon>
#include <QClipboard>
#include <QWidgetAction>
#include <QDesktopServices>
#include <QDateTime>
#include <QFile>
#include <QUrl>
#include <QNetworkAccessManager>
#include <QNetworkRequest>
#include <QTextStream>
#include <stdlib.h>
#include "version.h"

RouterKeygen::RouterKeygen(QWidget *parent) :
        QMainWindow(parent), ui(new Ui::RouterKeygen), router(NULL),calculator(NULL),
        loading(NULL), loadingText(NULL), aboutDialog(NULL), welcomeDialog(NULL) {
    ui->setupUi(this);
#if !defined(Q_OS_WIN) && !defined(Q_OS_MAC)
    setWindowIcon(QIcon(":/tray_icon.png"));
#endif
    connect(ui->calculateButton, SIGNAL( clicked() ), this,
            SLOT( manualCalculation() ));
    connect(ui->refreshScan, SIGNAL( clicked() ), this,
            SLOT( refreshNetworks() ));
    connect(ui->networkslist, SIGNAL( cellClicked(int,int) ), this,
            SLOT( tableRowSelected(int,int) ));

    connect(ui->actionDonate,SIGNAL(triggered()), this, SLOT(donatePaypal()));
    connect(ui->actionDonate_Google_Play, SIGNAL(triggered()),this, SLOT(donateGooglePlay()) );
    connect(ui->actionFeedback, SIGNAL(triggered()), this,SLOT(feedback()));
    connect(ui->actionAbout, SIGNAL(triggered()), this,SLOT(showAboutDialog()) );
    connect(ui->actionCheck_for_Updates, SIGNAL(triggered()), this,SLOT(checkUpdates()));

    
#if !defined(Q_OS_WIN) && !defined(Q_OS_MAC)
    connect(ui->forceRefresh, SIGNAL( stateChanged(int) ), this,
            SLOT( forceRefreshToggle(int) ));
#else
    ui->forceRefresh->setVisible(false); // it is not needed in Windows
#endif

    wifiManager = new QWifiManager();
    connect(wifiManager, SIGNAL( scanFinished(int) ), this,
            SLOT( scanFinished(int) ));
    loadingAnim = new QMovie(":/loading.gif");
    loadingAnim->setParent(this);
    //Auto-Complete!
    wordList << "Alice-" <<  "Arcor-" << "AXTEL-" << "AXTEL-XTREMO-" << "Bbox-" <<
            "BigPond" << "Blink" << "Cabovisao-" << "CONN" << "CYTA" << "Discus--"<<
            "DLink-" << "DMAX" << "EasyBox-" << "eircom" << "FASTWEB-1-" << "INFINITUM" <<
            "InfostradaWiFi-" << "InterCable" << "JAZZTEL_" << "MAXCOM" << "Megared" <<
            "MEO-" << "O2Wireless" << "Optimus" << "OptimusFibra" << "Orange-" << "OTE" <<
            "Otenet" << "PBS" << "privat" << "ptv" << "SKY" << "SpeedTouch" << "TECOM-AH4222-" <<
            "TECOM-AH4021-" << "TeleTu" << "Thomson" << "TN_private_" << "Vodafone-" << "WiFi" <<
            "wifimedia_R-" << "WLAN_" << "WLAN" << "YaCom";
    completer = new QCompleter(wordList, this);
    completer->setCaseSensitivity(Qt::CaseInsensitive);
    completer->setCompletionMode(QCompleter::PopupCompletion);
    ui->ssidInput->setCompleter(completer);
    ui->networkslist->setSelectionBehavior(QAbstractItemView::SelectRows);
    ui->networkslist->setEditTriggers(QAbstractItemView::NoEditTriggers);
    ui->passwordsList->installEventFilter(this);

    // build menu
    trayMenu = new QMenu(this);
    trayIcon = new QSystemTrayIcon(this);
    // set up and show the system tray icon
    trayIcon->setIcon(QIcon(":/tray_icon.png"));
    trayIcon->setContextMenu(trayMenu);
    trayIcon->show();
    
    //Set widget ration
    ui->splitter->setStretchFactor(0, 2);
    ui->splitter->setStretchFactor(1, 1);
    
    settings = new QSettings("Exobel", "RouterKeygen");
    bool forceRefresh = settings->value(FORCE_REFRESH, false).toBool();
    wifiManager->setForceScan(forceRefresh);
    ui->forceRefresh->setChecked(forceRefresh);
    runInBackground = settings->value(RUN_IN_BACKGROUND, true).toBool();
    runOnStartUp = settings->value(RUN_ON_START_UP, false).toBool();
    qApp->setQuitOnLastWindowClosed(!runInBackground);

    startUpAction = ui->menuPreferences->addAction(tr("Run on Start up"));
    startUpAction->setCheckable(true);
    startUpAction->setChecked(runOnStartUp);
    connect(startUpAction, SIGNAL(toggled(bool)), this,
            SLOT(startUpRunToggle(bool)));

    runInBackgroundAction = ui->menuPreferences->addAction(tr("Run in the background"));
    runInBackgroundAction->setCheckable(true);
    runInBackgroundAction->setChecked(runInBackground);
    connect(runInBackgroundAction, SIGNAL(toggled(bool)), this,
            SLOT(backgroundRunToggle(bool)));

    scanFinished(QWifiManager::SCAN_OK);
    wifiManager->startScan();
}

void RouterKeygen::showWithDialog(){
    show();
    if ( ( QDateTime::currentDateTime().toTime_t()- settings->value(WELCOME_DIALOG, 0).toUInt()) > SECONDS_IN_WEEK ){
        settings->setValue(WELCOME_DIALOG, QDateTime::currentDateTime().toTime_t() );
        if ( welcomeDialog == NULL )
            welcomeDialog = new WelcomeDialog(this);
        welcomeDialog->show();
    }
}

void RouterKeygen::showAboutDialog(){
    if ( aboutDialog == NULL )
        aboutDialog = new AboutDialog(this);
    aboutDialog->show();
}

void  RouterKeygen::checkUpdates(){
    QNetworkAccessManager* mNetworkManager = new QNetworkAccessManager(this);
    QObject::connect(mNetworkManager, SIGNAL(finished(QNetworkReply*)), this, SLOT(onNetworkReply(QNetworkReply*)));

    enableUI(false);
    setLoadingAnimation(tr("Checking for updates"));
    QUrl url("http://android-thomson-key-solver.googlecode.com/svn/trunk/RouterKeygenVersionPC.txt");
    mNetworkManager->get(QNetworkRequest(url));
}


void RouterKeygen::onNetworkReply(QNetworkReply* reply){
    QString replyString;
    const unsigned int RESPONSE_OK = 200;
    cleanLoadingAnimation();
    enableUI(true);
    if(reply->error() == QNetworkReply::NoError)
    {
        unsigned int httpstatuscode = reply->attribute(QNetworkRequest::HttpStatusCodeAttribute).toUInt();
        switch(httpstatuscode)
        {
        case RESPONSE_OK:
            if (reply->isReadable())
            {
                //Assuming this is a human readable file replyString now contains the file
                replyString = QString::fromUtf8(reply->readAll().data()).trimmed();
                if ( replyString == QApplication::applicationVersion() ){
                    ui->statusBar->showMessage(tr("The application is already at the latest version."));
                }
                else{
                    UpdateDialog * updateDialog = new UpdateDialog(this);
                    updateDialog->show();
                }
            }
            break;
        default:
            ui->statusBar->showMessage(tr("Error while checking for updates"));
            break;
        }
    }
    else {
        ui->statusBar->showMessage(tr("Error while checking for updates"));
    }
    reply->deleteLater();
}

void RouterKeygen::donateGooglePlay(){
    QDesktopServices::openUrl(QUrl("https://play.google.com/store/apps/details?id=org.exobel.routerkeygendownloader"));
}

void  RouterKeygen::donatePaypal(){
    QDesktopServices::openUrl(QUrl("https://www.paypal.com/pt/cgi-bin/webscr?cmd=_flow&SESSION=i5165NLrZfxUoHKUVuudmu6le5tVb6c0CX_9CP45rrU1Az-XgWgJbZ5bfJW&dispatch=5885d80a13c0db1f8e263663d3faee8d5348ead9d61c709ee8c979deef3ea735"));
}


void  RouterKeygen::feedback(){
    QDesktopServices::openUrl(QUrl("mailto:"+ QString(PROJECT_CONTACT) +"?subject=RouterKeygen Feedback "+ QString(SYSNAME) +"&body=I love you! ;)"));
}


RouterKeygen::~RouterKeygen() {
    delete ui;
    delete loadingAnim;
    delete wifiManager;
    if (calculator != NULL) {
        if (calculator->isRunning()) {
            router->stop();
            calculator->wait();
        }
        delete calculator;
    }
    delete router;
    delete settings;
    trayMenu->clear();
    delete trayMenu;
    delete trayIcon;
    delete aboutDialog;
    delete welcomeDialog;
}
void RouterKeygen::manualCalculation() {
    if (ui->ssidInput->text().trimmed() == "")
        return;
    calc(ui->ssidInput->text().trimmed(), ui->macInput->text());
}

void RouterKeygen::calc(QString ssid, QString mac) {
    if (calculator != NULL) {
        return; //ignore while a calculator is still running
    }
    delete router;
    if (ssid == "")
        return;
    if ( mac.length()>0 && mac.count(QRegExp("^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$")) == 0 ) {
        mac = "";
        ui->statusBar->showMessage(tr("Invalid MAC. It will not be used."));
    }

    router = matcher.getKeygen(ssid, mac, 0, "");
    if (!router) {
        ui->statusBar->showMessage(
                tr("Unsupported network. Check the MAC address and the SSID."));
        return;
    }
    ui->passwordsList->clear();
    setLoadingAnimation(tr("Calculating keys. This can take a while."));
    this->calculator = new KeygenThread(router);
    connect(this->calculator, SIGNAL( finished() ), this, SLOT( getResults() ));
    enableUI(false);
    this->calculator->start();
}

void RouterKeygen::tableRowSelected(int row, int) {
    calc(ui->networkslist->item(row, 0)->text().trimmed(),
         ui->networkslist->item(row, 1)->text().toUpper());
}

void RouterKeygen::refreshNetworks() {
    enableUI(false);
    setLoadingAnimation(tr("Scanning the network"));
    wifiManager->startScan();
}

void RouterKeygen::scanFinished(int code) {
    cleanLoadingAnimation();
    enableUI(true);
    switch (code) {
    case QWifiManager::SCAN_OK: {
            ui->networkslist->clear();
            QVector<QScanResult*> networks = wifiManager->getScanResults();
            ui->networkslist->setRowCount(networks.size());
            trayMenu->clear();
            connect(trayMenu->addAction(tr("Vulnerable networks")),
                    SIGNAL(triggered()), this, SLOT(show()));
            bool foundVulnerable = false;
            for (int i = 0; i < networks.size(); ++i) {
                ui->networkslist->setItem(i, 0,
                                          new QTableWidgetItem(networks.at(i)->ssid));
                ui->networkslist->setItem(i, 1,
                                          new QTableWidgetItem(networks.at(i)->bssid));
                QString level;
                level.setNum(networks.at(i)->level, 10);
                ui->networkslist->setItem(i, 2, new QTableWidgetItem(level));
                Keygen * supported = matcher.getKeygen(networks.at(i)->ssid,
                                                       networks.at(i)->bssid, networks.at(i)->level, networks.at(i)->capabilities);
                if (supported != NULL) {
                    if ( supported->getSupportState() == Keygen::SUPPORTED )
                        ui->networkslist->setItem(i, 3,
                                                  new QTableWidgetItem(tr("Yes")));
                    else //if ( supportsed->getSupportState() == Keygen::MAYBE )
                        ui->networkslist->setItem(i, 3,
                                                  new QTableWidgetItem(tr("Maybe")));
                    addNetworkToTray(networks.at(i)->ssid, networks.at(i)->level, supported->isLocked());
                    foundVulnerable = true;
                    delete supported;
                } else
                    ui->networkslist->setItem(i, 3, new QTableWidgetItem(tr("No")));
            }
            if (!foundVulnerable) {
                trayMenu->addAction(tr("None were detected"))->setEnabled(false);
            }
            trayMenu->addSeparator();
            trayMenu->addAction(startUpAction);
            trayMenu->addAction(runInBackgroundAction);
            trayMenu->addSeparator();
            QAction * exitAction = trayMenu->addAction(tr("Exit"));
            connect(exitAction, SIGNAL(triggered()), qApp, SLOT(quit()));
            QStringList headers;
            headers << "SSID" << "BSSID" << tr("Strength") << tr("Supported");
            ui->networkslist->setHorizontalHeaderLabels(headers);
            ui->networkslist->resizeColumnsToContents();
            ui->networkslist->horizontalHeader()->setStretchLastSection(true);
            ui->networkslist->sortByColumn(2); //Order by Strength
            ui->networkslist->sortByColumn(3); // and then by support
            //ui->statusBar->clearMessage();
            break;
	}
    case QWifiManager::ERROR_NO_NM:
        ui->statusBar->showMessage(tr("Network Manager was not detected"));
        break;

    case QWifiManager::ERROR_NO_WIFI:
        ui->statusBar->showMessage(tr("No Wifi device detected"));
        break;

    case QWifiManager::ERROR_NO_WIFI_ENABLED:
        ui->statusBar->showMessage(tr("The wifi device is not enabled"));
        break;
    }

}

void RouterKeygen::addNetworkToTray(const QString & ssid, int level, bool locked ) {
    QIcon icon;
    if (level >= 75)
        icon = locked?QIcon::fromTheme("nm-signal-100-secure"):QIcon::fromTheme("nm-signal-100");
    else if (level >= 50)
        icon = locked?QIcon::fromTheme("nm-signal-75-secure"):QIcon::fromTheme("nm-signal-75");
    else if (level >= 25)
        icon = locked?QIcon::fromTheme("nm-signal-50-secure"):QIcon::fromTheme("nm-signal-50");
    else
        icon = locked?QIcon::fromTheme("nm-signal-25-secure"):QIcon::fromTheme("nm-signal-25");
    QAction * net = trayMenu->addAction(icon, ssid);
    connect(net, SIGNAL(triggered()), this, SLOT(show()));
}

void RouterKeygen::getResults() {
    cleanLoadingAnimation();
    enableUI(true);
    if (calculator->hadError()) {
        ui->statusBar->showMessage(tr("Error while calculating."));
        delete calculator;
        calculator = NULL;
        return;
    }
    listKeys = this->calculator->getResults();
    if (listKeys.isEmpty()) {
        ui->statusBar->showMessage(tr("No keys were calculated."));
        delete calculator;
        calculator = NULL;
        return;
    }
    for (int i = 0; i < listKeys.size(); ++i)
        ui->passwordsList->insertItem(0, listKeys.at(i));
    ui->statusBar->showMessage(tr("Calculation finished"));
    ui->passwordsLabel->setText(tr("Calculated Passwords for %1").arg(router->getSsidName()));
    delete calculator;
    calculator = NULL;
}

bool RouterKeygen::eventFilter(QObject *obj, QEvent *event) {
    // you may handle multiple objects checking "obj" parameter
    if (event->type() == QEvent::ContextMenu) {
        QMouseEvent *mouseEvent = static_cast<QMouseEvent*>(event);
        if (obj == ui->passwordsList) {
            rightButtonClicked(obj, mouseEvent->globalPos());
            return true;
        }
    }
    return QObject::eventFilter(obj, event);
}

void RouterKeygen::rightButtonClicked(QObject *obj, const QPoint &pos) {
    if (obj == ui->passwordsList) {
        if (ui->passwordsList->count() == 0)
            return;
        if (ui->passwordsList->selectedItems().size() == 0)
            return;
        QMenu *menu = new QMenu();
        QAction * copy = new QAction(tr("Copy"), this);
        copy->setShortcut(QKeySequence::Copy);
        copy->setStatusTip(tr("Copy this key"));
        connect(copy, SIGNAL(triggered()), this, SLOT(copyKey()));
        menu->addAction(copy);
        menu->exec(pos);
    }
}

void RouterKeygen::copyKey() {
    QList<QListWidgetItem*> selectedItems = ui->passwordsList->selectedItems();
    if (selectedItems.size() == 0)
        return;
    QClipboard *clipboard = QApplication::clipboard();
    clipboard->setText(selectedItems.at(0)->text(), QClipboard::Clipboard);
    ui->statusBar->showMessage(tr("%1 copied").arg(selectedItems.at(0)->text()));
}

void RouterKeygen::forceRefreshToggle(int state) {
    wifiManager->setForceScan(state == Qt::Checked);
    settings->setValue(FORCE_REFRESH, state == Qt::Checked);
}

void RouterKeygen::backgroundRunToggle(bool state) {
    runInBackground = state;
    qApp->setQuitOnLastWindowClosed(!state);
    settings->setValue(RUN_IN_BACKGROUND, runInBackground);
}

void RouterKeygen::startUpRunToggle(bool state) {
    runOnStartUp = state;
    settings->setValue(RUN_ON_START_UP, runOnStartUp);
#ifdef Q_OS_UNIX
#ifdef Q_OS_MAC
    MacLoginItemsManager loginManager;
    if ( runOnStartUp ){
        if ( !loginManager.containsRunningApplication() ){
            if (!loginManager.appendRunningApplication())
                qDebug() << "Error setting startup state";
        }
    }
    else{
        if ( loginManager.containsRunningApplication() ){
            if (!loginManager.removeRunningApplication() )
                qDebug() << "Error setting startup state";
        }
    }
#else
    QString newFile = "/home/" + QString(getenv("USER"))
                      + "/.config/autostart/routerkeygen.desktop";
    if (runOnStartUp) {
        QFile file(newFile);
        if ( file.open(QIODevice::ReadWrite) )
        {
            QTextStream stream( &file );
            stream << "[Desktop Entry]" << endl;
            stream << "Type=Application" << endl;
            stream << "Terminal=false" << endl;
            stream << "Exec=" << QCoreApplication::applicationFilePath() << " --no-gui" << endl;
            stream << "Name=" <<  QCoreApplication::applicationName() << endl;
            stream.flush();
            QFile::setPermissions(newFile,
                                  QFile::ReadOwner | QFile::WriteOwner | QFile::ReadUser
                                  | QFile::WriteUser | QFile::ReadGroup | QFile::ReadGroup
                                  | QFile::ReadOther);

        }
    } else {
        if (QFile::exists(newFile))
            QFile::remove(newFile);
    }
#endif
#endif
#ifdef Q_OS_WIN
    QSettings settings("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run",QSettings::NativeFormat);
    if (runOnStartUp) {
        settings.setValue("RouterKeygen", QCoreApplication::applicationFilePath().replace('/','\\')+ " --no-gui");
    } else {
        settings.remove("RouterKeygen");
    }
#endif
}

void RouterKeygen::setLoadingAnimation(const QString& text) {
    loadingAnim->start();
    loading = new QLabel(ui->statusBar);
    loading->setMovie(loadingAnim);
    loadingText = new QLabel(text, ui->statusBar);
    ui->statusBar->clearMessage();
    ui->statusBar->addWidget(loading);
    ui->statusBar->addWidget(loadingText);
}
void RouterKeygen::cleanLoadingAnimation() {
    if (loading == NULL || loadingText == NULL)
        return;
    loadingAnim->stop();
    ui->statusBar->removeWidget(loading);
    ui->statusBar->removeWidget(loadingText);
    loading = NULL;
    loadingText = NULL;
}


void RouterKeygen::enableUI(bool enable){
    ui->actionCheck_for_Updates->setEnabled(enable);
    ui->refreshScan->setEnabled(enable);
    ui->calculateButton->setEnabled(enable);
    ui->networkslist->setEnabled(enable);
}

const QString RouterKeygen::RUN_ON_START_UP = "RUN_ON_START_UP";
const QString RouterKeygen::RUN_IN_BACKGROUND = "RUN_IN_BACKGROUND";
const QString RouterKeygen::FORCE_REFRESH = "FORCE_REFRESH";
const QString RouterKeygen::WELCOME_DIALOG = "WELCOME_DIALOG_TIME";
const unsigned int RouterKeygen::SECONDS_IN_WEEK = 7 * 24 * 3600;
