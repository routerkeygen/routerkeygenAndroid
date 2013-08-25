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
#include "TelseyKeygen.h"
#include <stdint.h>
#include <cstring>
TelseyKeygen::TelseyKeygen(QString ssid, QString mac) :
		Keygen(ssid, mac) {}

QVector<QString> & TelseyKeygen::getKeys() {
    if ( getMacAddress() == ""  )
    {
            throw ERROR;
    }
    uint32_t * key = scrambler(getMacAddress());
    uint32_t seed = 0;

    for (int x = 0; x < 64; x++) {
            seed = hashword(key,x, seed);
    }

    QString S1 = "";
    S1.setNum(seed,16);
    while ( S1.length() < 8 )
            S1 = "0" + S1;


    for ( int x = 0; x <64; x++) {
                if (x <8)
                    key[x] =( key[x]<< 3 ) & 0xFFFFFFFF;
                else if ( x<16)
                    key[x] >>= 5;
                else if (x < 32 )
                    key[x] >>= 2;
                else
                    key[x] =( key[x]<< 7 ) & 0xFFFFFFFF;
    }

    seed = 0;
    for (int x = 0; x < 64; x++) {
            seed = hashword(key, x, seed);
    }
    QString S2 =  "";
    S2.setNum(seed,16);
    while ( S2.length() < 8 )
            S2 = "0" + S2;
    results.append(S1.right(5) +  S2.left(5));
    return results;
}

//Scramble Function
uint32_t  * TelseyKeygen::scrambler(QString mac){
        uint32_t  * vector = new uint32_t [64];
        bool status;
        char macValue[6];
        for (int i = 0; i < 12; i += 2)
                macValue[i / 2] = (mac.mid(i,1).toInt(&status, 16) << 4)
                                + mac.mid(i + 1,1).toInt(&status, 16);

        vector[0] =( ( ( 0xFF & macValue[5] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[0] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[1] =( ( ( 0xFF & macValue[1] ) << 24 )|( ( 0xFF & macValue[0] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[2] =( ( ( 0xFF & macValue[4] ) << 24 )|( ( 0xFF & macValue[2] ) << 16 ) |
                           ( ( 0xFF & macValue[3] ) << 8 )|( 0xFF & macValue[2] ) );
        vector[3] =( ( ( 0xFF & macValue[4] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[2] ) );
        vector[4] =( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[4] ) << 16 ) |
                   ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[0] ) );
        vector[5] =( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[5] ) << 16 ) |
                           ( ( 0xFF & macValue[3] ) << 8 )|( 0xFF & macValue[1] ) );
        vector[6] =( ( ( 0xFF & macValue[0] ) << 24 )|( ( 0xFF & macValue[4] ) << 16 ) |
                           ( ( 0xFF & macValue[0] ) << 8 )|( 0xFF & macValue[1] ) );
        vector[7] =( ( ( 0xFF & macValue[1] ) << 24 )|( ( 0xFF & macValue[4] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[0] ) );
        vector[8] =( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[4] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[2] ) );
        vector[9] =( ( ( 0xFF & macValue[3] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[3] ) << 8 )|( 0xFF & macValue[4] ) );
        vector[10]=( ( ( 0xFF & macValue[4] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[4] ) << 8 )|( 0xFF & macValue[3] ) );
        vector[11]=( ( ( 0xFF & macValue[5] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[12]=( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[0] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[13]=( ( ( 0xFF & macValue[1] ) << 24 )|( ( 0xFF & macValue[0] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[1] ) );
        vector[14]=( ( ( 0xFF & macValue[4] ) << 24 )|( ( 0xFF & macValue[2] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[3] ) );
        vector[15]=( ( ( 0xFF & macValue[3] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[2] ) );
        vector[16]=( ( ( 0xFF & macValue[4] ) << 24 )|( ( 0xFF & macValue[4] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[4] ) );
        vector[17]=( ( ( 0xFF & macValue[5] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[4] ) << 8 )|( 0xFF & macValue[0] ) );
        vector[18]=( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[5] ) << 16 ) |
                           ( ( 0xFF & macValue[0] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[19]=( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[3] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[20]=( ( ( 0xFF & macValue[5] ) << 24 )|( ( 0xFF & macValue[2] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[4] ) );
        vector[21]=( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[4] ) );
        vector[22]=( ( ( 0xFF & macValue[0] ) << 24 )|( ( 0xFF & macValue[4] ) << 16 ) |
                           ( ( 0xFF & macValue[4] ) << 8 )|( 0xFF & macValue[3] ) );
        vector[23]=( ( ( 0xFF & macValue[3] ) << 24 )|( ( 0xFF & macValue[0] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[3] ) );
        vector[24]=( ( ( 0xFF & macValue[4] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[0] ) << 8 )|( 0xFF & macValue[0] ) );
        vector[25]=( ( ( 0xFF & macValue[3] ) << 24 )|( ( 0xFF & macValue[2] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[1] ) );
        vector[26]=( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[27]=( ( ( 0xFF & macValue[1] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[4] ) << 8 )|( 0xFF & macValue[3] ) );
        vector[28]=( ( ( 0xFF & macValue[0] ) << 24 )|( ( 0xFF & macValue[2] ) << 16 ) |
                           ( ( 0xFF & macValue[3] ) << 8 )|( 0xFF & macValue[4] ) );
        vector[29]=( ( ( 0xFF & macValue[0] ) << 24 )|( ( 0xFF & macValue[0] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[2] ) );
        vector[30]=( ( ( 0xFF & macValue[0] ) << 24 )|( ( 0xFF & macValue[0] ) << 16 ) |
                           ( ( 0xFF & macValue[0] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[31]=( ( ( 0xFF & macValue[1] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[4] ) );
        vector[32]=( ( ( 0xFF & macValue[4] ) << 24 )|( ( 0xFF & macValue[0] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[2] ) );
        vector[33]=( ( ( 0xFF & macValue[3] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[3] ) << 8 )|( 0xFF & macValue[0] ) );
        vector[34]=( ( ( 0xFF & macValue[0] ) << 24 )|( ( 0xFF & macValue[2] ) << 16 ) |
                           ( ( 0xFF & macValue[4] ) << 8 )|( 0xFF & macValue[1] ) );
        vector[35]=( ( ( 0xFF & macValue[5] ) << 24 )|( ( 0xFF & macValue[5] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[0] ) );
        vector[36]=( ( ( 0xFF & macValue[0] ) << 24 )|( ( 0xFF & macValue[4] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[0] ) );
        vector[37]=( ( ( 0xFF & macValue[1] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[2] ) );
        vector[38]=( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[2] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[1] ) );
        vector[39]=( ( ( 0xFF & macValue[3] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[3] ) );
        vector[40]=( ( ( 0xFF & macValue[1] ) << 24 )|( ( 0xFF & macValue[0] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[4] ) );
        vector[41]=( ( ( 0xFF & macValue[1] ) << 24 )|( ( 0xFF & macValue[5] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[42]=( ( ( 0xFF & macValue[0] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[4] ) << 8 )|( 0xFF & macValue[0] ) );
        vector[43]=( ( ( 0xFF & macValue[1] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[4] ) );
        vector[44]=( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[2] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[2] ) );
        vector[45]=( ( ( 0xFF & macValue[3] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[3] ) << 8 )|( 0xFF & macValue[3] ) );
        vector[46]=( ( ( 0xFF & macValue[5] ) << 24 )|( ( 0xFF & macValue[4] ) << 16 ) |
                           ( ( 0xFF & macValue[0] ) << 8 )|( 0xFF & macValue[1] ) );
        vector[47]=( ( ( 0xFF & macValue[4] ) << 24 )|( ( 0xFF & macValue[0] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[48]=( ( ( 0xFF & macValue[1] ) << 24 )|( ( 0xFF & macValue[0] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[0] ) );
        vector[49]=( ( ( 0xFF & macValue[0] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[1] ) );
        vector[50]=( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[2] ) << 16 ) |
                           ( ( 0xFF & macValue[4] ) << 8 )|( 0xFF & macValue[2] ) );
        vector[51]=( ( ( 0xFF & macValue[3] ) << 24 )|( ( 0xFF & macValue[4] ) << 16 ) |
                       ( ( 0xFF & macValue[4] ) << 8 )|( 0xFF & macValue[3] ) );
        vector[52]=( ( ( 0xFF & macValue[4] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[53]=( ( ( 0xFF & macValue[5] ) << 24 )|( ( 0xFF & macValue[5] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[4] ) );
        vector[54]=( ( ( 0xFF & macValue[3] ) << 24 )|( ( 0xFF & macValue[0] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[55]=( ( ( 0xFF & macValue[3] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[0] ) << 8 )|( 0xFF & macValue[4] ) );
        vector[56]=( ( ( 0xFF & macValue[4] ) << 24 )|( ( 0xFF & macValue[2] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[5] ) );
        vector[57]=( ( ( 0xFF & macValue[4] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[3] ) << 8 )|( 0xFF & macValue[1] ) );
        vector[58]=( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[4] ) << 16 ) |
                           ( ( 0xFF & macValue[3] ) << 8 )|( 0xFF & macValue[0] ) );
        vector[59]=( ( ( 0xFF & macValue[2] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[5] ) << 8 )|( 0xFF & macValue[1] ) );
        vector[60]=( ( ( 0xFF & macValue[3] ) << 24 )|( ( 0xFF & macValue[1] ) << 16 ) |
                           ( ( 0xFF & macValue[2] ) << 8 )|( 0xFF & macValue[3] ) );
        vector[61]=( ( ( 0xFF & macValue[5] ) << 24 )|( ( 0xFF & macValue[0] ) << 16 ) |
                           ( ( 0xFF & macValue[1] ) << 8 )|( 0xFF & macValue[2] ) );
        vector[62]=( ( ( 0xFF & macValue[5] ) << 24 )|( ( 0xFF & macValue[3] ) << 16 ) |
                           ( ( 0xFF & macValue[4] ) << 8 )|( 0xFF & macValue[1] ) );
        vector[63]=( ( ( 0xFF & macValue[0] ) << 24 )|( ( 0xFF & macValue[2] ) << 16 ) |
                           ( ( 0xFF & macValue[3] ) << 8 )|( 0xFF & macValue[0] ) );

        return vector;
}

//lookup3.c, by Bob Jenkins, May 2006, Public Domain.
#define rot(x,k) (((x)<<(k)) | ((x)>>(32-(k))))

#define mix(a,b,c) \
{ \
  a -= c;  a ^= rot(c, 4);  c += b; \
  b -= a;  b ^= rot(a, 6);  a += c; \
  c -= b;  c ^= rot(b, 8);  b += a; \
  a -= c;  a ^= rot(c,16);  c += b; \
  b -= a;  b ^= rot(a,19);  a += c; \
  c -= b;  c ^= rot(b, 4);  b += a; \
}

#define final(a,b,c) \
{ \
  c ^= b; c -= rot(b,14); \
  a ^= c; a -= rot(c,11); \
  b ^= a; b -= rot(a,25); \
  c ^= b; c -= rot(b,16); \
  a ^= c; a -= rot(c,4);  \
  b ^= a; b -= rot(a,14); \
  c ^= b; c -= rot(b,24); \
}
uint32_t TelseyKeygen::hashword(
const uint32_t * k,                   /* the key, an array of uint32_t values */
size_t          length,               /* the length of the key, in uint32_ts */
uint32_t        initval)         /* the previous hash, or an arbitrary value */
{
    uint32_t a,b,c;

        /* Set up the internal state */
        a = b = c = 0xdeadbeef + (((uint32_t)length)<<2) + initval;

        /*------------------------------------------------- handle most of the key */
        while (length > 3)
        {
          a += k[0];
          b += k[1];
          c += k[2];
          mix(a,b,c);
          length -= 3;
          k += 3;
        }

        /*------------------------------------------- handle the last 3 uint32_t's */
        switch(length)                     /* all the case statements fall through */
        {
        case 3 : c+=k[2];
        /* no break */
        case 2 : b+=k[1];
        /* no break */
        case 1 : a+=k[0];
          final(a,b,c);
          /* no break */
        case 0:     /* case 0: nothing left to add */
          break;
        }
        /*------------------------------------------------------ report the result */
        return c;
      }
