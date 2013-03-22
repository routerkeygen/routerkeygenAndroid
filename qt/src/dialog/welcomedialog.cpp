#include "welcomedialog.h"
#include <QDesktopServices>
#include <QUrl>
#include "ui_welcome_dialog.h"

WelcomeDialog::WelcomeDialog(QWidget *parent) :
    QDialog(parent) , ui(new Ui::WelcomeDialog) {
    ui->setupUi(this);
    connect(ui->donate,SIGNAL(clicked()), this, SLOT(donatePaypal()));
    connect(ui->donate_google, SIGNAL(clicked()),this, SLOT(donateGooglePlay()) );
}

WelcomeDialog::~WelcomeDialog(){
    delete ui;
}

void  WelcomeDialog::donatePaypal(){
    QDesktopServices::openUrl(QUrl("https://www.paypal.com/pt/cgi-bin/webscr?cmd=_flow&SESSION=i5165NLrZfxUoHKUVuudmu6le5tVb6c0CX_9CP45rrU1Az-XgWgJbZ5bfJW&dispatch=5885d80a13c0db1f8e263663d3faee8d5348ead9d61c709ee8c979deef3ea735"));
    close();
}

void WelcomeDialog::donateGooglePlay(){
    QDesktopServices::openUrl(QUrl("https://play.google.com/store/apps/details?id=org.exobel.routerkeygendownloader"));
    close();
}
