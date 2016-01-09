//
// Created by Dusan Klinec on 06.01.16.
//

#ifndef ANDROID_UPC_KEYS_H
#define ANDROID_UPC_KEYS_H

#define MAGIC_24GHZ 0xff8d8f20
#define MAGIC_5GHZ 0xffd9da60
#define MAGIC0 0xb21642c9ll
#define MAGIC1 0x68de3afll
#define MAGIC2 0x6b5fca6bll

#define MAX0 9
#define MAX1 99
#define MAX2 9
#define MAX3 9999
#define MAX_ITERATIONS ((long)((MAX0)+1L)*(long)((MAX1)+1L)*(long)((MAX2)+1L)*(long)((MAX3)+1L))

#ifdef __cplusplus
extern "C" {
#endif
extern void hash2pass(uint8_t *in_hash, char *out_pass);
extern uint32_t mangle(uint32_t *pp);
extern uint32_t upc_generate_ssid(uint32_t* data, uint32_t magic);
extern void compute_wpa2(int mode, char * serial, char * pass);
#ifdef __cplusplus
}
#endif

#endif //ANDROID_UPC_KEYS_H
