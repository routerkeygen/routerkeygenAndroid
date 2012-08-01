/*
 * KeygenThread.h
 *
 *  Created on: 1 de Ago de 2012
 *      Author: ruka
 */

#ifndef KEYGENTHREAD_H_
#define KEYGENTHREAD_H_

#include <QThread>
#include "Keygen.h"

class KeygenThread: public QThread {
public:
	KeygenThread(Keygen *);
	virtual ~KeygenThread();
	QVector<QString> getResults() const;
protected:
	void run();
private:
	Keygen * router;
	QVector<QString> results;
};

#endif /* KEYGENTHREAD_H_ */
