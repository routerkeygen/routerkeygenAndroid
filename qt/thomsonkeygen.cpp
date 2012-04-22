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
#include <QFile>
#include <QDataStream>
#include <QIODevice>

static char charectbytes0[] = {
        '3','3','3','3','3','3',
        '3','3','3','3','4','4',
        '4','4','4','4','4','4',
        '4','4','4','4','4','4',
        '4','5','5','5','5','5',
        '5','5','5','5','5','5',
        };

static char charectbytes1[] = {
        '0','1','2','3','4','5',
        '6','7','8','9','1','2',
        '3','4','5','6','7','8',
        '9','A','B','C','D','E',
        'F','0','1','2','3','4',
        '5','6','7','8','9','A',
        };

ThomsonKeygen::ThomsonKeygen( WifiNetwork * router , bool t) : KeygenThread(router) ,
                                table(NULL) , entry(NULL) ,  len(0) , thomson3g(t){
    this->hash = new QCryptographicHash(QCryptographicHash::Sha1);
    table = NULL;
}

ThomsonKeygen::~ThomsonKeygen(){
    delete hash;
    delete [] table;
    delete [] entry;
}

void ThomsonKeygen::run(){
    if ( !localCalc() )
    {
        nativeCalc();
        return;
    }
    if ( results.isEmpty() )
        results.append("keine results");

}


void ThomsonKeygen::nativeCalc(){
    QString result;
    int n = sizeof(dic)/sizeof("AAA");
    char input[13];
    input[0] = 'C';
    input[1] = 'P';
    for( int i = 0 ; i < n; ++i  )
      {
          sprintf( input + 6  , "%02X%02X%02X" , (int)dic[i][0]
                                  , (int)dic[i][1], (int)dic[i][2] );
          if ( stopRequested )
              return;
          for ( int year = 4 ; year <= 9 ; ++year )
          {
              for ( int week = 1 ; week <= 52 ; ++week )
              {
                  input[2] = '0' + year/10;
                  input[3] = '0' + year % 10 ;
                  input[4] = '0' + week / 10;
                  input[5] = '0' + week % 10;
                  hash->reset();
                  hash->addData(input,12);
                  result = QString::fromAscii(hash->result().toHex().data());

                  if (  result.right(6) == router->getSSID().right(6) )
                  {
                        this->results.append(result.toUpper().left(10));
                  }


              }
          }
      }
}

bool ThomsonKeygen::localCalc(){

    QFile file("RouterKeygen.dic");


    if ( !file.open(QIODevice::ReadOnly) ) {
       return false;
    }

    unsigned char routerESSID[3];
    unsigned int routerSSIDint = strtol( router->getSSIDsubpart().toUtf8().data(), NULL , 16);
    routerESSID[0] = routerSSIDint >> 16;
    routerESSID[1] = (routerSSIDint >> 8) & 0xFF;
    routerESSID[2] = routerSSIDint & 0xFF;
    QDataStream fis( &file );
    int version = 0;
    table = new char[1282];
    if ( fis.readRawData(table, 1282) == -1 )
    {

            return false;
    }
    version = table[0] << 8 | table[1];
    int totalOffset = 0;
    int offset = 0;
    int lastLength = 0 , length = 0;
    if ( table[( 0xFF &routerESSID[0] )*5 + 2 ] == routerESSID[0] )
    {
            int i = ( 0xFF &routerESSID[0] )*5 + 2;
            offset =( (0xFF & table[i + 1]) << 24 ) | ( (0xFF & table[i + 2])  << 16 ) |
                            ( (0xFF & table[i + 3])  << 8 ) | (0xFF & table[i + 4]);
            if ( (0xFF & table[i]) != 0xFF )
                    lastLength = ( (0xFF & table[i + 6]) << 24 ) | ( (0xFF & table[i + 7])  << 16 ) |
                            ( (0xFF & table[i + 8])  << 8 ) | (0xFF & table[i + 9]);
    }
    totalOffset += offset;
    fis.skipRawData(totalOffset-1282);
    if ( fis.readRawData(table,1024) == -1 )
    {
            return false;
    }
        if ( table[( 0xFF &routerESSID[1] )*4] == routerESSID[1] )
    {
            int i = ( 0xFF &routerESSID[1] )*4;
            offset =( (0xFF & table[i + 1])  << 16 ) |
                            ( (0xFF & table[i + 2])  << 8 ) | (0xFF & table[i + 3]);
            length =  ( (0xFF & table[i + 5])  << 16 ) |
                            ( (0xFF & table[i + 6])  << 8 ) | (0xFF & table[i + 7]);

    }
    totalOffset += offset;
    length -= offset;
    if ( ( lastLength != 0 ) && ( (0xFF & routerESSID[1] ) == 0xFF ) )
    {
            /*Only for SSID starting with XXFF. We use the next item on the main table
            to know the length of the sector we are looking for. */
            lastLength -= totalOffset;
            length = lastLength;
    }
    fis.skipRawData( offset - 1024 );
    if ( ( (0xFF & routerESSID[0] ) != 0xFF ) || ( (0xFF & routerESSID[1] ) != 0xFF  ) )
    {
            entry = new char[length];
            len = fis.readRawData(entry,length);
    }
    else
    { /*Only for SSID starting with FFFF as we don't have a marker of the end.*/
                    entry = new char[2000];
                    len = fis.readRawData( entry , 2000);
    }
    if ( len == -1 )
    {
           //problems
            return false;
    }

    int year = 4;
    int week = 1;
    int i = 0 , j = 0;
    char input[13];
    unsigned int sequenceNumber;
    unsigned int inc = 0;
    char * message_digest;
    int a,b,c;
    input[0] = 'C';
    input[1] = 'P';
    input[2] = '0';
    sequenceNumber =0;
    QString result;
    for( i = 0; i < len; i+=2  )
    {
            sequenceNumber += ( (0xFF & entry[i])  << 8 ) | (0xFF & entry[i+1]);
            for ( j = 0 ; j < 18 ; ++j )
            {
                    inc = j* ( 36*36*36*6*3);
                    year = ( (sequenceNumber+inc) / ( 36*36*36 )% 6) + 4 ;
                    week = (sequenceNumber+inc) / ( 36*36*36*6 )  + 1 ;
                    c = sequenceNumber % 36;
                    b = sequenceNumber/36 % 36;
                    a = sequenceNumber/(36*36) % 36;

                    input[3] = '0' + year % 10 ;
                    input[4] = '0' + week / 10;
                    input[5] = '0' + week % 10;
                    input[6] = charectbytes0[a];
                    input[7] = charectbytes1[a];
                    input[8] = charectbytes0[b];
                    input[9] = charectbytes1[b];
                    input[10] = charectbytes0[c];
                    input[11] = charectbytes1[c];
                    hash->reset();
                    hash->addData(input,12);
                    message_digest =  hash->result().data();

                    if( ( memcmp(&message_digest[17],&routerESSID[0],3) == 0) ){
                        result = QString::fromAscii(hash->result().toHex().data());
                        this->results.append(result.toUpper().left(10));
                    }
            }
    }

   return true;
}

