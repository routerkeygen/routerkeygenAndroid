package org.exobel.routerkeygen;

import java.lang.ref.WeakReference;

import android.os.Handler;

import com.millennialmedia.android.MMAdView;

public class RefreshHandler extends Handler
{
	private static final long TIME_TO_REFRESH_IN_MILLIS = 15000;
	private static final int MSG_REFRESH_BANNER = 4;
	private WeakReference<MMAdView> mmAdViewRef;

	public RefreshHandler(MMAdView adView)
	{
		mmAdViewRef = new WeakReference<MMAdView>(adView);
	}

	@Override
	public void handleMessage(android.os.Message msg)
	{
		switch(msg.what)
		{
		case MSG_REFRESH_BANNER:
			if(mmAdViewRef != null)
			{
				MMAdView adView = mmAdViewRef.get();
				if(adView != null)
				{
					adView.getAd();
					sendEmptyMessageDelayed(MSG_REFRESH_BANNER, TIME_TO_REFRESH_IN_MILLIS);
				}
			}
			break;
		}
	};

	public void onPause()
	{
		removeMessages(MSG_REFRESH_BANNER);
	}

	public void onResume()
	{
		sendEmptyMessage(MSG_REFRESH_BANNER);
	}
}
