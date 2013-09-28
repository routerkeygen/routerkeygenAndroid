package org.exobel.routerkeygen;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.view.ViewGroup;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;

public class AdsUtils {
	private AdsUtils() {
	}

	public static MMAdView loadAdIfNeeded(Activity activity) {
		boolean app_installed = checkDonation(activity);
		// Look up the AdView as a resource and load a request.
		MMAdView adView = (MMAdView) activity.findViewById(R.id.adView);
		if (app_installed) {
			final ViewGroup vg = (ViewGroup) adView.getParent();
			vg.removeView(adView);
			return null;
		} else {
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
			adView.setMMRequest(adRequest);
			adView.getAd();
		}
		return adView;
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

}
