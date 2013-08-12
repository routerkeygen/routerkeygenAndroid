package org.exobel.routerkeygen;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.ViewGroup;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class AdsUtils {
	private AdsUtils() {
	}
	
	public static void loadAdIfNeeded(Activity activity){
		boolean app_installed = checkDonation(activity);
		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) activity.findViewById(R.id.adView);
		if (app_installed) {
			final ViewGroup vg = (ViewGroup) adView.getParent();
			vg.removeView(adView);
		} else
			adView.loadAd(new AdRequest());
	}
	
	public static boolean checkDonation(Activity activity){
		final PackageManager pm = activity.getPackageManager();
		boolean app_installed = false;
		try {
			pm.getPackageInfo("org.exobel.routerkeygendownloader",
					PackageManager.GET_ACTIVITIES);
			app_installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		if ( !app_installed ){
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
