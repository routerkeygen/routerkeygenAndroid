package org.exobel.routerkeygen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

public class CancelOperationActivity extends Activity {

	public static final String SERVICE_TO_TERMINATE = "terminateService";
	public static final String MESSAGE = "message";

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cancel_operation);
		if (getIntent().getStringExtra(SERVICE_TO_TERMINATE) == null)
			finish();
		else {
			showDialog(0);
		}
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = getIntent().getStringExtra(MESSAGE);
		if (message == null)
			message = getString(android.R.string.cancel) + "?";
		builder.setTitle(R.string.app_name)
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						})
				.setPositiveButton(android.R.string.yes, new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
						try {
							stopService(new Intent(getApplicationContext(),
									Class.forName(getIntent().getStringExtra(
											SERVICE_TO_TERMINATE))));
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}

						finish();
					}
				}).setMessage(message);
		return builder.create();
	}

}
