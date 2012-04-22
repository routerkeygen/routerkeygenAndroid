#ifndef SHA256_H
#define SHA256_H
#include <stdint.h>
#define	SHA256_HASH_SIZE		32

/*
 * Context block for SHA-256.
 */
class SHA256
{
        private:
                unsigned char	input[64];
                uint32_t		inputLen;
                uint32_t		A, B, C, D, E, F, G, H;
                uint64_t		totalLen;
                void ProcessBlock(const unsigned char *block);
                void WriteLong(unsigned char *buf, uint32_t value);
        public:
                SHA256();
                void reset();
                void result(unsigned char hash[SHA256_HASH_SIZE]);
                void addData( const void *buffer, unsigned long len);
} ;






#endif
