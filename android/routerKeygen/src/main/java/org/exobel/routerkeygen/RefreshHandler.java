package org.exobel.routerkeygen;

import android.os.Handler;

import com.millennialmedia.android.MMAdView;

import java.lang.ref.WeakReference;

public class RefreshHandler extends Handler {
	private static final long TIME_TO_REFRESH_IN_MILLIS = 15000;
	private static final int MSG_REFRESH_BANNER = 4;
	private final WeakReference<MMAdView> mmAdViewRef;

	public RefreshHandler(MMAdView adView) {
		mmAdViewRef = new WeakReference<>(adView);
	}

	@Override
	public void handleMessage(android.os.Message msg) {
		switch (msg.what) {
			case MSG_REFRESH_BANNER:
				MMAdView adView = mmAdViewRef.get();
				if (adView != null) {
                    adView.getAd();
                    sendEmptyMessageDelayed(MSG_REFRESH_BANNER, TIME_TO_REFRESH_IN_MILLIS);
                }
				break;
		}
	}

	public void onPause() {
		removeMessages(MSG_REFRESH_BANNER);
	}

	public void onResume() {
		sendEmptyMessage(MSG_REFRESH_BANNER);
	}
}
