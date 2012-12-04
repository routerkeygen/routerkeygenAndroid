package org.exobel.routerkeygendownloader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
