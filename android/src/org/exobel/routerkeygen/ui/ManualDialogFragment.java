package org.exobel.routerkeygen.ui;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.WirelessMatcher;
import org.exobel.routerkeygen.algorithms.Keygen;
import org.exobel.routerkeygen.algorithms.UnsupportedKeygen;
import org.exobel.routerkeygen.ui.NetworksListFragment.OnItemSelectionListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class ManualDialogFragment extends SherlockDialogFragment {
	private final static String MANUAL_MAC_ARG = "manualMac";

	private final static String WIRELESS_MATCHER_ARG = "wirelessMatcher";

	private boolean manualMac;
	private WirelessMatcher matcher;

	public static ManualDialogFragment newInstance(boolean manualMac,
			WirelessMatcher matcher) {
		Bundle args = new Bundle();
		args.putParcelable(WIRELESS_MATCHER_ARG, matcher);
		args.putBoolean(MANUAL_MAC_ARG, manualMac);
		ManualDialogFragment frag = new ManualDialogFragment();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		manualMac = getArguments().getBoolean(MANUAL_MAC_ARG);
		matcher = getArguments().getParcelable(WIRELESS_MATCHER_ARG);
		AlertDialog.Builder builder = new Builder(getActivity());
		final LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.manual_input, null);
		builder.setTitle(getString(R.string.menu_manual));
		/* Need to do this to renew the dialog to show the MAC input */
		builder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				dismissAllowingStateLoss();
			}
		});
		final String[] routers = getResources().getStringArray(
				R.array.supported_routers);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_dropdown_item_1line, routers);
		final AutoCompleteTextView edit = (AutoCompleteTextView) layout
				.findViewById(R.id.manual_autotext);
		edit.setAdapter(adapter);
		edit.setThreshold(1);
		InputFilter filterMAC = new InputFilter() {
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
		edit.setFilters(new InputFilter[] { filterMAC });
		final EditText macs[] = new EditText[6];
		if (manualMac) {
			layout.findViewById(R.id.manual_mac_root).setVisibility(
					View.VISIBLE);
			edit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			macs[0] = (EditText) layout.findViewById(R.id.input_mac_pair1);
			macs[1] = (EditText) layout.findViewById(R.id.input_mac_pair2);
			macs[2] = (EditText) layout.findViewById(R.id.input_mac_pair3);
			macs[3] = (EditText) layout.findViewById(R.id.input_mac_pair4);
			macs[4] = (EditText) layout.findViewById(R.id.input_mac_pair5);
			macs[5] = (EditText) layout.findViewById(R.id.input_mac_pair6);
			final InputFilter maxSize = new InputFilter.LengthFilter(2);
			InputFilter filterMac = new InputFilter() {
				public CharSequence filter(CharSequence source, int start,
						int end, Spanned dest, int dstart, int dend) {
					try {/* TODO:Lazy mode programming, improve in the future */
						Integer.parseInt((String) source, 16);
					} catch (Exception e) {
						return "";
					}
					return null;
				}
			};
			for (final EditText mac : macs) {
				mac.setFilters(new InputFilter[] { filterMac, maxSize });
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
							if (macs[i].getText().length() != 0)
								continue;

							macs[i].requestFocus();
							return;
						}
					}
				});
			}
		}
		builder.setNeutralButton(getString(R.string.bt_manual_calc),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String ssid = edit.getText().toString().trim();
						StringBuilder mac = new StringBuilder();
						if (manualMac) {
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
								mac.setLength(0);// TODO: warn about mac being
													// ignored
								if (warnUnused)
									Toast.makeText(getActivity(),
											R.string.msg_invalid_mac,
											Toast.LENGTH_SHORT).show();
							}

						}
						if (ssid.equals(""))
							return;
						Keygen keygen = matcher.getKeygen(ssid, mac.toString(),
								0, "");
						if (keygen instanceof UnsupportedKeygen) {
							Toast.makeText(getActivity(),
									R.string.msg_unspported_network,
									Toast.LENGTH_SHORT).show();
							return;
						}
						dismissAllowingStateLoss();
						mCallbacks.onItemSelected(keygen);

					}
				});
		builder.setNegativeButton(getString(R.string.bt_manual_cancel),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dismissAllowingStateLoss();
					}
				});
		setCancelable(false);
		builder.setView(layout);
		return builder.create();
	}

	private static OnItemSelectionListener sDummyCallbacks = new OnItemSelectionListener() {
		public void onItemSelected(Keygen id) {
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

}
