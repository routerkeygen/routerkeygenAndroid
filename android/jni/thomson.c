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
#include "org_exobel_routerkeygen_algorithms_NativeThomson.h"
#include <ctype.h>
#include <string.h>
#include "sha.h"
#include "unknown.h"
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <android/log.h>
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "Routerkeygen", __VA_ARGS__))

JNIEXPORT jobjectArray JNICALL Java_org_exobel_routerkeygen_algorithms_NativeThomson_thomson(
		JNIEnv * env, jobject obj, jbyteArray ess, jint start, jint end) {
	int n = end;
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid_s = (*env)->GetFieldID(env, cls, "stopRequested", "Z");
	if (fid_s == NULL) {
		return NULL; /* exception already thrown */
	}
	unsigned char stop = (*env)->GetBooleanField(env, obj, fid_s);
	jbyte *e_native = (*env)->GetByteArrayElements(env, ess, 0);
	uint8_t ssid[3];
	ssid[0] = e_native[0];
	ssid[1] = e_native[1];
	ssid[2] = e_native[2];
	uint8_t message_digest[20];
	SHA_CTX sha1;
	int year = 4;
	int week = 1;
	int i = 0;
	char debug[80];
	char input[13];
	input[0] = 'C';
	input[1] = 'P';
	char result[20][11];
	int keys = 0;
	for (i = start; i < n; ++i) {
		sprintf((&input[0]) + 6, "%02X%02X%02X", (int) dic[i][0],
				(int) dic[i][1], (int) dic[i][2]);
		stop = (*env)->GetBooleanField(env, obj, fid_s);
		if (stop) {
			(*env)->ReleaseByteArrayElements(env, ess, e_native, 0);
			//LOGI("Stopping");
			return NULL;
		}
		for (year = 4; year <= 12; ++year) {
			for (week = 1; week <= 52; ++week) {
				input[2] = '0' + year / 10;
				input[3] = '0' + year % 10;
				input[4] = '0' + week / 10;
				input[5] = '0' + week % 10;
				SHA1_Init(&sha1);
				SHA1_Update(&sha1, (const void *) input, 12);
				SHA1_Final(message_digest, &sha1);
				if ((memcmp(&message_digest[17], &ssid[0], 3) == 0)) {
					sprintf(result[keys++], "%02X%02X%02X%02X%02X",
							message_digest[0], message_digest[1],
							message_digest[2], message_digest[3],
							message_digest[4]);
				}
			}
		}
	}
	jobjectArray ret;
	ret = (jobjectArray) (*env)->NewObjectArray(env, keys,
			(*env)->FindClass(env, "java/lang/String"), 0);
	for (i = 0; i < keys; ++i)
		(*env)->SetObjectArrayElement(env, ret, i,
				(*env)->NewStringUTF(env, result[i]));
	(*env)->ReleaseByteArrayElements(env, ess, e_native, 0);
	return ret;
}
