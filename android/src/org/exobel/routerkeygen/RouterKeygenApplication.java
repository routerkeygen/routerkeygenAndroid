package org.exobel.routerkeygen;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import com.millennialmedia.android.MMSDK;

import android.app.Application;

@ReportsCrashes(formKey = "", mailTo = "exobel@gmail.com", customReportContent = {
		ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
		ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA,
		ReportField.STACK_TRACE, ReportField.LOGCAT }, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class RouterKeygenApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			ACRA.init(this);
		} catch (Exception e) {

		}
		try {
			MMSDK.initialize(this);
		} catch (Exception e) {

		}
	}
}