#ifndef WELCOMEDIALOG_H
#define WELCOMEDIALOG_H
#include <QDialog>


namespace Ui {
     class WelcomeDialog;
 }

class WelcomeDialog : public QDialog
{
    Q_OBJECT
public:
    explicit WelcomeDialog(QWidget *parent = 0);

    virtual ~WelcomeDialog();
private:
    Ui::WelcomeDialog *ui;

private slots:
    void donatePaypal();
    void donateGooglePlay();
    
};

#endif // WELCOMEDIALOG_H
