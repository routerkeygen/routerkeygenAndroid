/*
 * KeygenThread.h
 *
 *  Created on: 1 de Ago de 2012
 *      Author: ruka
 */

#ifndef KEYGENTHREAD_H_
#define KEYGENTHREAD_H_
#include <QVector>
#include <QString>
#include <QThread>

class Keygen;
class KeygenThread: public QThread {
public:
	KeygenThread(Keygen *);
	virtual ~KeygenThread();
	QVector<QString> getResults() const;
	bool hadError() const;
protected:
	void run();
private:
	Keygen * router;
	QVector<QString> results;
	bool error;
};

#endif /* KEYGENTHREAD_H_ */
