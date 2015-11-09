package org.exobel.routerkeygen;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.millennialmedia.MMSDK;
import com.millennialmedia.UserData;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.exobel.routerkeygen.ui.NetworkActivity;
import org.exobel.routerkeygen.ui.NetworksListActivity;
import org.exobel.routerkeygen.ui.Preferences;

@ReportsCrashes(mailTo = "exobel@gmail.com", customReportContent = {
        ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
        ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA,
        ReportField.STACK_TRACE, ReportField.LOGCAT}, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class RouterKeygenApplication extends Application {

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.analytics);
            mTracker.enableAdvertisingIdCollection(true);
        }
        return mTracker;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ACRA.init(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            UserData userData = new UserData().
            setEthnicity(UserData.Ethnicity.HISPANIC);
            MMSDK.setUserData(userData);
        } catch (Exception e) {
            e.printStackTrace();
        }
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