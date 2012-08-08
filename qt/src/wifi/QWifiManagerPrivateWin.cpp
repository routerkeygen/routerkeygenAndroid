/*
 * QWifiManagerPrivateWin.cpp
 *
 *  Created on: 8 de Ago de 2012
 *      Author: ruka
 */

#include "QWifiManagerPrivateWin.h"

QWifiManagerPrivateWin::QWifiManagerPrivateWin() {
	// TODO Auto-generated constructor stub

}

QWifiManagerPrivateWin::~QWifiManagerPrivateWin() {
	if (scan != NULL) {
		scan->terminate(); //we are using SIGTERM here because we really want it dead!
		scan->waitForFinished();
		delete scan;
	}
}

void QWifiManagerPrivateWin::startScan() {

	if (scan != NULL) {
		delete scan;
	}
	scan = new QProcess(this);
	QStringList args;
	args << "wlan" << "show" << "network" << "mode=bssid";
	connect(scan, SIGNAL(finished(int)), this, SLOT(parseResults()));
	scan->start("netsh", args);
}

void QWifiManagerPrivateWin::parseResults() {
	QString reply(scan->readAllStandardOutput());
	qDebug() << reply;
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
			scanResults.append(new QScanResult(ssid, bssid, enc, 0, level));
		} else if (lines.at(i).contains("SSID")) {
			ssid = lines.at(i).mid(lines.at(i).indexOf(":") + 2).remove("\n");
			i += 2;
			enc = lines.at(i).mid(lines.at(i).indexOf(":") + 2);
		}

	}
	emit scanFinished(QWifiManager::SCAN_OK);
}
