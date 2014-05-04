package org.exobel.routerkeygen.ui;

import java.io.IOException;
import java.util.Locale;
import java.util.zip.ZipInputStream;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.algorithms.Keygen;
import org.exobel.routerkeygen.algorithms.WiFiNetwork;
import org.exobel.routerkeygen.ui.NetworksListFragment.OnItemSelectionListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class ManualInputFragment extends SherlockFragment {
	public final static String MAC_ADDRESS_ARG = "mac_address";
	private View loading;
	private View mainView;

	public static ManualInputFragment newInstance() {
		ManualInputFragment frag = new ManualInputFragment();
		frag.setArguments(Bundle.EMPTY);
		return frag;
	}

	public static ManualInputFragment newInstance(String mac) {
		Bundle args = new Bundle();
		if (mac != null)
			args.putString(MAC_ADDRESS_ARG, mac.replace(":", ""));
		ManualInputFragment frag = new ManualInputFragment();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final String macAddress;
		if (getArguments().containsKey(MAC_ADDRESS_ARG))
			macAddress = getArguments().getString(MAC_ADDRESS_ARG);
		else
			macAddress = null;
		final View root = inflater
				.inflate(R.layout.fragment_manual_input, null);
		mainView = root.findViewById(R.id.manual_root);
		loading = root.findViewById(R.id.loading_spinner);
		final String[] routers = getResources().getStringArray(
				R.array.supported_routers);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_dropdown_item_1line, routers);
		final AutoCompleteTextView edit = (AutoCompleteTextView) root
				.findViewById(R.id.manual_autotext);
		edit.setAdapter(adapter);
		edit.setThreshold(1);
		final InputFilter filterSSID = new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				for (int i = start; i < end; i++) {
					if (!Character.isLetterOrDigit(source.charAt(i))
							&& source.charAt(i) != '-'
							&& source.charAt(i) != '_'
							&& source.charAt(i) != ' ') {
						return "";
					}
				}
				return null;
			}
		};
		edit.setFilters(new InputFilter[] { filterSSID });
		final EditText macs[] = new EditText[6];
		root.findViewById(R.id.manual_mac_root).setVisibility(View.VISIBLE);
		edit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		macs[0] = (EditText) root.findViewById(R.id.input_mac_pair1);
		macs[1] = (EditText) root.findViewById(R.id.input_mac_pair2);
		macs[2] = (EditText) root.findViewById(R.id.input_mac_pair3);
		macs[3] = (EditText) root.findViewById(R.id.input_mac_pair4);
		macs[4] = (EditText) root.findViewById(R.id.input_mac_pair5);
		macs[5] = (EditText) root.findViewById(R.id.input_mac_pair6);
		if (macAddress != null) {
			macs[0].setText(macAddress.substring(0, 2));
			macs[1].setText(macAddress.substring(2, 4));
			macs[2].setText(macAddress.substring(4, 6));
			macs[3].setText(macAddress.substring(6, 8));
			macs[4].setText(macAddress.substring(8, 10));
			macs[5].setText(macAddress.substring(10, 12));
		}
		final InputFilter filterMac = new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				if (dstart >= 2)
					return "";
				if (source.length() > 2)
					return "";// max 2 chars
				for (int i = start; i < end; i++) {
					if (Character.digit(source.charAt(i), 16) == -1) {
						return "";
					}
				}
				if (source.length() + dstart > 2)
					return source.subSequence(0, 2 - dstart);
				return null;
			}
		};
		for (final EditText mac : macs) {
			mac.setFilters(new InputFilter[] { filterMac });
			mac.addTextChangedListener(new TextWatcher() {
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable e) {
					if (e.length() != 2)
						return;

					for (int i = 0; i < 6; ++i) {
						if (macs[i].getText().length() >= 2)
							continue;

						macs[i].requestFocus();
						return;
					}
				}
			});
		}
		Button calc = (Button) root.findViewById(R.id.bt_calc);
		calc.setOnClickListener(new View.OnClickListener() {

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			public void onClick(View v) {
				String ssid = edit.getText().toString().trim();
				StringBuilder mac = new StringBuilder();
				boolean warnUnused = false;
				for (EditText m : macs) {
					final String mText = m.getText().toString();
					if (mText.length() > 0)
						warnUnused = true;
					mac.append(mText);
					if (!m.equals(macs[5]))
						mac.append(":"); // do not add this for the
											// last one
				}
				if (mac.length() < 17) {
					mac.setLength(0);
					if (warnUnused)
						Toast.makeText(getActivity(), R.string.msg_invalid_mac,
								Toast.LENGTH_SHORT).show();
				}

				if (ssid.equals(""))
					return;
				KeygenMatcherTask matcher = new KeygenMatcherTask(ssid, mac
						.toString().toUpperCase(Locale.getDefault()));
				if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
					matcher.execute();
				} else {
					matcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}

			}
		});
		Button cancel = (Button) root.findViewById(R.id.bt_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
		return root;
	}

	private static OnItemSelectionListener sDummyCallbacks = new OnItemSelectionListener() {
		public void onItemSelected(WiFiNetwork id) {
		}

		public void onItemSelected(String mac) {
		}
	};

	private OnItemSelectionListener mCallbacks = sDummyCallbacks;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof OnItemSelectionListener)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (OnItemSelectionListener) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	private class KeygenMatcherTask extends AsyncTask<Void, Void, WiFiNetwork> {
		private final String ssid;
		private final String mac;

		public KeygenMatcherTask(String ssid, String mac) {
			this.ssid = ssid;
			this.mac = mac;
		}

		@Override
		protected void onPreExecute() {
			mainView.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(WiFiNetwork wifiNetwork) {
			loading.setVisibility(View.GONE);
			mainView.setVisibility(View.VISIBLE);
			if (wifiNetwork.getSupportState() == Keygen.UNSUPPORTED) {
				Toast.makeText(getActivity(), R.string.msg_unspported_network,
						Toast.LENGTH_SHORT).show();
				return;
			}
			mCallbacks.onItemSelected(wifiNetwork);
		}

		@Override
		protected WiFiNetwork doInBackground(Void... params) {
			final ZipInputStream magicInfo = new ZipInputStream(getActivity()
					.getResources().openRawResource(R.raw.magic_info));
			final WiFiNetwork wifi = new WiFiNetwork(ssid, mac, 0, "",
					magicInfo);
			try {
				magicInfo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return wifi;
		}
	}
}
