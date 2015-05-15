package org.exobel.routerkeygen;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.millennialmedia.android.MMAd;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMInterstitial;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;
import com.millennialmedia.android.RequestListener.RequestListenerImpl;

public class AdsUtils {
	private AdsUtils() {
	}

	// Constants for tablet sized ads (728x90)
	private static final int IAB_LEADERBOARD_WIDTH = 728;
	private static final int IAB_LEADERBOARD_HEIGHT = 90;

	private static final int MED_BANNER_WIDTH = 480;
	private static final int MED_BANNER_HEIGHT = 60;

	// Constants for phone sized ads (320x50)
	private static final int BANNER_AD_WIDTH = 320;
	private static final int BANNER_AD_HEIGHT = 50;

	private static final String BANNER_APID = "136973";
	private static final String CONNECT_APID = "200804";
	private static final String STARTUP_APID = "201332";

	private static final String CONNECT_LAST_SHOWN_TIME = "CONNECT_LAST_SHOWN_TIME";
	private static final String STARTUP_LAST_SHOWN_TIME = "STARTUP_LAST_SHOWN_TIME";
	private static final String CONNECT_LAST_SHOWN_COUNT = "CONNECT_LAST_SHOWN_COUNT";
	private static final String STARTUP_LAST_SHOWN_COUNT = "STARTUP_LAST_SHOWN_COUNT";

	public static MMAdView loadAdIfNeeded(Activity activity) {
		// Create the adView
		MMAdView adView = new MMAdView(activity);
		if (checkDonation(activity)) {
			final View vg = activity.findViewById(R.id.adBannerRelativeLayout);
			((ViewGroup) vg.getParent()).removeView(adView);
			return null;
		}
		// Set your apid
		adView.setApid(BANNER_APID);
		adView.setMMRequest(getAdRequest(activity));
		// (Highly Recommended) Set the id to preserve your ad on
		// configuration changes. Save Battery!
		// Each MMAdView you give requires a unique id.
		adView.setId(MMSDK.getDefaultAdId());

		int placementWidth = BANNER_AD_WIDTH;
		int placementHeight = BANNER_AD_HEIGHT;

		// (Optional) Set the ad size
		if (canFit(activity.getResources(), IAB_LEADERBOARD_WIDTH)) {
			placementWidth = IAB_LEADERBOARD_WIDTH;
			placementHeight = IAB_LEADERBOARD_HEIGHT;
		} else if (canFit(activity.getResources(), MED_BANNER_WIDTH)) {
			placementWidth = MED_BANNER_WIDTH;
			placementHeight = MED_BANNER_HEIGHT;
		}

		// (Optional) Set the AdView size based on the placement size. You
		// could use WRAP_CONTENT and not specify the placement size
		adView.setWidth(placementWidth);
		adView.setHeight(placementHeight);
		adView.getAd();
		// Add the adview to the view layout
		RelativeLayout adRelativeLayout = (RelativeLayout) activity
				.findViewById(R.id.adBannerRelativeLayout);
		int layoutWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, placementWidth, activity
						.getResources().getDisplayMetrics());
		int layoutHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, placementHeight, activity
						.getResources().getDisplayMetrics());
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				layoutWidth, layoutHeight);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		adRelativeLayout.addView(adView, layoutParams);
		return adView;
	}

	private static MMRequest getAdRequest(Activity activity) {
		// Acquire a reference to the system Location Manager
		final LocationManager locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);
		final MMRequest adRequest = new MMRequest();
		final Location location = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location != null) {
			MMRequest.setUserLocation(location);
		}
		Map<String, String> metaData = new HashMap<String, String>();
		metaData.put(MMRequest.KEY_ETHNICITY, MMRequest.ETHNICITY_HISPANIC);
		adRequest.setMetaValues(metaData);
		return adRequest;
	}

	public static void displayStartupInterstitial(Activity activity) {
		displayInterstitial(activity, STARTUP_APID, STARTUP_LAST_SHOWN_COUNT,
				STARTUP_LAST_SHOWN_TIME);
	}

	public static void displayConnectInterstitial(Activity activity) {
		displayInterstitial(activity, CONNECT_APID, CONNECT_LAST_SHOWN_COUNT,
				CONNECT_LAST_SHOWN_TIME);
	}

	private static final int COUNTER_LIMIT = 5;

	private static void displayInterstitial(Activity activity,
			final String apid, final String countKey, final String timeKey) {
		if (checkDonation(activity)) {
			return; // NO ADS!
		}
		final SharedPreferences mPrefs = PreferenceManager
				.getDefaultSharedPreferences(activity);
		final SharedPreferences.Editor editor = mPrefs.edit();
		int counter = mPrefs.getInt(countKey, 0);
		final long timePassed = System.currentTimeMillis()
				- mPrefs.getLong(timeKey, 0);
		counter++;
		editor.putInt(countKey, counter);
		editor.apply();
		if (timePassed > DateUtils.WEEK_IN_MILLIS || counter >= COUNTER_LIMIT) {
			final MMInterstitial interstitial = new MMInterstitial(activity);

			// Add the MMRequest object to your MMInterstitial.
			interstitial.setMMRequest(getAdRequest(activity));
			interstitial.setApid(apid);
			interstitial.setListener(new RequestListenerImpl() {
				@Override
				public void requestCompleted(MMAd mmAd) {
					interstitial.display(); // display the ad that was cached by
											// fetch
					editor.putInt(countKey, 0);
					editor.putLong(timeKey, System.currentTimeMillis());
					editor.apply();
				}
			});
			interstitial.fetch(); // request ad to be cached locally
		}

	}

	public static boolean checkDonation(Activity activity) {
		final PackageManager pm = activity.getPackageManager();
		boolean app_installed = false;
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
