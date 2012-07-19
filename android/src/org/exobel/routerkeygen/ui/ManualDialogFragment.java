package org.exobel.routerkeygen.ui;

import org.exobel.routerkeygen.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public class ManualDialogFragment extends DialogFragment {
	private final static String MANUAL_MAC_ARG = "manualMac";

	private boolean manualMac;
	public static ManualDialogFragment newInstance(boolean manualMac) {
		Bundle args = new Bundle(); 
		args.putBoolean(MANUAL_MAC_ARG, manualMac);
		ManualDialogFragment frag = new ManualDialogFragment();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		manualMac = getArguments().getBoolean(MANUAL_MAC_ARG);
		AlertDialog.Builder builder = new Builder(getActivity()); 
		final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.manual_input,null);
		builder.setTitle(getString(R.string.menu_manual));
		/*Need to do this to renew the dialog to show the MAC input*/
		builder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				dismissAllowingStateLoss();
			}
		});
		final String[] routers = getResources().getStringArray(R.array.supported_routers);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
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
				    	mac = "";//TODO: warn about mac being ignored
			    }
				if ( ssid.equals("") )
					return;					
			}
		});
		builder.setNegativeButton(getString(R.string.bt_manual_cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});
		setCancelable(false);
		builder.setView(layout);
		return builder.create();
	}  

	public void onDismiss (DialogInterface dialog){
		super.onDismiss(dialog);
		if ( isCancelable() )
		{
			if ( getActivity() instanceof OnDismissListener )
			{
				((OnDismissListener) getActivity()).onDismiss(dialog);
			}
		}
	}

}
