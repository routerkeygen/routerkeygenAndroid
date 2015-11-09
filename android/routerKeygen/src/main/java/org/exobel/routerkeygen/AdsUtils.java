package org.exobel.routerkeygen;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import com.millennialmedia.InterstitialAd;
import com.millennialmedia.MMSDK;
import com.millennialmedia.InlineAd;
import com.millennialmedia.MMException;

import java.lang.ref.WeakReference;

public class AdsUtils {
    private static final String BANNER_APID = "136973";
    private static final String CONNECT_APID = "200804";
    private static final String STARTUP_APID = "201332";
    private static final String CONNECT_LAST_SHOWN_TIME = "CONNECT_LAST_SHOWN_TIME";
    private static final String STARTUP_LAST_SHOWN_TIME = "STARTUP_LAST_SHOWN_TIME";
    private static final String CONNECT_LAST_SHOWN_COUNT = "CONNECT_LAST_SHOWN_COUNT";
    private static final String STARTUP_LAST_SHOWN_COUNT = "STARTUP_LAST_SHOWN_COUNT";
    private static final int COUNTER_LIMIT = 5;

    private AdsUtils() {
    }

    public static void loadAdIfNeeded(final Activity activity) {
        final String TAG = activity.getLocalClassName();
        final RelativeLayout adRelativeLayout = (RelativeLayout) activity
                .findViewById(R.id.adBannerRelativeLayout);
        if (checkDonation(activity)) {
            adRelativeLayout.getLayoutParams().height = 0;
            return;
        }
        MMSDK.initialize(activity);
        // Create the adView
        try {
            final InlineAd inlineAd = InlineAd.createInstance(BANNER_APID, adRelativeLayout);
            // set a refresh rate of 30 seconds that will be applied after the first request
            inlineAd.setRefreshInterval(15000);
            InlineAd.AdSize adSize = InlineAd.AdSize.BANNER;
            // (Optional) Set the ad size
            if (canFit(activity.getResources(), InlineAd.AdSize.LEADERBOARD.width)) {
                adSize = InlineAd.AdSize.LEADERBOARD;
            } else if (canFit(activity.getResources(), InlineAd.AdSize.FULL_BANNER.width)) {
                adSize = InlineAd.AdSize.FULL_BANNER;
            }
            final float scale = activity.getResources().getDisplayMetrics().density;
            final int adHeight = (int) (adSize.height * scale + 0.5f);
            final WeakReference<RelativeLayout> adLayoutRef = new WeakReference<RelativeLayout>(adRelativeLayout);
            // The InlineAdMetadata instance is used to pass additional metadata to the server to
            // improve ad selection
            final InlineAd.InlineAdMetadata inlineAdMetadata = new InlineAd.InlineAdMetadata().
                    setAdSize(adSize);
            inlineAd.setListener(new InlineAd.InlineListener() {
                @Override
                public void onRequestSucceeded(InlineAd inlineAd) {
                    Log.i(TAG, "Inline Ad loaded.");
                    final RelativeLayout ad = adLayoutRef.get();
                    if (ad != null) {
                        ad.getLayoutParams().height = adHeight;
                    }
                }
                @Override
                public void onRequestFailed(InlineAd inlineAd, InlineAd.InlineErrorStatus errorStatus) {
                    Log.i(TAG, errorStatus.toString());
                }
                @Override
                public void onClicked(InlineAd inlineAd) {
                    Log.i(TAG, "Inline Ad clicked.");
                }
                @Override
                public void onResize(InlineAd inlineAd, int width, int height) {
                    Log.i(TAG, "Inline Ad starting resize.");
                }
                @Override
                public void onResized(InlineAd inlineAd, int width, int height, boolean toOriginalSize) {
                    Log.i(TAG, "Inline Ad resized.");
                }
                @Override
                public void onExpanded(InlineAd inlineAd) {
                    Log.i(TAG, "Inline Ad expanded.");
                }
                @Override
                public void onCollapsed(InlineAd inlineAd) {
                    Log.i(TAG, "Inline Ad collapsed.");
                }
                @Override
                public void onAdLeftApplication(InlineAd inlineAd) {
                    Log.i(TAG, "Inline Ad left application.");
                }
            });

            inlineAd.request(inlineAdMetadata);
        } catch (MMException e) {
            Log.e(TAG, "Error creating inline ad", e);
            // abort loading ad
        }

    }


    public static void displayStartupInterstitial(Activity activity) {
        displayInterstitial(activity, STARTUP_APID, STARTUP_LAST_SHOWN_COUNT,
                STARTUP_LAST_SHOWN_TIME);
    }

    public static void displayConnectInterstitial(Activity activity) {
        displayInterstitial(activity, CONNECT_APID, CONNECT_LAST_SHOWN_COUNT,
                CONNECT_LAST_SHOWN_TIME);
    }

    private static void displayInterstitial(final Activity activity,
                                            final String apid, final String countKey, final String timeKey) {
        final String TAG = activity.getLocalClassName();
        if (checkDonation(activity)) {
            return; // NO ADS!
        }
        MMSDK.initialize(activity);
        final SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(activity);
        final SharedPreferences.Editor editor = mPrefs.edit();
        int counter = mPrefs.getInt(countKey, 0);
        final long timePassed = System.currentTimeMillis()
                - mPrefs.getLong(timeKey, 0);
        counter++;
        editor.putInt(countKey, counter);
        editor.apply();
        if (timePassed > DateUtils.WEEK_IN_MILLIS || counter >= COUNTER_LIMIT) try {
            final InterstitialAd interstitialAd = InterstitialAd.createInstance(apid);
            interstitialAd.setListener(new InterstitialAd.InterstitialListener() {
                @Override
                public void onLoaded(InterstitialAd interstitialAd) {
                    Log.i(TAG, "Interstitial Ad loaded.");
                    // Show the Ad using the display options you configured.
                    try {
                        interstitialAd.show(activity);
                    } catch (MMException e) {
                        Log.i(activity.getLocalClassName(), "Unable to show interstitial ad content, exception occurred");
                        e.printStackTrace();
                    }
                }
                @Override
                public void onLoadFailed(InterstitialAd interstitialAd,
                                         InterstitialAd.InterstitialErrorStatus errorStatus) {
                    Log.i(TAG, "Interstitial Ad load failed.");
                }
                @Override
                public void onShown(InterstitialAd interstitialAd) {
                    editor.putInt(countKey, 0);
                    editor.putLong(timeKey, System.currentTimeMillis());
                    editor.apply();
                    Log.i(TAG, "Interstitial Ad shown.");
                }
                @Override
                public void onShowFailed(InterstitialAd interstitialAd,
                                         InterstitialAd.InterstitialErrorStatus errorStatus) {
                    Log.i(TAG, "Interstitial Ad show failed.");
                }
                @Override
                public void onClosed(InterstitialAd interstitialAd) {
                    Log.i(TAG, "Interstitial Ad closed.");
                }
                @Override
                public void onClicked(InterstitialAd interstitialAd) {
                    Log.i(TAG, "Interstitial Ad clicked.");
                }
                @Override
                public void onAdLeftApplication(InterstitialAd interstitialAd) {
                    Log.i(TAG, "Interstitial Ad left application.");
                }
                @Override
                public void onExpired(InterstitialAd interstitialAd) {
                    Log.i(TAG, "Interstitial Ad expired.");
                }
            });
            interstitialAd.load(activity, null);
        } catch (MMException e) {
            Log.e(activity.getLocalClassName(), "Error creating interstitial ad", e);
            // abort loading ad
        }

    }

    public static boolean checkDonation(Activity activity) {
        final PackageManager pm = activity.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo("org.exobel.routerkeygendownloader",
                    PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        if (!app_installed) {
            try {
                pm.getPackageInfo("org.doublecheck.wifiscanner",
                        PackageManager.GET_ACTIVITIES);
                app_installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                app_installed = false;
            }
        }
        return app_installed;
    }

    // Determine if the requested adWidth can fit on the screen.
    private static boolean canFit(Resources res, int adWidth) {
        int adWidthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, adWidth, res.getDisplayMetrics());
        DisplayMetrics metrics = res.getDisplayMetrics();
        return metrics.widthPixels >= adWidthPx;
    }

}
