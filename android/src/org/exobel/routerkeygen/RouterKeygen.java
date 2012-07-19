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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exobel.routerkeygen.WiFiScanReceiver.OnScanListener;
import org.exobel.routerkeygen.algorithms.Keygen;
import org.exobel.routerkeygen.algorithms.NativeThomson;
import org.exobel.routerkeygen.algorithms.ThomsonKeygen;
import org.exobel.routerkeygen.algorithms.UnsupportedKeygen;
import org.exobel.routerkeygen.ui.WifiListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
@SuppressWarnings("deprecation")
public class RouterKeygen extends Activity implements OnScanListener {

	private WifiManager wifi;
	boolean wifi_state;
	private ListView scanResuls;
	private List<String> list_key = null;
	private BroadcastReceiver scanFinished;
	private BroadcastReceiver stateChanged;
	private List<Keygen> vulnerable;
	private WirelessMatcher networkMatcher;
	private Keygen router;
	private KeygenThread calculator ;
	static final String TAG = "RouterKeygen";
	static final String welcomeScreenShownPref = "welcomeScreenShown";

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setWifi((WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE));
		wifi_state = getWifi().getWifiState() == WifiManager.WIFI_STATE_ENABLED ||  
		getWifi().getWifiState() == WifiManager.WIFI_STATE_ENABLING;
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean welcomeScreenShown = mPrefs.getBoolean( welcomeScreenShownPref, false);

		if (!welcomeScreenShown) {

			String whatsNewTitle = getString(R.string.msg_welcome_title);
			String whatsNewText = getString(R.string.msg_welcome_text);
			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(whatsNewTitle).setMessage(whatsNewText).setPositiveButton(
					R.string.bt_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putBoolean(welcomeScreenShownPref, true);
			editor.commit();
		}

		networkMatcher = new WirelessMatcher(getResources().openRawResource(R.raw.alice));
		scanResuls = (ListView) findViewById(R.id.ListWifi);
		scanResuls.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				router = vulnerable.get(position);
				calculator = (KeygenThread) new KeygenThread(router).execute();
			}
		});
		stateChanged = new WifiStateReceiver(getWifi());
		scanFinished = new WiFiScanReceiver(this, networkMatcher , wifi);
		if ( savedInstanceState == null  )
			return;
		final List<Keygen> list_networks =(List<Keygen>) savedInstanceState.getSerializable("networks");
		if ( list_networks != null )
		{
			vulnerable = list_networks;
			scanResuls.setAdapter(new WifiListAdapter(vulnerable, this));
		}
		Keygen r = (Keygen) savedInstanceState.getSerializable("router");
		if ( r != null )
		{
			router = r;
		}
		else
			router = new UnsupportedKeygen("","",0,"");
		ArrayList<String> list_k =  (ArrayList<String>) savedInstanceState.getSerializable("keys");
		if ( list_k != null )
		{
			list_key = list_k;
		}
	}
	
	protected void onSaveInstanceState (Bundle outState){	
		try {
			//TODO: outState.putSerializable("router", router);
			outState.putSerializable("keys", (Serializable) list_key );
			//TODO: outState.putSerializable("networks", getVulnerable() );
		}
		catch(Exception e){}
	}


	public void onStart() {
		try{ 
			super.onStart();
			getPrefs();
			if ( wifiOn )
			{
				if ( !getWifi().setWifiEnabled(true))
					Toast.makeText( RouterKeygen.this , getString(R.string.msg_wifibroken),
							Toast.LENGTH_SHORT).show();
				else
					wifi_state = true;
			}
			scan();	
		}
		catch (Exception e) {}
	}

	public void onStop() {
		try{ 
			super.onStop();
			unregisterReceiver(scanFinished);
			unregisterReceiver(stateChanged);
			removeDialog(DIALOG_KEY_LIST);
			removeDialog(DIALOG_MANUAL_CALC); 
		}
		catch (Exception e) {}
	}
	ProgressDialog progressDialog;
	private static final int DIALOG_THOMSON3G = 0; 
	private static final int DIALOG_KEY_LIST = 1;
	private static final int DIALOG_MANUAL_CALC = 2;
	private static final int DIALOG_NATIVE_CALC = 3;
	private static final int DIALOG_AUTO_CONNECT = 4;
	protected Dialog onCreateDialog(int id ) {
		switch (id) {
			case DIALOG_THOMSON3G: {
				progressDialog = new ProgressDialog(RouterKeygen.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setTitle(getString(R.string.dialog_thomson3g));
				progressDialog.setMessage(getString(R.string.dialog_thomson3g_msg));
				progressDialog.setCancelable(false);
				progressDialog.setProgress(0);
				progressDialog.setButton(getString(R.string.bt_manual_cancel),
						new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						if ( RouterKeygen.this.calculator != null )
							RouterKeygen.this.calculator.cancel();
						removeDialog(DIALOG_THOMSON3G);
					}
				});
				progressDialog.setIndeterminate(false);
				return progressDialog;
			}
			case DIALOG_KEY_LIST: {
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle(router.getSsidName());
			    LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
			    View layout = inflater.inflate(R.layout.results,
			                                   (ViewGroup) findViewById(R.id.layout_root));
			    ListView list = (ListView) layout.findViewById(R.id.list_keys);
				list.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String key = ((TextView)view).getText().toString();
						Toast.makeText(getApplicationContext(), key + " " 							
								+ getString(R.string.msg_copied),
								Toast.LENGTH_SHORT).show();
						ClipboardManager clipboard = 
							(ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
	
						clipboard.setText(key);
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
					}
				});
				
				list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_key)); 
				/*
				 * TODO: Auto connect
				 * Still not working as wished though it works +-.
				 
				builder.setPositiveButton(RouterKeygen.this.getResources().getString(R.string.bt_connect),
						new OnClickListener() {	
							public void onClick(DialogInterface dialog, int which) {
								wifi.disconnect();
								AutoConnectManager auto = new AutoConnectManager(wifi, list_key , 
														router , RouterKeygen.this , handler);
								auto.activate();
								registerReceiver( auto, new IntentFilter(
										WifiManager.NETWORK_STATE_CHANGED_ACTION));showDialog(DIALOG_AUTO_CONNECT);
							}
				});*/
				builder.setNeutralButton(RouterKeygen.this.getResources().getString(R.string.bt_share),
							new OnClickListener() {	
								public void onClick(DialogInterface dialog, int which) {
									try
									{
										Intent i = new Intent(Intent.ACTION_SEND);
										i.setType("text/plain");
										i.putExtra(Intent.EXTRA_SUBJECT, router.getSsidName() + getString(R.string.share_msg_begin));
										Iterator<String> it = list_key.iterator();
										String message = router.getSsidName() + getString(R.string.share_msg_begin) + ":\n";
										while ( it.hasNext() )
											message += it.next() + "\n";
										
										i.putExtra(Intent.EXTRA_TEXT, message);
										message = getString(R.string.share_title);
										startActivity(Intent.createChooser(i, message));
									}
									catch(Exception e)
									{
										Toast.makeText( RouterKeygen.this , getString(R.string.msg_err_sendto) , 
												Toast.LENGTH_SHORT).show();
										return;
									}
								}
							});
				builder.setNegativeButton(getString(R.string.bt_save_sd), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						removeDialog(DIALOG_MANUAL_CALC);
						if ( !Environment.getExternalStorageState().equals("mounted")  && 
							     !Environment.getExternalStorageState().equals("mounted_ro")	)
						{
							Toast.makeText( RouterKeygen.this , getString(R.string.msg_nosdcard),
								Toast.LENGTH_SHORT).show();
							return ;
						}
						try {
							BufferedWriter out = new BufferedWriter(
									new FileWriter(folderSelect + File.separator + router.getSsidName() + ".txt"));
							out.write(router.getSsidName() + " KEYS");
							out.newLine();
							for ( String s : list_key )
							{
								out.write(s);
								out.newLine();
							}
							out.close();
						}
						catch (IOException e)
						{
							Toast.makeText( RouterKeygen.this , getString(R.string.msg_err_saving_key_file),
									Toast.LENGTH_SHORT).show();
							return ;
						}
						Toast.makeText( RouterKeygen.this , router.getSsidName() + ".txt " + getString(R.string.msg_saved_key_file),
								Toast.LENGTH_SHORT).show();
					}
				});
				
				builder.setView(layout);
				return builder.create();
			}
			case DIALOG_MANUAL_CALC: {
				AlertDialog.Builder builder = new Builder(this); 
				final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.manual_input,
                        (ViewGroup) findViewById(R.id.manual_root));
				builder.setTitle(getString(R.string.menu_manual));
				/*Need to do this to renew the dialog to show the MAC input*/
				builder.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						removeDialog(DIALOG_MANUAL_CALC);
					}
				});
				String[] routers = getResources().getStringArray(R.array.supported_routers);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
										android.R.layout.simple_dropdown_item_1line, routers);
				final AutoCompleteTextView edit = (AutoCompleteTextView) layout.findViewById(R.id.manual_autotext);
				edit.setAdapter(adapter);
				edit.setThreshold(1);
				InputFilter filterMAC = new InputFilter() { 
			        public CharSequence filter(CharSequence source, int start, int end, 
			        		Spanned dest, int dstart, int dend) { 
			        		                for (int i = start; i < end; i++) { 
			        		                        if (!Character.isLetterOrDigit(source.charAt(i)) &&
			        		                        		source.charAt(i) != '-' && source.charAt(i) != '_' && source.charAt(i) != ' ') { 
			        		                                return ""; 
			        		                        } 
			        		                } 
			        		                return null; 
			        		        }
	     		};
			    edit.setFilters(new InputFilter[]{ filterMAC});
			    if ( manualMac )
			    {
			    	layout.findViewById(R.id.manual_mac_root).setVisibility(View.VISIBLE);
			    	edit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			    	final EditText macs[] = new EditText[6];
			    	macs[0] = (EditText) layout.findViewById(R.id.input_mac_pair1);
			    	macs[1] = (EditText) layout.findViewById(R.id.input_mac_pair2);
			    	macs[2] = (EditText) layout.findViewById(R.id.input_mac_pair3);
			    	macs[3] = (EditText) layout.findViewById(R.id.input_mac_pair4);
			    	macs[4] = (EditText) layout.findViewById(R.id.input_mac_pair5);
			    	macs[5] = (EditText) layout.findViewById(R.id.input_mac_pair6);
		     		final InputFilter maxSize = new InputFilter.LengthFilter(2);
	        		InputFilter filterMac = new InputFilter() { 
				        public CharSequence filter(CharSequence source, int start, int end, 
				        		Spanned dest, int dstart, int dend) { 
				        		                try{/*TODO:Lazy mode programming, improve in the future*/
				        		                	Integer.parseInt((String) source , 16);
				        		                }
				        		                catch( Exception e){
				        		                	return "";
				        		                }
				        		                return null; 
				        		        }
				        		};
				    for(final EditText mac : macs)
				    {
				    	mac.setFilters(new InputFilter[]{filterMac , maxSize});
					    mac.addTextChangedListener(new TextWatcher() {
							public void onTextChanged(CharSequence s, int start, int before, int count) {}
							
							public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
							
							public void afterTextChanged(Editable e) {
						    	if(e.length() != 2)
						    		return;
						    	
							    for(int i = 0; i < 6; ++i)
							    {
							    	if(macs[i].getText().length() != 0)
							    		continue;
							    	
						    		macs[i].requestFocus();
						    		return;
							    }
							}
						});
				    }
			    }
				builder.setNeutralButton(getString(R.string.bt_manual_calc), new OnClickListener() {			
					public void onClick(DialogInterface dialog, int which) {
						String ssid = edit.getText().toString().trim();
						String mac = "";
						if ( manualMac )
						{
						    EditText mac1 = (EditText) layout.findViewById(R.id.input_mac_pair1);
						    EditText mac2 = (EditText) layout.findViewById(R.id.input_mac_pair2);
						    EditText mac3 = (EditText) layout.findViewById(R.id.input_mac_pair3);
						    EditText mac4 = (EditText) layout.findViewById(R.id.input_mac_pair4);
						    EditText mac5 = (EditText) layout.findViewById(R.id.input_mac_pair5);
						    EditText mac6 = (EditText) layout.findViewById(R.id.input_mac_pair6);
						    mac= mac1.getText().toString()+':'+mac2.getText().toString()+':'+
						    	 mac3.getText().toString()+':'+mac4.getText().toString()+':'+
						    	 mac5.getText().toString()+':'+mac6.getText().toString();
						    if ( mac.length() < 17 )
						    	mac = "";
					    }
						if ( ssid.equals("") )
							return;
						router = networkMatcher.getKeygen(ssid, mac , 0 ,"");
						calculator = (KeygenThread) new KeygenThread(router).execute();
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);						
					}
				});
				builder.setNegativeButton(getString(R.string.bt_manual_cancel), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						removeDialog(DIALOG_MANUAL_CALC);
					}
				});
				
				builder.setView(layout);
				return builder.create();
			}
			case DIALOG_NATIVE_CALC: {
				progressDialog = new ProgressDialog(RouterKeygen.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setTitle(RouterKeygen.this.getResources().getString(R.string.dialog_nativecalc));
				progressDialog.setMessage(RouterKeygen.this.getResources().getString(R.string.dialog_nativecalc_msg));
				progressDialog.setCancelable(false);
				progressDialog.setProgress(0);
				progressDialog.setButton(RouterKeygen.this.getResources().getString(R.string.bt_manual_cancel),
						new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						if ( RouterKeygen.this.calculator != null )
							RouterKeygen.this.calculator.cancel();
						removeDialog(DIALOG_THOMSON3G);
					}
				});
				progressDialog.setIndeterminate(false);
				return progressDialog;
			}
			case DIALOG_AUTO_CONNECT:
			{
				progressDialog = new ProgressDialog(RouterKeygen.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setMessage("Connecting");
				progressDialog.setMax(list_key.size() + 1);
				progressDialog.setTitle(R.string.msg_dl_dlingdic);
				progressDialog.setCancelable(false);
				progressDialog.setButton(getString(R.string.bt_close), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						removeDialog(DIALOG_AUTO_CONNECT);
					}
				});
				return progressDialog;
			}
		}
		return null;
	}


	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.networks_list, menu);
		getMenuInflater().inflate(R.menu.preferences, menu);
		return true;
	}
	public void scan(){
		registerReceiver(scanFinished, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		if ( !wifi_state && !wifiOn )
		{
			Toast.makeText( RouterKeygen.this , 
					RouterKeygen.this.getResources().getString(R.string.msg_nowifi),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if ( getWifi().getWifiState() == WifiManager.WIFI_STATE_ENABLING )
		{
			registerReceiver(stateChanged, new IntentFilter(
					WifiManager.WIFI_STATE_CHANGED_ACTION));
			Toast.makeText( RouterKeygen.this ,
					RouterKeygen.this.getResources().getString(R.string.msg_wifienabling),
					Toast.LENGTH_SHORT).show();
		}
		else{
			if ( getWifi().startScan() )	
				Toast.makeText(this, R.string.msg_scanstarted, Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this, R.string.msg_scanfailed, Toast.LENGTH_SHORT).show();
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.wifi_scan:
			scan();
			return true;
		case R.id.manual_input:
			showDialog(DIALOG_MANUAL_CALC);
			return true;
		case R.id.pref:
			startActivity( new Intent(this , Preferences.class ));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	private boolean wifiOn;
	private boolean thomson3g;
	private boolean nativeCalc;
	private boolean manualMac;
	private String folderSelect;
	private final String folderSelectPref = "folderSelect";
	final String wifiOnPref = "wifion";
	final String thomson3gPref = "thomson3g";
	final String nativeCalcPref = "nativethomson";
	final String manualMacPref = "manual_mac";

	private void getPrefs() {
		SharedPreferences prefs = PreferenceManager
		.getDefaultSharedPreferences(getBaseContext());
		wifiOn = prefs.getBoolean(wifiOnPref , true);
		thomson3g = prefs.getBoolean(thomson3gPref, false);
		nativeCalc = prefs.getBoolean(nativeCalcPref, true);
		manualMac = prefs.getBoolean(manualMacPref, false);
		folderSelect = prefs.getString(folderSelectPref, 
				Environment.getExternalStorageDirectory().getAbsolutePath());
	}

    public ListView getScanResuls() {
        return scanResuls;
    }

    public void setScanResuls(ListView scanResuls) {
        this.scanResuls = scanResuls;
    }
    public WifiManager getWifi() {
        return wifi;
    }

    public void setWifi(WifiManager wifi) {
        this.wifi = wifi;
    }
	
	private class KeygenThread extends AsyncTask<Keygen, Integer, List<String>>{
		private Keygen keygen;
		private KeygenThread(Keygen keygen){
			this.keygen = keygen;
		}
		
		@Override
		protected void onPostExecute(List<String> result) {
			if ( thomson3g)
				removeDialog(DIALOG_THOMSON3G);
			if ( nativeCalc )
				removeDialog(DIALOG_NATIVE_CALC);
			if ( result == null )
				return;
			list_key = result;
			if (!isFinishing())
				showDialog(DIALOG_KEY_LIST);
		}

		@Override
		protected void onPreExecute() {
			if ( !keygen.isSupported() )
			{
				Toast.makeText( getApplicationContext() , R.string.msg_unspported,
						Toast.LENGTH_SHORT).show();
				cancel(true);
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			for ( Integer s : values )
			{
				if ( s == 0 )
					showDialog(DIALOG_NATIVE_CALC);
				else
					Toast.makeText(getApplicationContext(), getString(s), Toast.LENGTH_SHORT).show();

			}
		}
		
		public void cancel(){
			keygen.setStopRequested(true);
			cancel(true);
		}
		
		private final static int SHOW_NATIVE_DIALOG = 0;

		@Override
		protected List<String> doInBackground(Keygen... params) {
			if ( !keygen.isSupported() )
				return null;
			if ( keygen instanceof ThomsonKeygen ) {
				((ThomsonKeygen)keygen).setFolder(folderSelect);
				((ThomsonKeygen)keygen).setInternetAlgorithm(thomson3g);
			}
			long begin = System.currentTimeMillis();
			List<String> result = keygen.getKeys();
			long end = System.currentTimeMillis() -begin;
			Log.d(TAG, "Time to solve:" + end);

			final int errorCode = keygen.getErrorCode();
			if ( errorCode != 0 )
				publishProgress(errorCode);
			if ( nativeCalc && ( keygen instanceof ThomsonKeygen ) )
			{
				if ( ((ThomsonKeygen)keygen).isErrorDict() )
				{
					publishProgress(R.string.msg_startingnativecalc);
					try{
						keygen = new NativeThomson(keygen);
						if (isCancelled())
							return null;
						publishProgress(SHOW_NATIVE_DIALOG);
						begin = System.currentTimeMillis();
						result = keygen.getKeys();
						end = System.currentTimeMillis() -begin;
						Log.d(TAG, "Time to solve:" + end);

						if ( keygen.getErrorCode() != 0 )
							publishProgress(keygen.getErrorCode());
					}catch(LinkageError e){
						publishProgress(R.string.err_misbuilt_apk);
						return null;
					}
				}
			}
			return result;
		}
		
	}

	public void onScanFinished(List<Keygen> networks) {
        this.vulnerable = networks;
		scanResuls.setAdapter(new WifiListAdapter(vulnerable, this));
	}

}
