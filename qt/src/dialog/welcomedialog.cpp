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
