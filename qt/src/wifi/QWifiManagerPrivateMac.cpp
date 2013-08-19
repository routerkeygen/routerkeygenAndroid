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
#include "QWifiManagerPrivateMac.h"
#include <QDebug>
#include "QWifiManager.h"
#include <QSharedPointer>

QWifiManagerPrivateMac::QWifiManagerPrivateMac() : scan(NULL), timerId(-1){
}

QWifiManagerPrivateMac::~QWifiManagerPrivateMac() {
	if (scan != NULL) {
		scan->terminate(); //we are using SIGTERM here because we really want it dead!
		scan->waitForFinished();
		delete scan;
	}
}

void QWifiManagerPrivateMac::startScan() {
    if ( timerId == -1 )
        timerId = startTimer(120000);
    if (scan != NULL ) {
        if ( scan->state() == QProcess::NotRunning )
            delete scan;
        else
            return;
	}
    scan = new QProcess();
	QStringList args;
    args << "-s";
	connect(scan, SIGNAL(finished(int)), this, SLOT(parseResults()));
    scan->start("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport", args);
}


void QWifiManagerPrivateMac::parseResults() {
	QString reply(scan->readAllStandardOutput());
	QStringList lines = reply.split("\n");
    QString ssid, bssid, enc;
    int level;
    clearPreviousScanResults();
    if ( lines.size() <= 1 ){
        emit scanFinished(QWifiManager::SCAN_OK);
        return;
    }
    int ssidLimit = lines.at(0).indexOf("BSSID");
    if (ssidLimit == -1 ){
        emit scanFinished(QWifiManager::SCAN_OK);
        return;
    }
    for (int i = 1; i < lines.size(); ++i) {
        ssid = lines.at(i).left(ssidLimit).trimmed();
        if ( ssid.length() == 0)
            continue;
        bssid = lines.at(i).mid(ssidLimit,17).toUpper();
        level = lines.at(i).mid(ssidLimit+18,4).trimmed().toInt() + 100;
        if ( lines.at(i).contains("PSK") )
            enc = "PSK";
        else if ( lines.at(i).contains("EAP") )
            enc = "EAP";
        else if ( lines.at(i).contains("WEP") )
            enc = "WEP";
        else
            enc = "Open";
        scanResults.append(QSharedPointer<QScanResult>(new QScanResult(ssid, bssid, enc, 0, level)));
    }
    emit scanFinished(QWifiManager::SCAN_OK);

}

void QWifiManagerPrivateMac::timerEvent(QTimerEvent *)
{
   // qDebug() << "Rescanning";
    startScan();
}
