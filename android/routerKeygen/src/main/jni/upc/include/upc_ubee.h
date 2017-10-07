//
// Created by Dusan Klinec on 06.01.16.
//

#ifndef ANDROID_UPC_KEYS_UBEE_H
#define ANDROID_UPC_KEYS_UBEE_H

#ifdef __cplusplus
extern "C" {
#endif

int ubee_generate_ssid(unsigned const char * mac, unsigned char * ssid, size_t * len);
int ubee_generate_pass(unsigned const char * mac, unsigned char * pass, size_t * len);

#ifdef __cplusplus
}
#endif
#endif //ANDROID_UPC_KEYS_UBEE_H
