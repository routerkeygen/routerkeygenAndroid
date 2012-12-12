package org.exobel.routerkeygen;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dHhJWWdhVDZOTnAwaWZMTngxY1pYY0E6MQ") 
public class RouterKeygenApplication extends Application {
  @Override
  public void onCreate() {
    ACRA.init(this);
    super.onCreate();
  }
}	