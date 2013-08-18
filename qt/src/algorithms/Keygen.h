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
    const static int SUPPORTED = 0;
    const static int UNLIKELY = 1;
    const static int UNSUPPORTED = 2;
    Keygen(QString & ssid, QString & mac);
	QVector<QString> & getResults();
	void stop();
    bool isStopped() const;
    QString getError() const;
	QString getMacAddress() const;
	QString getSsidName() const;
	bool isStopRequested() const;
	virtual ~Keygen() {
    }

    int getSupportState() const;

protected:
	QVector<QString> results;
	bool stopRequested;
	void setError(const QString & error);
private:
	virtual QVector<QString> & getKeys() = 0;

	QString ssidName;
    QString macAddress;
	QString error;

};

#endif // Keygen_H
