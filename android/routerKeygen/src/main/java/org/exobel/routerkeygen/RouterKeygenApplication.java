package org.exobel.routerkeygen;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import org.exobel.routerkeygen.ui.NetworkActivity;
import org.exobel.routerkeygen.ui.NetworksListActivity;
import org.exobel.routerkeygen.ui.Preferences;

public class RouterKeygenApplication extends Application {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                StrictMode
                        .setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                .detectAll().penaltyLog().build());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog()
                            .setClassInstanceLimit(
                                    CancelOperationActivity.class, 2)
                            .setClassInstanceLimit(NetworksListActivity.class,
                                    2)
                            .setClassInstanceLimit(NetworkActivity.class, 2)
                            .setClassInstanceLimit(Preferences.class, 2)
                            .build());
                } else {
                    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                            .detectLeakedSqlLiteObjects().penaltyLog().build());

                }
            }
        }
    }
}