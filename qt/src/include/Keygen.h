/*
 * Copyright 2012 Rui Araújo, Luís Fonseca
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
#ifndef Keygen_H
#define Keygen_H
#include <QString>
#include <QVector>

class Keygen {
public:
	const static int ERROR = 0;
	Keygen(QString & ssid, QString & mac, int level, QString enc);
	QVector<QString> & getResults();
	void stop();
	bool isStopped() const;
	virtual bool isSupported() const;
	QString getEncryption() const;
	QString getError() const;
	int getLevel() const;
	QString getMacAddress() const;
	QString getSsidName() const;
	bool isStopRequested() const;
	virtual ~Keygen() {
	}
	bool isLocked();

	static QString getScanResultSecurity(Keygen * scanResult);

	// Constants used for different security types
	const static QString PSK;
	const static QString WEP;
	const static QString EAP;
	const static QString OPEN;
protected:
	QVector<QString> results;
	bool stopRequested;
	void setError(const QString & error);
private:
	virtual QVector<QString> & getKeys() = 0;

	QString ssidName;
	QString macAddress;
	int level;
	QString encryption;
	QString error;

};

#endif // Keygen_H
