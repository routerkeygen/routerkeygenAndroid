package org.exobel.routerkeygendownloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String donateScreenShownPref = "donateScreenShown";
	boolean welcomeScreenShown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final SharedPreferences mPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		welcomeScreenShown = mPrefs.getBoolean(donateScreenShownPref, false);

		if (!welcomeScreenShown) {

			final String whatsNewTitle = getString(R.string.app_name);
			final String whatsNewText = getString(R.string.msg_welcome_text_donate);
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(whatsNewTitle)
					.setMessage(whatsNewText)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
			final SharedPreferences.Editor editor = mPrefs.edit();
			editor.putBoolean(donateScreenShownPref, true);
			editor.commit();
		}
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://code.google.com/p/android-thomson-key-solver/downloads/list")));
				Toast.makeText(getApplicationContext(), R.string.download_desc,
						Toast.LENGTH_SHORT).show();

			}
		});
	}

}
