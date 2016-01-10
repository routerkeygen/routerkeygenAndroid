#include <android/log.h>
#include <ctype.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "upc_keys_wrapper.h"
#include "upc_keys.h"

// Logging
#define LOG_TAG "upc_keys"
#define DPRINTF(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define IPRINTF(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define EPRINTF(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

JNIEXPORT void JNICALL Java_org_exobel_routerkeygen_algorithms_UpcKeygen_upcNative
        (JNIEnv * env, jobject obj, jbyteArray ess, jint mode)
{
  // Get stopRequested - cancellation flag.
  jclass cls = (*env)->GetObjectClass(env, obj);
  jfieldID fid_s = (*env)->GetFieldID(env, cls, "stopRequested", "Z");
  if (fid_s == NULL) {
    return; /* exception already thrown */
  }
  unsigned char stop = (*env)->GetBooleanField(env, obj, fid_s);

  // Monitoring methods
  jmethodID on_key_computed = (*env)->GetMethodID(env, cls, "onKeyComputed", "(Ljava/lang/String;)V");
  jmethodID on_progressed = (*env)->GetMethodID(env, cls, "onProgressed", "(D)V");
  if (on_key_computed == NULL || on_progressed == NULL){
    return;
  }

  // ESSID reading from parameter.
  jbyte *e_native = (*env)->GetByteArrayElements(env, ess, 0);
  char * e_ssid = (char*) e_native;

  // Definitions.
  int matched[2], mx;
  uint32_t buf[4], target;
  char serial[64];
  char pass[9];
  uint32_t i, cnt=0;

  target = strtoul(e_ssid + 3, NULL, 0);
  IPRINTF("Computing UPC keys for essid [%s], target %lu, mode: %d", e_ssid, (unsigned long)target, mode);
  unsigned long stop_ctr = 0;
  unsigned long iter_ctr = 0;

  // Compute - from upc_keys.c
  for (buf[0] = 0; buf[0] <= MAX0; buf[0]++) {
    for (buf[1] = 0; buf[1] <= MAX1; buf[1]++) {
      for (buf[2] = 0; buf[2] <= MAX2; buf[2]++) {
        for (buf[3] = 0; buf[3] <= MAX3; buf[3]++) {
          // Check cancellation signal & progress monitoring.
          stop_ctr += 1;
          iter_ctr += 1;
          if (stop_ctr > (MAX_ITERATIONS/2000)){
            stop_ctr = 0;
            stop = (*env)->GetBooleanField(env, obj, fid_s);
            if (stop) {
              break;
            }

            double current_progress = (double)iter_ctr / MAX_ITERATIONS;
            (*env)->CallVoidMethod(env, obj, on_progressed, (jdouble)current_progress);
          }

          matched[0]= (mode & 1) && upc_generate_ssid(buf, MAGIC_24GHZ) == target;
          matched[1]= (mode & 2) && upc_generate_ssid(buf, MAGIC_5GHZ) == target;
          if (!matched[0] && !matched[1]){
            continue;
          }

          sprintf(serial, "SAAP%d%02d%d%04d", buf[0], buf[1], buf[2], buf[3]);

          // For matched mode compute passwords.
          for(mx=0; mx<2; mx++){
            if (matched[mx]==0){
              continue;
            }

            cnt++;
            compute_wpa2(mx+1, serial, pass);
            IPRINTF("  -> #%02d WPA2 phrase for '%s' = '%s', mode: %d", cnt, serial, pass, mx+1);

            jstring jpass = (*env)->NewStringUTF(env, pass);
            jstring jserial = (*env)->NewStringUTF(env, serial);
            (*env)->CallVoidMethod(env, obj, on_key_computed, jpass, jserial, (jint)mx, (jint)0);
            (*env)->DeleteLocalRef(env, jpass);
            (*env)->DeleteLocalRef(env, jserial);
          }
        }
      }
    }
  }
}

