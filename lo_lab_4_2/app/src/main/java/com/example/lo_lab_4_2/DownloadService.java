package com.example.lo_lab_4_2;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService {

    private static final String ACTION_DOWNLOAD = "com.example.lo_lab_4_2.action.DOWNLOAD";
    private static final String EXTRA_URL = "com.example.lo_lab_4_2.extra.URL";

    private static final String CHANNEL_ID = "DownloadServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public DownloadService() {
        super("DownloadService");
    }

    public static void startActionDownload(Context context, String url) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                handleActionDownload(url);
            }
        }
    }

    private void handleActionDownload(String urlString) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Download Service Channel",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Downloading")
                .setContentText("Download in progress")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("DownloadService", "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage());
                return;
            }

            int fileLength = connection.getContentLength();

            inputStream = connection.getInputStream();
            String fileName = getFileName(urlString);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            file = getUniqueFile(file);

            outputStream = new FileOutputStream(file);

            byte[] data = new byte[4096];
            long total = 0;
            int count;
            while ((count = inputStream.read(data)) != -1) {
                total += count;
                if (fileLength > 0) {
                    updateNotificationProgress(total, fileLength);
                    sendProgressBroadcast(new PostepInfo(total, fileLength, "Downloading"));
                }
                outputStream.write(data, 0, count);
            }
            sendProgressBroadcast(new PostepInfo(total, fileLength, "Completed"));
            updateNotificationProgress(fileLength, fileLength);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ignored) {
            }
        }

        notificationManager.cancel(NOTIFICATION_ID);
    }

    private String getFileName(String urlString) {
        return urlString.substring(urlString.lastIndexOf('/') + 1);
    }

    private File getUniqueFile(File file) {
        File uniqueFile = file;
        String filePath = file.getAbsolutePath();
        String fileName = file.getName();
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex);
            fileName = fileName.substring(0, dotIndex);
        }

        int fileNumber = 1;
        while (uniqueFile.exists()) {
            uniqueFile = new File(file.getParent(), fileName + "_" + fileNumber + fileExtension);
            fileNumber++;
        }
        return uniqueFile;
    }

    private void updateNotificationProgress(long downloaded, long total) {
        int progress = (int) ((downloaded * 100) / total);
        notificationBuilder.setProgress(100, progress, false);
        notificationBuilder.setContentText("Downloaded " + downloaded + " of " + total + " bytes");
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void sendProgressBroadcast(PostepInfo postepInfo) {
        Intent intent = new Intent("com.example.lo_lab_4_2.DOWNLOAD_PROGRESS");
        intent.putExtra("postepInfo", postepInfo);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
