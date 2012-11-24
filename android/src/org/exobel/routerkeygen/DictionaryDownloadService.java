package org.exobel.routerkeygen;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.exobel.routerkeygen.ui.Preferences;
import org.exobel.routerkeygen.utils.HashUtils;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DictionaryDownloadService extends IntentService {

	public final static String URL_DOWNLOAD = "org.exobel.routerkeygen.DictionaryDownloadService.URL_DOWNLOAD";
	private final static String DEFAULT_DIC_NAME = "RouterKeygen.dic";

	private static final long MIN_TIME_BETWWEN_UPDATES = 500;
	
	private static final byte[] DICTIONARY_HASH = { (byte) 0x8c, (byte) 0xcf, 0x2c,
			(byte) 0xb2, (byte) 0xe8, (byte) 0xda, (byte) 0x13, (byte) 0xc2,
 (byte) 0xd8, (byte) 0xc7, (byte) 0xbb, (byte) 0x08,
			0x2c, (byte) 0xc2, (byte) 0x1f, (byte) 0xe6 };

	public DictionaryDownloadService() {
		super("DictionaryDownloadService");
	}

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private final int UNIQUE_ID = R.string.app_name
			+ DictionaryDownloadService.class.getName().hashCode();
	private int fileLen;
	private boolean stopRequested = false;

	public void onDestroy() {
		stopRequested = true;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		File myDicFile;
		HttpURLConnection con;
		DataInputStream dis;
		FileOutputStream fos;
		int myProgress = 0;
		int byteRead;
		byte[] buf;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			mNotificationManager.notify(
					UNIQUE_ID,
					getSimple(getString(R.string.msg_error),
							getString(R.string.msg_nosdcard)).build());
			return;
		}
		final String dicTemp = Environment.getExternalStorageDirectory()
				.getPath() + File.separator + "DicTemp.dic";
		try {

			final String urlDownload = intent.getStringExtra(URL_DOWNLOAD);

			con = (HttpURLConnection) new URL(urlDownload).openConnection();
			myDicFile = new File(dicTemp);

			fos = new FileOutputStream(myDicFile, false);

			myProgress = byteRead = 0;

			dis = new DataInputStream(con.getInputStream());
			fileLen = con.getContentLength();
			// Checking if external storage has enough memory ...
			android.os.StatFs stat = new android.os.StatFs(Environment
					.getExternalStorageDirectory().getPath());
			if ((long) stat.getBlockSize() * (long) stat.getAvailableBlocks() < fileLen) {
				mNotificationManager.notify(
						UNIQUE_ID,
						getSimple(getString(R.string.msg_error),
								getString(R.string.msg_nomemoryonsdcard))
								.build());
				fos.close();
				dis.close();
				con.disconnect();
				return;
			}
			String dicFile = PreferenceManager.getDefaultSharedPreferences(
					getBaseContext()).getString(Preferences.dicLocalPref, null);
			if (dicFile == null) {
				dicFile = Environment.getExternalStorageDirectory().getPath()
						+ File.separator + DEFAULT_DIC_NAME;
				final SharedPreferences.Editor editor = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext()).edit();
				editor.putString(Preferences.dicLocalPref, dicFile);
				editor.commit();
			}

			// Testing if we can write to the file
			if (!canWrite(dicFile)) {
				mNotificationManager.notify(
						UNIQUE_ID,
						getSimple(getString(R.string.msg_error),
								getString(R.string.msg_no_write_permissions))
								.build());
				dis.close();
				fos.close();
				con.disconnect();
				return;
			}

			mNotificationManager.notify(
					UNIQUE_ID,
					createProgressBar(getString(R.string.msg_dl_dlingdic), "",
							myProgress, false));
			long lastNotificationTime = System.currentTimeMillis();
			buf = new byte[1024 * 512];
			while (myProgress < fileLen) {
				if (stopRequested) {
					mNotificationManager.cancel(UNIQUE_ID);
					dis.close();
					fos.close();
					con.disconnect();
					myDicFile.delete();
					return;
				}
				if ((byteRead = dis.read(buf)) != -1) {
					fos.write(buf, 0, byteRead);
					myProgress += byteRead;
				} else {
					dis.close();
					fos.close();
					con.disconnect();
					myProgress = fileLen;
				}
				if ((System.currentTimeMillis() - lastNotificationTime) > MIN_TIME_BETWWEN_UPDATES) {
					mNotificationManager.notify(UNIQUE_ID,
							updateProgressBar(myProgress, false));
					lastNotificationTime = System.currentTimeMillis();
				}
			}

			mNotificationManager.notify(
					UNIQUE_ID,
					createProgressBar(getString(R.string.msg_dl_dlingdic),
							getString(R.string.msg_wait), myProgress, true));
			if (!HashUtils.checkDicMD5(dicTemp, DICTIONARY_HASH)) {
				new File(dicTemp).delete();
				mNotificationManager.notify(
						UNIQUE_ID,
						getSimple(getString(R.string.msg_error),
								getString(R.string.msg_err_unkown)).build());
				return;
			}
			if (!renameFile(dicTemp, dicFile, true)) {

				mNotificationManager.notify(
						UNIQUE_ID,
						getSimple(getString(R.string.msg_error),
								getString(R.string.pref_msg_err_rename_dic))
								.build());
				return;
			}
			mNotificationManager.notify(
					UNIQUE_ID,
					getSimple(getString(R.string.app_name),
							getString(R.string.msg_dic_updated_finished))
							.build());
		} catch (FileNotFoundException e) {
			mNotificationManager.notify(
					UNIQUE_ID,
					getSimple(getString(R.string.msg_error),
							getString(R.string.msg_nosdcard)).build());
			e.printStackTrace();
		} catch (Exception e) {
			mNotificationManager.notify(
					UNIQUE_ID,
					getSimple(getString(R.string.msg_error),
							getString(R.string.msg_err_unkown)).build());
			e.printStackTrace();
		}
	}

	private boolean canWrite(String filename) {
		File file;
		while ((file = new File(filename)).exists()) {
			filename += "1";
		}
		try {
			file.createNewFile();
			boolean ret = file.canWrite();
			file.delete();
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean renameFile(String file, String toFile, boolean saveOld) {

		File toBeRenamed = new File(file);
		File newFile = new File(toFile);

		if (!toBeRenamed.exists() || toBeRenamed.isDirectory())
			return false;

		if (newFile.exists() && !newFile.isDirectory() && saveOld) {
			if (!renameFile(toFile, toFile + "_backup", false))
				Toast.makeText(getBaseContext(),
						R.string.pref_msg_err_backup_dic, Toast.LENGTH_SHORT)
						.show();
			else
				toFile += "_backup";
		}
		newFile = new File(toFile);

		// Rename
		if (!toBeRenamed.renameTo(newFile))
			return false;

		return true;
	}

	private NotificationCompat.Builder getSimple(CharSequence title,
			CharSequence context) {
		return new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.icon).setTicker(title)
				.setContentTitle(title).setContentText(context)
				.setContentIntent(getPendingIntent());
	}

	private Notification update;

	@TargetApi(16)
	private Notification updateProgressBar(int progress, boolean indeterminate) {
		update.contentView.setProgressBar(android.R.id.progress, fileLen,
				progress, indeterminate);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			update.bigContentView.setProgressBar(android.R.id.progress,
					fileLen, progress, indeterminate);
		return update;
	}

	private Notification createProgressBar(CharSequence title,
			CharSequence content, int progress, boolean indeterminate) {
		final NotificationCompat.Builder builder = getSimple(title, content);
		final PendingIntent i = PendingIntent.getActivity(
				getApplicationContext(),
				0,
				new Intent(this, CancelOperationActivity.class).putExtra(
						CancelOperationActivity.SERVICE_TO_TERMINATE,
						DictionaryDownloadService.class.getName()).putExtra(
						CancelOperationActivity.MESSAGE,
						getString(R.string.cancel_download)),
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(i);
		builder.setOngoing(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			builder.setProgress(fileLen, progress, indeterminate);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
					&& !indeterminate) {
				builder.addAction(
						android.R.drawable.ic_menu_close_clear_cancel,
						getString(android.R.string.cancel), i);
			}
			update = builder.build();
		} else {
			RemoteViews contentView = new RemoteViews(getPackageName(),
					R.layout.notification);
			contentView.setTextViewText(android.R.id.text1, content);
			contentView.setProgressBar(android.R.id.progress, fileLen,
					progress, indeterminate);
			update = builder.build();
			update.contentView = contentView;
		}
		return update;
	}

	private PendingIntent getPendingIntent() {
		return PendingIntent.getActivity(getApplicationContext(), 0,
				new Intent(), // add this
				// pass null
				// to intent
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

}
