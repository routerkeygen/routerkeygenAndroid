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
#include "QWifiManagerPrivateWin.h"
#include <QDebug>
#include "QWifiManager.h"

QWifiManagerPrivateWin::QWifiManagerPrivateWin() : scan(NULL), timerId(-1){
}

QWifiManagerPrivateWin::~QWifiManagerPrivateWin() {
	if (scan != NULL) {
		scan->terminate(); //we are using SIGTERM here because we really want it dead!
		scan->waitForFinished();
		delete scan;
	}
}

void QWifiManagerPrivateWin::startScan() {
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
	args << "wlan" << "show" << "network" << "mode=bssid";
	connect(scan, SIGNAL(finished(int)), this, SLOT(parseResults()));
	scan->start("netsh", args);
}

void QWifiManagerPrivateWin::parseResults() {
	QString reply(scan->readAllStandardOutput());
    //qDebug() << reply;
	QStringList lines = reply.split("\n");
	QString ssid, bssid, enc;
	int level;
	clearPreviousScanResults();
	for (int i = 0; i < lines.size(); ++i) {
		if (lines.at(i).contains("BSSID")) {
			bssid = lines.at(i).mid(lines.at(i).indexOf(":") + 2, 17).toUpper();
			i++;
			int pos = lines.at(i).indexOf(":") + 2;
			level = lines.at(i).mid(pos, lines.at(i).indexOf("%") - pos).toInt(
					NULL, 10);
			scanResults.append(QSharedPointer<QScanResult> ( new QScanResult(ssid, bssid, enc, 0, level));
		} else if (lines.at(i).contains("SSID")) {
			ssid = lines.at(i).mid(lines.at(i).indexOf(":") + 2).remove("\n");
			i += 2;
			enc = lines.at(i).mid(lines.at(i).indexOf(":") + 2);
		}

	}
	emit scanFinished(QWifiManager::SCAN_OK);
}

void QWifiManagerPrivateWin::timerEvent(QTimerEvent *)
{
   // qDebug() << "Rescanning";
    startScan();
}
