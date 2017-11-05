package org.exobel.routerkeygen;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.exobel.routerkeygen.ui.Preferences;
import org.exobel.routerkeygen.utils.HashUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DictionaryDownloadService extends IntentService {

    public final static String URL_DOWNLOAD = "org.exobel.routerkeygen.DictionaryDownloadService.URL_DOWNLOAD";
    private final static String DEFAULT_DIC_NAME = "RouterKeygen.dic";

    private static final long MIN_TIME_BETWWEN_UPDATES = 1000;

    private static final byte[] DICTIONARY_HASH = {(byte) 0x8c, (byte) 0xcf,
            0x2c, (byte) 0xb2, (byte) 0xe8, (byte) 0xda, (byte) 0x13,
            (byte) 0xc2, (byte) 0xd8, (byte) 0xc7, (byte) 0xbb, (byte) 0x08,
            0x2c, (byte) 0xc2, (byte) 0x1f, (byte) 0xe6};
    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private final int UNIQUE_ID = R.string.app_name
            + DictionaryDownloadService.class.getName().hashCode();
    private NotificationManager mNotificationManager;
    private boolean cancelNotification = true;
    private boolean stopRequested = false;

    public DictionaryDownloadService() {
        super("DictionaryDownloadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void onDestroy() {
        super.onDestroy();
        stopRequested = true;
        if (cancelNotification)
            mNotificationManager.cancel(UNIQUE_ID);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onHandleIntent(Intent intent) {
        File myDicFile;
        HttpURLConnection con = null;
        DataInputStream dis = null;
        FileOutputStream fos = null;
        int myProgress;
        int byteRead;
        byte[] buf;

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            mNotificationManager.notify(
                    UNIQUE_ID,
                    NotificationUtils.getSimple(this,
                            getString(R.string.msg_error),
                            getString(R.string.msg_nosdcard)).build());
            cancelNotification = false;
            return;
        }
        final String dicTemp = Environment.getExternalStorageDirectory()
                .getPath()
                + File.separator
                + "DicTemp"
                + System.currentTimeMillis();
        try {

            final String urlDownload = intent.getStringExtra(URL_DOWNLOAD);

            con = (HttpURLConnection) new URL(urlDownload).openConnection();

            myProgress = 0;

            dis = new DataInputStream(con.getInputStream());
            int fileLen = con.getContentLength();
            if (noSpaceLeft(fileLen)) {
                mNotificationManager.notify(
                        UNIQUE_ID,
                        NotificationUtils.getSimple(this,
                                getString(R.string.msg_error),
                                getString(R.string.msg_nomemoryonsdcard))
                                .build());
                dis.close();
                con.disconnect();
                cancelNotification = false;
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
                editor.apply();
            }

            // Testing if we can write to the file
            if (canNotWrite(dicFile) || canNotWrite(dicTemp)) {
                mNotificationManager.notify(
                        UNIQUE_ID,
                        NotificationUtils.getSimple(this,
                                getString(R.string.msg_error),
                                getString(R.string.msg_no_write_permissions))
                                .build());
                dis.close();
                con.disconnect();
                cancelNotification = false;
                return;
            }
            myDicFile = new File(dicTemp);

            fos = new FileOutputStream(myDicFile, false);

            final Intent i = new Intent(getApplicationContext(),
                    CancelOperationActivity.class)
                    .putExtra(CancelOperationActivity.SERVICE_TO_TERMINATE,
                            DictionaryDownloadService.class.getName())
                    .putExtra(
                            CancelOperationActivity.MESSAGE,
                            getApplicationContext().getString(
                                    R.string.cancel_download))
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mNotificationManager.notify(UNIQUE_ID, NotificationUtils.createProgressBar(this,
                    getString(R.string.msg_dl_dlingdic), "", fileLen,
                    myProgress, false, PendingIntent.getActivity(
                            getApplicationContext(), 0, i,
                            PendingIntent.FLAG_UPDATE_CURRENT)));
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
                    mNotificationManager.notify(UNIQUE_ID, NotificationUtils.createProgressBar(this,
                            getString(R.string.msg_dl_dlingdic), "", fileLen,
                            myProgress, false, PendingIntent.getActivity(
                                    getApplicationContext(), 0, i,
                                    PendingIntent.FLAG_UPDATE_CURRENT)));
                    lastNotificationTime = System.currentTimeMillis();
                }
            }

            mNotificationManager
                    .notify(UNIQUE_ID,
                            NotificationUtils
                                    .createProgressBar(
                                            this,
                                            getString(R.string.msg_dl_dlingdic),
                                            getString(R.string.msg_wait),
                                            0,
                                            0,
                                            true,
                                            NotificationUtils
                                                    .getDefaultPendingIntent(getApplicationContext())));
            if (!HashUtils.checkDicMD5(dicTemp, DICTIONARY_HASH)) {
                new File(dicTemp).delete();
                mNotificationManager.notify(
                        UNIQUE_ID,
                        NotificationUtils.getSimple(this,
                                getString(R.string.msg_error),
                                getString(R.string.msg_err_unkown)).build());
                cancelNotification = false;
                return;
            }
            if (renameFile(dicTemp, dicFile)) {
                new File(dicTemp).delete();
                mNotificationManager.notify(
                        UNIQUE_ID,
                        NotificationUtils.getSimple(this,
                                getString(R.string.msg_error),
                                getString(R.string.pref_msg_err_rename_dic))
                                .build());
                cancelNotification = false;
                return;
            }
            mNotificationManager.notify(
                    UNIQUE_ID,
                    NotificationUtils.getSimple(this,
                            getString(R.string.app_name),
                            getString(R.string.msg_dic_updated_finished))
                            .build());
            cancelNotification = false;
        } catch (FileNotFoundException e) {
            new File(dicTemp).delete();
            mNotificationManager.notify(
                    UNIQUE_ID,
                    NotificationUtils.getSimple(this,
                            getString(R.string.msg_error),
                            getString(R.string.msg_nosdcard)).build());
            cancelNotification = false;
            e.printStackTrace();
        } catch (Exception e) {
            new File(dicTemp).delete();
            mNotificationManager.notify(
                    UNIQUE_ID,
                    NotificationUtils.getSimple(this,
                            getString(R.string.msg_error),
                            getString(R.string.msg_err_unkown)).build());
            cancelNotification = false;
            e.printStackTrace();
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (dis != null)
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (con != null)
                con.disconnect();
        }
    }

    private boolean canNotWrite(String filename) {
        File file;
        while ((file = new File(filename)).exists()) {
            filename += "1";
        }
        try {
            file.createNewFile();
            boolean ret = file.canWrite();
            file.delete();
            return !ret;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean renameFile(String file, String toFile) {

        File toBeRenamed = new File(file);
        File newFile = new File(toFile);

        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()
                || newFile.isDirectory())
            return true;

        if (newFile.exists()) {
            if (renameFile(toFile, toFile + "_backup"))
                Toast.makeText(getBaseContext(),
                        R.string.pref_msg_err_backup_dic, Toast.LENGTH_SHORT)
                        .show();
        }

        // Rename
        return !toBeRenamed.renameTo(newFile);
    }

    private boolean noSpaceLeft(int fileLen) {
        // Checking if external storage has enough memory ...
        android.os.StatFs stat = new android.os.StatFs(Environment
                .getExternalStorageDirectory().getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return stat.getBlockSizeLong() == 0 || stat.getAvailableBlocksLong() < ((long) fileLen / stat.getBlockSizeLong());
        } else {
            return stat.getBlockSize() == 0 || stat.getAvailableBlocks() < (fileLen / stat.getBlockSize());
        }
    }
}
