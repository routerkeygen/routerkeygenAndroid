/*
 * KeygenThread.cpp
 *
 *  Created on: 1 de Ago de 2012
 *      Author: ruka
 */

#include "KeygenThread.h"

KeygenThread::KeygenThread( Keygen * router): router(router) {}

KeygenThread::~KeygenThread() {
}

void KeygenThread::run(){
	results = router->getResults();
}

QVector<QString> KeygenThread::getResults() const {
	return results;
}




