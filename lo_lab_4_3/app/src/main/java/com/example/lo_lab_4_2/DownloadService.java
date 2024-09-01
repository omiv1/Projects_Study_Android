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
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Download Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Downloading File")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);

        startForeground(NOTIFICATION_ID, notificationBuilder.build());

        InputStream input = null;
        FileOutputStream output = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();

            input = connection.getInputStream();
            File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Download");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File outputFile = createUniqueFile(outputDir, new File(url.getPath()).getName());
            output = new FileOutputStream(outputFile);

            byte[] data = new byte[4096];
            long total = 0;
            int count;

            while ((count = input.read(data)) != -1) {
                total += count;
                if (fileLength > 0) {
                    int progress = (int) (total * 100 / fileLength);
                    notificationBuilder.setProgress(100, progress, false)
                            .setContentText("Downloaded " + total + " of " + fileLength + " bytes");
                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
                    Log.d("DownloadService", "Progress: " + progress + "%");
                    sendProgressBroadcast(total, fileLength);
                }
                output.write(data, 0, count);
            }
            notificationBuilder.setContentText("Download complete")
                    .setProgress(0, 0, false)
                    .setOngoing(false);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        } catch (Exception e) {
            notificationBuilder.setContentText("Download failed")
                    .setProgress(0, 0, false)
                    .setOngoing(false);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (Exception ignored) {
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void sendProgressBroadcast(long downloaded, long total) {
        Intent intent = new Intent("com.example.lo_lab_4_2.DOWNLOAD_PROGRESS");
        intent.putExtra("downloaded", downloaded);
        intent.putExtra("total", total);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private File createUniqueFile(File directory, String fileName) {
        File file = new File(directory, fileName);
        if (!file.exists()) {
            return file;
        }

        String fileNameWithoutExtension = fileName;
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileNameWithoutExtension = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }

        int counter = 1;
        while (file.exists()) {
            file = new File(directory, fileNameWithoutExtension + "_" + counter + extension);
            counter++;
        }

        return file;
    }
}
