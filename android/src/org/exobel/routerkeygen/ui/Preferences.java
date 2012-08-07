/*
 * Copyright 2012 Rui Araújo, Luís Fonseca
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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Stack;
import java.util.TreeSet;

import org.exobel.routerkeygen.DictionaryDownloadService;
import org.exobel.routerkeygen.Downloader;
import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.utils.HashUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

@SuppressWarnings("deprecation")
public class Preferences extends SherlockPreferenceActivity {

	/** The maximum supported dictionary version */
	public static final int MAX_DIC_VERSION = 4;

	ProgressDialog pbarDialog;
	Downloader downloader;
	long myProgress = 0, fileLen;
	long lastt, now = 0, downloadBegin = 0;

	public static final String folderSelectPref = "folderSelect";
	public static final String wifiOnPref = "wifion";
	public static final String thomson3gPref = "thomson3g";
	public static final String nativeCalcPref = "nativethomson";
	public static final String manualMacPref = "manual_mac";
	public static final String PUB_DOWNLOAD = "http://android-thomson-key-solver.googlecode.com/files/RKDictionary.dic";
	private static final String PUB_DIC_CFV = "http://android-thomson-key-solver.googlecode.com/svn/trunk/RKDictionary.cfv";
	private static final String PUB_VERSION = "http://android-thomson-key-solver.googlecode.com/svn/trunk/RouterKeygenVersion.txt";

	private static final String VERSION = "2.9.1";
	private static final String LAUNCH_DATE = "04/01/2012";
	private String version = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		findPreference("download").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
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
						// wifi
						// connection
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

		findPreference("donate").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						String donateLink = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=V3FFBTRTTV5DN";
						Uri uri = Uri.parse(donateLink);
						startActivity(new Intent(Intent.ACTION_VIEW, uri));

						return true;
					}
				});
		findPreference("update").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						new AsyncTask<Void, Void, Integer>() {
							protected void onPreExecute() {
								showDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
							}

							protected Integer doInBackground(Void... params) {

								// Comparing this version with the online
								// version
								try {
									URLConnection con = new URL(PUB_VERSION)
											.openConnection();
									DataInputStream dis = new DataInputStream(
											con.getInputStream());
									final byte[] versionData = new byte[6];
									dis.read(versionData);
									version = new String(versionData).trim();

									// Check our version
									if (VERSION.equals(version)) {
										// All is well
										return 1;
									}
									return 0;

								} catch (UnknownHostException e) {
									return -1;
								} catch (Exception e) {
									return null;
								}
							}

							protected void onPostExecute(Integer result) {
								removeDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
								if (isFinishing())
									return;
								if (result == null) {
									showDialog(DIALOG_ERROR);
									return;
								}
								switch (result) {
								case -1:
									Toast.makeText(Preferences.this,
											R.string.msg_errthomson3g,
											Toast.LENGTH_SHORT).show();
									break;
								case 0:
									showDialog(DIALOG_UPDATE_NEEDED);
									break;
								case 1:
									Toast.makeText(Preferences.this,
											R.string.msg_app_updated,
											Toast.LENGTH_SHORT).show();
									break;
								}

							}
						}.execute();
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
		findPreference("folderSelect").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						mPath = new File(Environment
								.getExternalStorageDirectory() + File.separator);
						mChosenFile = File.separator;
						directoryTree.clear();
						showDialog(DIALOG_LOAD_FOLDER);
						return true;
					}
				});
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

	private static final String TAG = "ThomsonPreferences";
	private String[] mFileList;

	private File mPath = new File(Environment.getExternalStorageDirectory()
			+ File.separator);
	private String mChosenFile = File.separator;
	Stack<String> directoryTree = new Stack<String>();

	private void loadFolderList() {
		mPath = new File(Environment.getExternalStorageDirectory()
				+ File.separator + mChosenFile);
		if (mPath.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					return sel.isDirectory();
				}
			};
			mFileList = mPath.list(filter);
			if (mFileList == null)
				return;
			TreeSet<String> sorter = new TreeSet<String>();
			for (int i = 0; i < mFileList.length; ++i)
				sorter.add(mFileList[i]);
			mFileList = sorter.toArray(mFileList);
		} else {
			if (!directoryTree.empty()) {
				mChosenFile = directoryTree.pop();
				loadFolderList();
			} else
				mFileList = null;
		}
	}

	private static final int DIALOG_LOAD_FOLDER = 1000;
	private static final int DIALOG_ABOUT = 1001;
	private static final int DIALOG_ASK_DOWNLOAD = 1002;
	private static final int DIALOG_CHECK_DOWNLOAD_SERVER = 1003;
	private static final int DIALOG_ERROR_TOO_ADVANCED = 1004;
	private static final int DIALOG_DOWNLOAD = 1005;
	private static final int DIALOG_ERROR = 1006;
	private static final int DIALOG_ERROR_NOSD = 1007;
	private static final int DIALOG_ERROR_NOMEMORYONSD = 1008;
	private static final int DIALOG_CHECKING_DOWNLOAD = 1009;
	private static final int DIALOG_UPDATE_NEEDED = 1011;

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new Builder(this);
		switch (id) {
		case DIALOG_LOAD_FOLDER: {
			loadFolderList();
			builder.setTitle(getString(R.string.folder_chooser_title));
			if (mFileList == null || mFileList.length == 0) {
				Log.e(TAG, "Showing file picker before loading the file list");
				mFileList = new String[1];
				mFileList[0] = getString(R.string.folder_chooser_no_dir);
				builder.setItems(mFileList,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
			} else
				builder.setItems(mFileList,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								directoryTree.push(mChosenFile);
								mChosenFile += File.separator
										+ mFileList[which];
								removeDialog(DIALOG_LOAD_FOLDER);
								showDialog(DIALOG_LOAD_FOLDER);
							}
						});
			if (!mChosenFile.equals(File.separator))
				builder.setNegativeButton(R.string.bt_choose_back,
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (!directoryTree.empty())
									mChosenFile = directoryTree.pop();
								else
									mChosenFile = File.separator;
								removeDialog(DIALOG_LOAD_FOLDER);
								showDialog(DIALOG_LOAD_FOLDER);
							}
						});
			builder.setNeutralButton(R.string.bt_choose, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences customSharedPreference = PreferenceManager
							.getDefaultSharedPreferences(getBaseContext());
					SharedPreferences.Editor editor = customSharedPreference
							.edit();

					editor.putString(folderSelectPref, mPath.toString());
					editor.commit();
					String path = mPath.toString();
					mPath = new File(path + File.separator + "RouterKeygen.dic");
					File second = new File(path + File.separator
							+ "RKDictionary.dic");
					if (!mPath.exists() && !second.exists()) {
						Toast.makeText(
								getBaseContext(),
								getResources().getString(
										R.string.pref_msg_notfound)
										+ " " + path, Toast.LENGTH_SHORT)
								.show();
					} else {
						if (mPath.exists())
							Toast.makeText(
									getBaseContext(),
									mPath.toString()
											+ " "
											+ getResources().getString(
													R.string.pref_msg_found),
									Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(
									getBaseContext(),
									second.toString()
											+ " "
											+ getResources().getString(
													R.string.pref_msg_found),
									Toast.LENGTH_SHORT).show();
					}
				}
			});

			break;
		}
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
					.setMessage(getString(R.string.update_message, version))
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
									String url = "http://code.google.com/p/android-thomson-key-solver/downloads/list";
									Intent i = new Intent(Intent.ACTION_VIEW);
									i.setData(Uri.parse(url));
									startActivity(i);
								}
							});
			break;
		}
		case DIALOG_CHECK_DOWNLOAD_SERVER: {
			pbarDialog = new ProgressDialog(Preferences.this);
			pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pbarDialog.setMessage(getString(R.string.msg_wait));
			return pbarDialog;
		}
		case DIALOG_ERROR_TOO_ADVANCED: {
			builder.setTitle(R.string.msg_error).setMessage(
					R.string.msg_err_online_too_adv);
			break;
		}
		case DIALOG_DOWNLOAD: {
			pbarDialog = new ProgressDialog(Preferences.this);
			pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pbarDialog.setMessage(getString(R.string.msg_dl_estimating));
			pbarDialog.setMax(100);
			pbarDialog.setTitle(R.string.msg_dl_dlingdic);
			pbarDialog.setCancelable(true);
			pbarDialog.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					if (downloader != null)
						downloader.setStopRequested(true);
				}
			});
			pbarDialog.setButton(getString(R.string.bt_pause),
					new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (downloader != null)
								downloader.setStopRequested(true);
							removeDialog(DIALOG_DOWNLOAD);
						}
					});
			pbarDialog.setButton2(getString(R.string.bt_manual_cancel),
					new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (downloader != null) {
								downloader.setDeleteTemp(true);
								downloader.setStopRequested(true);
							}
							removeDialog(DIALOG_DOWNLOAD);
						}
					});
			return pbarDialog;
		}
		case DIALOG_ERROR: {
			builder.setTitle(R.string.msg_error).setMessage(
					R.string.msg_err_unkown);
			break;
		}
		case DIALOG_ERROR_NOMEMORYONSD: {
			builder.setTitle(R.string.msg_error).setMessage(
					R.string.msg_nomemoryonsdcard);
			break;
		}
		case DIALOG_ERROR_NOSD: {
			builder.setTitle(R.string.msg_error).setMessage(
					R.string.msg_nosdcard);
			break;
		}
		case DIALOG_CHECKING_DOWNLOAD: {
			pbarDialog = new ProgressDialog(Preferences.this);
			pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pbarDialog.setMessage(getString(R.string.msg_wait));
			return pbarDialog;
		}
		}
		return builder.create();
	}

	private void checkCurrentDictionary() throws FileNotFoundException {
		final File myDicFile = getDictionaryFile();
		if (myDicFile == null) {
			removeDialog(DIALOG_ASK_DOWNLOAD);
			startService(new Intent(getApplicationContext(),
					DictionaryDownloadService.class).putExtra(
					DictionaryDownloadService.URL_DOWNLOAD, PUB_DOWNLOAD));
		} else {
			new AsyncTask<Void, Void, Integer>() {
				protected void onPreExecute() {
					removeDialog(DIALOG_ASK_DOWNLOAD);
					showDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
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
						InputStream is = new FileInputStream(myDicFile);
						URLConnection con = new URL(PUB_DIC_CFV)
								.openConnection();
						DataInputStream dis = new DataInputStream(
								con.getInputStream());
						if (con.getContentLength() != 18)
							throw new Exception();

						byte[] cfvTable = new byte[18];
						dis.read(cfvTable);

						byte[] dicVersion = new byte[2];
						// Check our version
						is.read(dicVersion);

						int thisVersion, onlineVersion;
						thisVersion = dicVersion[0] << 8 | dicVersion[1];
						onlineVersion = cfvTable[0] << 8 | cfvTable[1];

						if (thisVersion == onlineVersion) {
							// It is the latest version, but is
							// it not corrupt?
							byte[] dicHash = new byte[16];
							for (int i = 2; i < 18; ++i)
								dicHash[i - 2] = cfvTable[i];
							if (HashUtils.checkDicMD5(myDicFile.getPath(),
									dicHash)) {
								// All is well
								return OK;
							}
						}
						if (onlineVersion > thisVersion
								&& onlineVersion > MAX_DIC_VERSION) {
							// Online version is too advanced
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
					removeDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
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
						Toast.makeText(Preferences.this, R.string.msg_errthomson3g,
								Toast.LENGTH_SHORT).show();
						break;
					case DOWNLOAD_NEEDED:
						startService(new Intent(getApplicationContext(),
								DictionaryDownloadService.class).putExtra(
								DictionaryDownloadService.URL_DOWNLOAD, PUB_DOWNLOAD));
						break;
					case OK:
						Toast.makeText(getBaseContext(),
								getResources().getString(R.string.msg_dic_updated),
								Toast.LENGTH_SHORT).show();
						break;
					case TOO_ADVANCED:
						showDialog(DIALOG_ERROR_TOO_ADVANCED);
						break;
					}

				}
			}.execute();
		}
	}

	private File getDictionaryFile() throws FileNotFoundException {
		String folderSelect = PreferenceManager.getDefaultSharedPreferences(
				getBaseContext()).getString(folderSelectPref,
				Environment.getExternalStorageDirectory().getAbsolutePath());
		String firstName = folderSelect + File.separator + "RouterKeygen.dic";
		String secondName = folderSelect + File.separator + "RKDictionary.dic";
		try {
			File dic = new File(firstName);
			if (dic.exists())
				return dic;
			dic = new File(secondName);
			if (dic.exists())
				return dic;
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new FileNotFoundException("Permissions Error");
		}
		return null;
	}
};
