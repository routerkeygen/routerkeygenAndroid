/*
 * Copyright 2013 Rui Araújo, Luís Fonseca
 *
 * This file is part of Router Keygen.
 *
 * Router Keygen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Router Keygen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Router Keygen.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.exobel.routerkeygen.ui;

import it.gmariotti.changelibs.library.view.ChangeLogListView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.exobel.routerkeygen.AdsUtils;
import org.exobel.routerkeygen.DictionaryDownloadService;
import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.UpdateCheckerService;
import org.exobel.routerkeygen.UpdateCheckerService.LastVersion;
import org.exobel.routerkeygen.utils.HashUtils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;

@SuppressWarnings("deprecation")
public class Preferences extends SherlockPreferenceActivity {

	/** The maximum supported dictionary version */
	public static final int MAX_DIC_VERSION = 4;

	public static final String dicLocalPref = "dictionaryPath";
	public static final String wifiOnPref = "wifion";
	public static final String thomson3gPref = "thomson3g";
	public static final String nativeCalcPref = "nativethomson";
	public static final String autoScanPref = "autoScan";
	public static final String analyticsPref = "analytics_enabled";
	public static final String autoScanIntervalPref = "autoScanInterval";

	public final static String GOOGLE_PLAY_DOWNLOADER = "org.doublecheck.wifiscanner";

	public static final String PUB_DOWNLOAD = "https://github.com/routerkeygen/thomsonDicGenerator/releases/download/v3/RouterKeygen_v3.dic";
	private static final String PUB_DIC_CFV = "https://github.com/routerkeygen/thomsonDicGenerator/releases/download/v3/RKDictionary.cfv";

	public static final String VERSION = "3.9.1";
	private static final String LAUNCH_DATE = "11/09/2014";

	private LastVersion lastVersion;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		findPreference("download").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						if (isDictionaryServiceRunning()) {
							Toast.makeText(
									getBaseContext(),
									getString(R.string.pref_msg_download_running),
									Toast.LENGTH_SHORT).show();
							return true;
						}
						ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo netInfo = cm.getActiveNetworkInfo();
						if (netInfo == null
								|| !netInfo.isConnectedOrConnecting()) {
							Toast.makeText(getBaseContext(),
									getString(R.string.pref_msg_no_network),
									Toast.LENGTH_SHORT).show();
							return true;
						}

						// Don't complain about dictionary size if user is on a
						// wifi connection
						if ((((WifiManager) getBaseContext().getSystemService(
								Context.WIFI_SERVICE))).getConnectionInfo()
								.getSSID() != null) {
							try {
								checkCurrentDictionary();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						} else
							showDialog(DIALOG_ASK_DOWNLOAD);
						return true;
					}
				});

		boolean app_installed = AdsUtils.checkDonation(this);
		final PreferenceCategory mCategory = (PreferenceCategory) findPreference("2section");
		if (!app_installed) {
			mCategory.removePreference(findPreference("analytics_enabled"));
			// If you haven't the donate app installed remove the paypal donate
			// link.
			mCategory.removePreference(findPreference("donate_paypal"));
			findPreference("donate_playstore").setOnPreferenceClickListener(
					new OnPreferenceClickListener() {
						public boolean onPreferenceClick(Preference preference) {
							try {
								startActivity(new Intent(Intent.ACTION_VIEW,
										Uri.parse("market://details?id="
												+ GOOGLE_PLAY_DOWNLOADER)));
							} catch (android.content.ActivityNotFoundException anfe) {
								startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://play.google.com/store/apps/details?id="
												+ GOOGLE_PLAY_DOWNLOADER)));
							}
							Toast.makeText(getApplicationContext(),
									R.string.msg_donation, Toast.LENGTH_LONG)
									.show();
							return true;
						}
					});
		} else {
			// If you have the donate app installed no need to link to it.
			mCategory.removePreference(findPreference("donate_playstore"));
			findPreference("donate_paypal").setOnPreferenceClickListener(
					new OnPreferenceClickListener() {
						public boolean onPreferenceClick(Preference preference) {
							final String donateLink = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=V3FFBTRTTV5DN";
							Uri uri = Uri.parse(donateLink);
							startActivity(new Intent(Intent.ACTION_VIEW, uri));

							return true;
						}
					});
		}

		findPreference("update").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
							protected void onPreExecute() {
								showDialog(DIALOG_WAIT);
							}

							protected Void doInBackground(Void... params) {
								lastVersion = UpdateCheckerService
										.getLatestVersion();
								return null;
							}

							protected void onPostExecute(Void result) {
								removeDialog(DIALOG_WAIT);
								if (isFinishing())
									return;
								if (lastVersion == null) {
									showDialog(DIALOG_ERROR);
									return;
								}
								if (!Preferences.VERSION
										.equals(lastVersion.version)) {
									showDialog(DIALOG_UPDATE_NEEDED);
								} else {
									Toast.makeText(Preferences.this,
											R.string.msg_app_updated,
											Toast.LENGTH_SHORT).show();
								}

							}
						};
						if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
							task.execute();
						} else {
							task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}
						// Checking for updates every week
						startService(new Intent(getApplicationContext(),
								UpdateCheckerService.class));
						return true;
					}
				});
		findPreference("changelog").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						showDialog(DIALOG_CHANGELOG);
						return true;
					}
				});
		findPreference("about").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						showDialog(DIALOG_ABOUT);
						return true;
					}
				});
		findPreference(dicLocalPref).setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						startActivityForResult(new Intent(
								getApplicationContext(),
								FileChooserActivity.class), 0);
						return true;
					}
				});
		final CheckBoxPreference autoScan = (CheckBoxPreference) findPreference("autoScan");
		autoScan.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				findPreference("autoScanInterval").setEnabled(
						autoScan.isChecked());
				return true;

			}
		});
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		findPreference("autoScanInterval").setEnabled(
				prefs.getBoolean(Preferences.autoScanPref, getResources()
						.getBoolean(R.bool.autoScanDefault)));
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				// The URI of the selected file
				final Uri uri = data.getData();
				// Create a File from this Uri
				File file = FileUtils.getFile(this, uri);
				final SharedPreferences customSharedPreference = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				final SharedPreferences.Editor editor = customSharedPreference
						.edit();
				editor.putString(dicLocalPref, file.getAbsolutePath());
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
					editor.apply();
				else
					new Thread(new Runnable() {
						public void run() {
							editor.commit();
						}
					}).start();
			}
		}
	}

	private boolean isDictionaryServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("org.exobel.routerkeygen.DictionaryDownloadService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this,
					NetworksListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	private static final int DIALOG_ABOUT = 1001;
	private static final int DIALOG_ASK_DOWNLOAD = 1002;
	private static final int DIALOG_WAIT = 1003;
	private static final int DIALOG_ERROR_TOO_ADVANCED = 1004;
	private static final int DIALOG_ERROR = 1005;
	private static final int DIALOG_UPDATE_NEEDED = 1006;
	private static final int DIALOG_CHANGELOG = 1007;

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new Builder(this);
		switch (id) {
		case DIALOG_ABOUT: {
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.about_dialog,
					(ViewGroup) findViewById(R.id.tabhost));
			TabHost tabs = (TabHost) layout.findViewById(R.id.tabhost);
			tabs.setup();
			TabSpec tspec1 = tabs.newTabSpec("about");
			tspec1.setIndicator(getString(R.string.pref_about));

			tspec1.setContent(R.id.text_about_scroll);
			TextView text = ((TextView) layout.findViewById(R.id.text_about));
			text.setMovementMethod(LinkMovementMethod.getInstance());
			text.append(VERSION + "\n" + LAUNCH_DATE);
			tabs.addTab(tspec1);
			TabSpec tspec2 = tabs.newTabSpec("credits");
			tspec2.setIndicator(getString(R.string.dialog_about_credits));
			tspec2.setContent(R.id.about_credits_scroll);
			((TextView) layout.findViewById(R.id.about_credits))
					.setMovementMethod(LinkMovementMethod.getInstance());
			tabs.addTab(tspec2);
			TabSpec tspec3 = tabs.newTabSpec("license");
			tspec3.setIndicator(getString(R.string.dialog_about_license));
			tspec3.setContent(R.id.about_license_scroll);
			((TextView) layout.findViewById(R.id.about_license))
					.setMovementMethod(LinkMovementMethod.getInstance());
			tabs.addTab(tspec3);
			builder.setNeutralButton(R.string.bt_close, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DIALOG_ABOUT);

				}
			});
			builder.setView(layout);
			break;
		}
		case DIALOG_ASK_DOWNLOAD: {
			DialogInterface.OnClickListener diOnClickListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// Check if we have the latest dictionary version.
					try {
						checkCurrentDictionary();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			builder.setTitle(R.string.pref_download);
			builder.setMessage(R.string.msg_dicislarge);
			builder.setCancelable(false);
			builder.setPositiveButton(android.R.string.yes, diOnClickListener);
			builder.setNegativeButton(android.R.string.no,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							removeDialog(DIALOG_ASK_DOWNLOAD);
						}
					});
			break;
		}
		case DIALOG_UPDATE_NEEDED: {
			builder.setTitle(R.string.update_title)
					.setMessage(
							getString(R.string.update_message,
									lastVersion.version))
					.setNegativeButton(R.string.bt_close,
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									removeDialog(DIALOG_UPDATE_NEEDED);
								}
							})
					.setPositiveButton(R.string.bt_website,
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									startActivity(new Intent(Intent.ACTION_VIEW)
											.setData(Uri.parse(lastVersion.url)));
								}
							});
			break;
		}
		case DIALOG_WAIT: {
			ProgressDialog pbarDialog = new ProgressDialog(Preferences.this);
			pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pbarDialog.setMessage(getString(R.string.msg_wait));
			return pbarDialog;
		}
		case DIALOG_ERROR_TOO_ADVANCED: {
			builder.setTitle(R.string.msg_error).setMessage(
					R.string.msg_err_online_too_adv);
			break;
		}
		case DIALOG_ERROR: {
			builder.setTitle(R.string.msg_error).setMessage(
					R.string.msg_err_unkown);
			break;
		}
		case DIALOG_CHANGELOG: {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ChangeLogListView chgList = (ChangeLogListView) layoutInflater
					.inflate(R.layout.dialog_changelog, null);
			builder.setTitle(R.string.pref_changelog)
					.setView(chgList)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();
								}
							});
			break;
		}
		}
		return builder.create();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void checkCurrentDictionary() throws FileNotFoundException {
		final String myDicFile = PreferenceManager.getDefaultSharedPreferences(
				getBaseContext()).getString(dicLocalPref, null);
		if (myDicFile == null) {
			removeDialog(DIALOG_ASK_DOWNLOAD);
			startService(new Intent(getApplicationContext(),
					DictionaryDownloadService.class).putExtra(
					DictionaryDownloadService.URL_DOWNLOAD, PUB_DOWNLOAD));
		} else {
			AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
				protected void onPreExecute() {
					removeDialog(DIALOG_ASK_DOWNLOAD);
					showDialog(DIALOG_WAIT);
				}

				private final static int TOO_ADVANCED = 1;
				private final static int OK = 0;
				private final static int DOWNLOAD_NEEDED = -1;
				private final static int ERROR_NETWORK = -2;
				private final static int ERROR = -3;

				protected Integer doInBackground(Void... params) {

					// Comparing this version with the online
					// version
					try {
						HttpURLConnection con = (HttpURLConnection) new URL(
								PUB_DIC_CFV).openConnection();
						DataInputStream dis = new DataInputStream(
								con.getInputStream());

						byte[] cfvTable = new byte[18];
						dis.read(cfvTable);
						dis.close();
						con.disconnect();

						InputStream is = new FileInputStream(myDicFile);
						byte[] dicVersion = new byte[2];
						// Check our version
						is.read(dicVersion);
						is.close();
						int thisVersion, onlineVersion;
						thisVersion = dicVersion[0] << 8 | dicVersion[1];
						onlineVersion = cfvTable[0] << 8 | cfvTable[1];

						if (thisVersion == onlineVersion) {
							// It is the latest version, but is
							// it not corrupt?
							byte[] dicHash = new byte[16];
							for (int i = 2; i < 18; ++i)
								dicHash[i - 2] = cfvTable[i];
							if (HashUtils.checkDicMD5(
									new File(myDicFile).getPath(), dicHash)) {
								// All is well
								return OK;
							}
						}
						if (onlineVersion > thisVersion
								&& onlineVersion > MAX_DIC_VERSION) {
							// Online version is too advancedv
							return TOO_ADVANCED;
						}
						return DOWNLOAD_NEEDED;
					} catch (FileNotFoundException e) {
						return DOWNLOAD_NEEDED;
					} catch (UnknownHostException e) {
						return ERROR_NETWORK;
					} catch (Exception e) {
						return ERROR;
					}
				}

				protected void onPostExecute(Integer result) {
					removeDialog(DIALOG_WAIT);
					if (isFinishing())
						return;
					if (result == null) {
						showDialog(DIALOG_ERROR);
						return;
					}
					switch (result) {
					case ERROR:
						showDialog(DIALOG_ERROR);
						break;
					case ERROR_NETWORK:
						Toast.makeText(Preferences.this,
								R.string.msg_errthomson3g, Toast.LENGTH_SHORT)
								.show();
						break;
					case DOWNLOAD_NEEDED:
						startService(new Intent(getApplicationContext(),
								DictionaryDownloadService.class).putExtra(
								DictionaryDownloadService.URL_DOWNLOAD,
								PUB_DOWNLOAD));
						break;
					case OK:
						Toast.makeText(
								getBaseContext(),
								getResources().getString(
										R.string.msg_dic_updated),
								Toast.LENGTH_SHORT).show();
						break;
					case TOO_ADVANCED:
						showDialog(DIALOG_ERROR_TOO_ADVANCED);
						break;
					}

				}
			};
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
				task.execute();
			} else {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}
	}

};
