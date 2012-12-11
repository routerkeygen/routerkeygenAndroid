package org.exobel.routerkeygen;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dE5pQldqS3Vycjh6Ui1zejlLOHd3dlE6MQ") 
public class RouterKeygenApplication extends Application {
  @Override
  public void onCreate() {
    ACRA.init(this);
    super.onCreate();
  }
}	