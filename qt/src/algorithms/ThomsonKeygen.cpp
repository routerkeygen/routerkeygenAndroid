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
#include "ThomsonKeygen.h"
#include "unknown.h"
#include <stdio.h>

#ifdef Q_OS_LINUX
#include <openssl/sha.h>
#else
#include "sha.h"
#endif
#include <cstdlib>
#include <stdint.h>
#include <QFile>
#include <QDataStream>
#include <QIODevice>
#include <QMutex>
#include <QStack>
#include <QThread>

/*
 * A private task to divide the calculation work among threads.
 */
class ThomsonTask: public QThread {

public:
    ThomsonTask(int i, int final , uint32_t essid, QVector<QString> * results, QMutex * mutex, bool * stop) :
        i(i), final(final), ssid(essid), results(results), mutex(mutex), stopRequested(stop) {
	}
	~ThomsonTask() {
	}

	void run() {
		uint8_t message_digest[20];
		SHA_CTX sha1;
		int year = 4;
		int week = 1;
		char input[13];
		char key[11];
		uint32_t * currentSSID = new uint32_t;
		input[0] = 'C';
		input[1] = 'P';
		for (; i < final; ++i) {
			sprintf(input + 6, "%02X%02X%02X", (int) dic[i][0], (int) dic[i][1],
					(int) dic[i][2]);
			for (year = 4; year <= 12; ++year) {
				for (week = 1; week <= 52; ++week) {
					input[2] = '0' + year / 10;
					input[3] = '0' + year % 10;
					input[4] = '0' + week / 10;
					input[5] = '0' + week % 10;
                    if ( *stopRequested ){
                        delete currentSSID;
                        return;
                    }
					SHA1_Init(&sha1);
					SHA1_Update(&sha1, (const void *) input, 12);
					SHA1_Final(message_digest, &sha1);
					/*
					 * We have to this because of little endianess
					 */
					*currentSSID = 0;
					memcpy(((uint8_t *) currentSSID), message_digest + 19, 1);
					memcpy(((uint8_t *) currentSSID) + 1, message_digest + 18, 1);
					memcpy(((uint8_t *) currentSSID) + 2, message_digest + 17, 1);

					if ((*currentSSID) == ssid) {
						sprintf(key, "%02X%02X%02X%02X%02X", message_digest[0],
								message_digest[1], message_digest[2],
								message_digest[3], message_digest[4]);
						mutex->lock();
						results->append(QString(key));
						mutex->unlock();
					}
				}

			}
		}
		delete currentSSID;
	}
private:
	int i;
	int final;
	uint32_t ssid;
	QVector<QString> * results;
	QMutex * mutex;
    bool * stopRequested;
};

ThomsonKeygen::ThomsonKeygen(QString & ssid, QString & mac, int level,
		QString enc) :
		Keygen(ssid, mac, level, enc) {
}


int ThomsonKeygen::getSupportState() const{
    if (getMacAddress().length() < 12)
        return SUPPORTED;
    // It is a new generation router which the probability of working is
    // very low.
    if (getMacAddress().right(6).toUpper() ==  getSsidName().right(6).toUpper())
        return UNLIKELY;
    return SUPPORTED;
}

QVector<QString> & ThomsonKeygen::getKeys() {
	uint32_t ssid = 0;
	const int n = sizeof(dic) / sizeof("AAA");
	bool status = false;
	ssid = getSsidName().right(6).toInt(&status, 16);
	if (!status)
		throw ERROR;
	QMutex resultsLocker;
	int totalThreads = QThread::idealThreadCount();
    if ( totalThreads <= 0 )
        totalThreads = 1;
    int work = n / totalThreads;
	int beggining = 0;
	QStack<ThomsonTask *> queue;
	for ( int i = 0 ; i < totalThreads-1; ++i )
	{
        queue.push(new ThomsonTask(beggining , beggining + work , ssid , &results, &resultsLocker, &stopRequested));
		beggining += work;
		queue.top()->start();
	}
	//Doing the last one separately
    queue.push(new ThomsonTask(beggining , n , ssid , &results, &resultsLocker, &stopRequested));
	queue.top()->start();
	while ( queue.size()> 0)
	{
		queue.top()->wait();
		delete queue.pop();
	}
	return results;
}
