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

package org.exobel.routerkeygen;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Stack;
import java.util.TreeSet;

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
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class Preferences extends PreferenceActivity {
	
	/** The maximum supported dictionary version */
	public static final int MAX_DIC_VERSION = 3;

	ProgressDialog pbarDialog;
	Downloader downloader;
	long myProgress = 0, fileLen;
	long lastt, now = 0, downloadBegin = 0;
	
	byte[] dicVersion = new byte [2];
	static byte[] cfvTable = new byte[18];
	
	public static final String PUB_DOWNLOAD = 
		"http://android-thomson-key-solver.googlecode.com/files/RKDictionary.dic";
	private static final String PUB_DIC_CFV =
		"http://android-thomson-key-solver.googlecode.com/svn/trunk/RKDictionary.cfv";
	private static final String PUB_VERSION =
		"http://android-thomson-key-solver.googlecode.com/svn/trunk/RouterKeygenVersion.txt";

	private static final String folderSelectPref = "folderSelect";
	private static final String VERSION = "2.9.1";
	private static final String LAUNCH_DATE = "04/01/2012";
	private String version ="";
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
	
		
		findPreference("download").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference)
					{
						ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					    NetworkInfo netInfo = cm.getActiveNetworkInfo();
					    if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
					    	Toast.makeText(getBaseContext(),getString(R.string.pref_msg_no_network),
								Toast.LENGTH_SHORT).show();
					        return true;
					    }
						
						showDialog(DIALOG_ASK_DOWNLOAD);
						return true;
					}
				});
		
		findPreference("donate").setOnPreferenceClickListener(
  				new OnPreferenceClickListener() {
  					public boolean onPreferenceClick(Preference preference)
  					{
  						String donateLink = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=V3FFBTRTTV5DN";
  						Uri uri = Uri.parse(donateLink );
  			    		startActivity( new Intent( Intent.ACTION_VIEW, uri ) );

  						return true;
  					}
  				});
		findPreference("update").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						new AsyncTask<Void, Void, Integer>() {
  							protected void onPreExecute(){
  									showDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
  							}
  							
  							protected Integer doInBackground(Void... params) {

								// Comparing this version with the online version
								try {
									URLConnection con = new URL(PUB_VERSION).openConnection();
									DataInputStream dis = new DataInputStream(con.getInputStream());
									final byte [] versionData = new byte[6];
									dis.read(versionData);
									version = new String(versionData).trim();
									
									// Check our version
									if(VERSION.equals(version))
									{
										// All is well
										return 1;
									}
									return 0;
									
								}  catch ( UnknownHostException e ){
									return -1;
								}
								catch (Exception e)
								{
									return null;
								}
  							}
  				      
  							protected void onPostExecute(Integer result ){
  								removeDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
  								if (isFinishing())
  									return;
  								if ( result == null )
  								{
  									showDialog(DIALOG_ERROR);
  									return;
  								}
  								switch ( result )
  								{
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
		findPreference("folderSelect").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				mPath = new File(Environment.getExternalStorageDirectory() + File.separator);
				mChosenFile = File.separator;
				directoryTree.clear();
				showDialog(DIALOG_LOAD_FOLDER);
				return true;
			}
		});
	}
	
	private void checkDownload(){
		showDialog(DIALOG_CHECKING_DOWNLOAD);
		new Thread(new Runnable() {
			public void run() {
				try
				{
					String folderSelect = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext()).getString(folderSelectPref, 
							Environment.getExternalStorageDirectory().getAbsolutePath());

					String dicTemp = Environment.getExternalStorageDirectory().getPath() + File.separator + "DicTemp.dic";
					if(!checkDicMD5(dicTemp))
					{
						new File(dicTemp).delete();
						messHand.sendEmptyMessage(-1);
						return;
					}
					if (!renameFile(Environment.getExternalStorageDirectory().getPath() + File.separator + "DicTemp.dic" ,
							folderSelect + File.separator + "RouterKeygen.dic" , true ))
					{
						messHand.sendEmptyMessage(8);
						return;
					}
				}
				catch (Exception e)
				{
					messHand.sendEmptyMessage(-1);
					return;
				}
				messHand.sendEmptyMessage(9);
			}
		}).start();
	}
	
	// Check RouterKeygen.dic file through md5
	private boolean checkDicMD5(String dicFile)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			InputStream is = new FileInputStream(dicFile);
			try {
				is = new DigestInputStream(is, md);
				byte []  buffer = new byte [16384] ; 
				while ( is.read ( buffer )  != -1 );
			}
			finally {
				is.close();
			}
			byte[] digest = md.digest();

			URLConnection con = new URL(PUB_DIC_CFV).openConnection();
			DataInputStream dis = new DataInputStream(con.getInputStream());
			if(con.getContentLength() != 18)
				throw new Exception();
			
			dis.read(Preferences.cfvTable);

			for(int i = 0; i < 16; ++i)
				if(digest[i] != cfvTable[i + 2])
					return false;
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	// Download the dictionary
	private void startDownload() {
		showDialog(DIALOG_DOWNLOAD);
		myProgress = 0;
		downloader = new Downloader(messHand , PUB_DOWNLOAD);
		downloader.start();
		lastt = downloadBegin = System.currentTimeMillis();
	}
	int downloadBefore = 0;
	Handler messHand = new Handler() {

		public void handleMessage(Message msg) {
			
			switch(msg.what)
			{
			case -1:
				removeDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
				removeDialog(DIALOG_CHECKING_DOWNLOAD);
				removeDialog(DIALOG_DOWNLOAD);
				if (!isFinishing()) {
				showDialog(DIALOG_ERROR);
				}
			break;
			case 0:
				removeDialog(DIALOG_DOWNLOAD);
				if (!isFinishing()) {
					showDialog(DIALOG_ERROR_NOSD);
				}
				break;
			case 1:
				removeDialog(DIALOG_DOWNLOAD);
				if (!isFinishing()) {
					showDialog(DIALOG_ERROR_NOMEMORYONSD);
				}
				break;
			case 2:
				downloadBefore = msg.arg1;
				fileLen = msg.arg2;
				break;
			case 3:
				removeDialog(DIALOG_DOWNLOAD);
				checkDownload();				
				break;
			case 4:
				now = System.currentTimeMillis();
				if(now - lastt < 1000 )
					break;
				
				myProgress = msg.arg1 ;
				fileLen = msg.arg2;
				if ( fileLen == 0 )
					break;
				
				long kbs = 0;
				try{
					 kbs = (((myProgress- downloadBefore) / (now - downloadBegin))*1000/1024);
					 
				}catch(ArithmeticException e){
					kbs = 0;
				}
				if(kbs == 0)
					break;
				long eta = (fileLen - myProgress) / kbs / 1024;
				long progress = ( 100L * myProgress )/ fileLen;
				pbarDialog.setProgress((int)progress);
				pbarDialog.setMessage(getString(R.string.msg_dl_speed) + ": "
						+ kbs + "kb/s\n"
						+ getString(R.string.msg_dl_eta) + ": "
						+ (eta > 60 ? eta/60 + "m" : eta + "s"));
				lastt = now;
				break;
			case 5:
				removeDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
				if (!isFinishing()) {
					showDialog(DIALOG_ERROR_TOO_ADVANCED);
				}
				break;
			case 6:
				removeDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
				if (!isFinishing()) {
					Toast.makeText(getBaseContext(),getResources().getString(R.string.msg_dic_updated),
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 7: 
				removeDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
				startDownload();
				break;
			case 8: 
				removeDialog(DIALOG_CHECKING_DOWNLOAD);
				if (!isFinishing()) {
				Toast.makeText(getBaseContext(),getResources().getString(R.string.pref_msg_err_rename_dic),
						Toast.LENGTH_SHORT).show();
				}
				break;
				
			case 9: 
				removeDialog(DIALOG_CHECKING_DOWNLOAD);
				if (!isFinishing()) {
					Toast.makeText(Preferences.this, R.string.msg_dic_updated_finished, Toast.LENGTH_SHORT).show();
				}
				break;
			case 10:
				removeDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
				if (!isFinishing()) {
					Toast.makeText(Preferences.this, R.string.msg_errthomson3g, Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	
	 private boolean renameFile(String file, String toFile , boolean saveOld) {

	        File toBeRenamed = new File(file);
	        File newFile = new File(toFile);

	        if (!toBeRenamed.exists() || toBeRenamed.isDirectory())
	            return false;
	        

	        if (newFile.exists() && !newFile.isDirectory() && saveOld) {
	        	if ( !renameFile(toFile,toFile+"_backup" , false) )
	        		Toast.makeText(getBaseContext(),getResources().getString(R.string.pref_msg_err_backup_dic),
						Toast.LENGTH_SHORT).show();
	        	else
	        		toFile +="_backup";
	        }
	        newFile = new File(toFile);

	        //Rename
	        if (!toBeRenamed.renameTo(newFile) )
	           return false;
	       

	        return true;
	    }
	
	private static final String TAG = "ThomsonPreferences";
	private String[] mFileList;
	
	private File mPath = new File(Environment.getExternalStorageDirectory() + File.separator);
	private String mChosenFile = File.separator;
	Stack<String> directoryTree = new Stack<String>();


	private void loadFolderList() {
		mPath = new File(Environment.getExternalStorageDirectory() + File.separator + mChosenFile);
		if(mPath.exists()){
			FilenameFilter filter = new FilenameFilter(){
				public boolean accept(File dir, String filename){
					File sel = new File(dir, filename);
					return sel.isDirectory();
				}
			};
			mFileList = mPath.list(filter);
			if ( mFileList == null )
				return;
			TreeSet<String> sorter = new TreeSet<String>();
			for ( int i = 0 ; i < mFileList.length ; ++i  )
				sorter.add(mFileList[i]);
			mFileList = sorter.toArray(mFileList);
		}
		else{ 
			if ( !directoryTree.empty() )
			{
				mChosenFile = directoryTree.pop();
				loadFolderList();
			}
			else
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
		switch(id) {
			case DIALOG_LOAD_FOLDER:
			{
				loadFolderList();
				builder.setTitle(getString(R.string.folder_chooser_title));
				if(mFileList == null || mFileList.length == 0) {
					Log.e(TAG, "Showing file picker before loading the file list");
					mFileList = new String[1];
					mFileList[0] = getString(R.string.folder_chooser_no_dir);
					builder.setItems(mFileList, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog,int which) {}}
					);
				}
				else
					builder.setItems(mFileList, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which){
							directoryTree.push(mChosenFile);
							mChosenFile += File.separator + mFileList[which];
							removeDialog(DIALOG_LOAD_FOLDER);
							showDialog(DIALOG_LOAD_FOLDER);
						}
					});
				if ( !mChosenFile.equals(File.separator))
					builder.setNegativeButton(R.string.bt_choose_back,new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if ( !directoryTree.empty())
								mChosenFile = directoryTree.pop();
							else
								mChosenFile = File.separator;
							removeDialog(DIALOG_LOAD_FOLDER);
							showDialog(DIALOG_LOAD_FOLDER);
						}
					});
				builder.setNeutralButton(R.string.bt_choose,new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
						SharedPreferences.Editor editor = customSharedPreference
						.edit();
	
						editor.putString(folderSelectPref,mPath.toString());
						editor.commit();
						String path = mPath.toString();
						mPath = new File(path +  File.separator + "RouterKeygen.dic");
						File second = new File(path +  File.separator + "RKDictionary.dic");
						if ( !mPath.exists() && !second.exists())
						{
							Toast.makeText(getBaseContext(),getResources().getString(R.string.pref_msg_notfound) + " " + path,
									Toast.LENGTH_SHORT).show();							
						}
						else
						{
							if ( mPath.exists() )
								Toast.makeText(getBaseContext(),mPath.toString() +  " " + getResources().getString(R.string.pref_msg_found),
										Toast.LENGTH_SHORT).show();
							else
								Toast.makeText(getBaseContext(),second.toString() +  " " + getResources().getString(R.string.pref_msg_found),
										Toast.LENGTH_SHORT).show();
						}
					}
				});
	
				break;
			}
			case DIALOG_ABOUT:
			{
				LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
			    View layout = inflater.inflate(R.layout.about_dialog,
			                                   (ViewGroup) findViewById(R.id.tabhost));
			    TabHost tabs = (TabHost) layout.findViewById(R.id.tabhost);
			    tabs.setup();
			    TabSpec tspec1 = tabs.newTabSpec("about");
			    tspec1.setIndicator(getString(R.string.pref_about));
			    
			    tspec1.setContent(R.id.text_about_scroll);	
			    TextView text = ((TextView)layout.findViewById(R.id.text_about));
			    text.setMovementMethod(LinkMovementMethod.getInstance());
			    text.append(VERSION + "\n" + LAUNCH_DATE);
			    tabs.addTab(tspec1);
			    TabSpec tspec2 = tabs.newTabSpec("credits");
			    tspec2.setIndicator(getString(R.string.dialog_about_credits));
			    tspec2.setContent(R.id.about_credits_scroll);		   
			    ((TextView)layout.findViewById(R.id.about_credits))
			    .setMovementMethod(LinkMovementMethod.getInstance());
			    tabs.addTab(tspec2);
			    TabSpec tspec3 = tabs.newTabSpec("license");
			    tspec3.setIndicator(getString(R.string.dialog_about_license));
			    tspec3.setContent(R.id.about_license_scroll);		   
			    ((TextView)layout.findViewById(R.id.about_license))
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
			case DIALOG_ASK_DOWNLOAD:
			{
				DialogInterface.OnClickListener diOnClickListener = new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Check if we have the latest dictionary version.
							try 
							{
								final File myDicFile = getDictionaryFile();
								if( myDicFile == null )
								{
									messHand.sendEmptyMessage(7);
								}
								else
								{
								    removeDialog(DIALOG_ASK_DOWNLOAD);
									showDialog(DIALOG_CHECK_DOWNLOAD_SERVER);
									  new Thread(new Runnable() {
										    public void run() {
		
												// Comparing this version with the online version
												try {
													InputStream is = new FileInputStream(myDicFile);
													URLConnection con = new URL(PUB_DIC_CFV).openConnection();
													DataInputStream dis = new DataInputStream(con.getInputStream());
													if(con.getContentLength() != 18)
														throw new Exception();
													
													dis.read(Preferences.cfvTable);
													
													// Check our version
													is.read(dicVersion);
													
													int thisVersion, onlineVersion;
													thisVersion = dicVersion[0] << 8 | dicVersion[1];
													onlineVersion = cfvTable[0] << 8 | cfvTable[1];
													
													if(thisVersion == onlineVersion)
													{
														// It is the latest version, but is it not corrupt?
														if(checkDicMD5(myDicFile.getPath()))
														{
															// All is well
															messHand.sendEmptyMessage(6);
															return;
														}
													}
													if(onlineVersion > thisVersion && onlineVersion > MAX_DIC_VERSION)
													{
														// Online version is too advanced
														messHand.sendEmptyMessage(5);
														return;
													}
													messHand.sendEmptyMessage(7);
													return;
													
												} catch ( FileNotFoundException e ){
													messHand.sendEmptyMessage(7);
													return;
												} catch ( UnknownHostException e ){
													messHand.sendEmptyMessage(10);
													return;
												}
												catch (Exception e)
												{
													messHand.sendEmptyMessage(-1);
													return;
												}
											}
										  }).start();
								}
							}
							catch (Exception e) {
									e.printStackTrace();					
							}			
			           }
				};
				// Don't complain about dictionary size if user is on a wifi connection
				if((((WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE))).getConnectionInfo().getSSID() != null)
				{
					diOnClickListener.onClick(null, -1);
					break;
				}
				
				builder.setTitle(R.string.pref_download);
				builder.setMessage(R.string.msg_dicislarge);
				builder.setCancelable(false);
				builder.setPositiveButton(R.string.bt_yes, diOnClickListener);
				builder.setNegativeButton(R.string.bt_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						removeDialog(DIALOG_ASK_DOWNLOAD);
					}
				});
		       break;
			}
			case DIALOG_UPDATE_NEEDED:
			{
				builder.setTitle(R.string.update_title)
				.setMessage(getString(R.string.update_message,version))
				.setNegativeButton(R.string.bt_close,new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						removeDialog(DIALOG_UPDATE_NEEDED);
					}
				})
				.setPositiveButton(R.string.bt_website, new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						String url = "http://code.google.com/p/android-thomson-key-solver/downloads/list";
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(url));
						startActivity(i);
					}
				});
				break;
			}
			case DIALOG_CHECK_DOWNLOAD_SERVER:
			{
				pbarDialog = new ProgressDialog(Preferences.this);
				pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pbarDialog.setMessage(getString(R.string.msg_wait));
				return pbarDialog;
			}
			case DIALOG_ERROR_TOO_ADVANCED:
			{
				builder.setTitle(R.string.msg_error)
				.setMessage(R.string.msg_err_online_too_adv);
				break;
			}
			case DIALOG_DOWNLOAD:
			{
				pbarDialog = new ProgressDialog(Preferences.this);
				pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pbarDialog.setMessage(getString(R.string.msg_dl_estimating));
				pbarDialog.setMax(100);
				pbarDialog.setTitle(R.string.msg_dl_dlingdic);
				pbarDialog.setCancelable(true);
				pbarDialog.setOnDismissListener(new OnDismissListener() {
					public void onDismiss(DialogInterface dialog) {
						if ( downloader != null )
							downloader.stopRequested = true;
					}
				});
				pbarDialog.setButton(getString(R.string.bt_pause), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if ( downloader != null )
							downloader.stopRequested = true;
						removeDialog(DIALOG_DOWNLOAD);
					}
				});
				pbarDialog.setButton2(getString(R.string.bt_manual_cancel), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if ( downloader != null )
						{
							downloader.deleteTemp = true;
							downloader.stopRequested = true;
						}
						removeDialog(DIALOG_DOWNLOAD);
					}
				});
				return pbarDialog;
			}
			case DIALOG_ERROR:
			{
				builder.setTitle(R.string.msg_error)
				.setMessage(R.string.msg_err_unkown);
				break;
			}
			case DIALOG_ERROR_NOMEMORYONSD:
			{
				builder.setTitle(R.string.msg_error)
				.setMessage(R.string.msg_nomemoryonsdcard);
				break;
			}
			case DIALOG_ERROR_NOSD:
			{
				builder.setTitle(R.string.msg_error)
				.setMessage(R.string.msg_nosdcard);
				break;
			}
			case DIALOG_CHECKING_DOWNLOAD:
			{
				pbarDialog = new ProgressDialog(Preferences.this);
				pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pbarDialog.setMessage(getString(R.string.msg_wait));
				return pbarDialog;
			}
		}
		return builder.create();
	}
	
	private File getDictionaryFile() throws FileNotFoundException {
		String folderSelect =  PreferenceManager.getDefaultSharedPreferences(getBaseContext())
		.getString(folderSelectPref,
				Environment.getExternalStorageDirectory().getAbsolutePath())
				;
		String firstName = folderSelect + File.separator + "RouterKeygen.dic";
		String secondName = folderSelect + File.separator + "RKDictionary.dic";
		try{
			File dic = new File(firstName);
			if ( dic.exists() )
				return dic;
			dic = new File(secondName);
			if ( dic.exists() )
				return dic;
		} catch(SecurityException e  ){
			e.printStackTrace();
			throw new FileNotFoundException("Permissions Error");
		}
		return null;
	}
};


