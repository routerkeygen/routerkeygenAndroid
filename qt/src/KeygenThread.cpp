/*
 * KeygenThread.cpp
 *
 *  Created on: 1 de Ago de 2012
 *      Author: ruka
 */

#include "KeygenThread.h"

KeygenThread::KeygenThread( Keygen * router): router(router) , error(false){}

KeygenThread::~KeygenThread() {
}

void KeygenThread::run(){
	try{
	results = router->getResults();
	} catch (int e){
		error = true;
	}
}

QVector<QString> KeygenThread::getResults() const {
	return results;
}

bool KeygenThread::hadError() const{
	return error;
}



