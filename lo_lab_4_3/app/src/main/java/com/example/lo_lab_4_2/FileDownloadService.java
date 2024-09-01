package com.example.lo_lab_4_2;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloadService extends IntentService {
    private static final String ACTION_DOWNLOAD = "com.example.lo_lab_4_2.action.DOWNLOAD";
    private static final String EXTRA_URL = "com.example.lo_lab_4_2.extra.URL";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "FileDownloadChannel";

    public FileDownloadService() {
        super("FileDownloadService");
    }

    public static void startActionDownload(Context context, String url) {
        Intent intent = new Intent(context, FileDownloadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                handleActionDownload(url);
            }
        }
    }

    private void handleActionDownload(String url) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(notificationManager);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pobieranie pliku")
                .setContentText("Pobieranie w toku")
                .setSmallIcon(R.drawable.ic_download)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        startForeground(NOTIFICATION_ID, builder.build());

        HttpURLConnection connection = null;
        InputStream input = null;
        FileOutputStream output = null;
        try {
            URL downloadUrl = new URL(url);
            connection = (HttpURLConnection) downloadUrl.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("FileDownloadService", "Server returned HTTP " + connection.getResponseCode());
                return;
            }

            int fileLength = connection.getContentLength();

            input = connection.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "downloadedfile");
            output = new FileOutputStream(file);

            byte[] data = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);

                PostepInfo postepInfo = new PostepInfo();
                postepInfo.mPobranychBajtow = (int) total;
                postepInfo.mRozmiar = fileLength;
                postepInfo.mStatus = "Pobieranie trwa";

                Intent broadcastIntent = new Intent("com.example.lo_lab_4_2.DOWNLOAD_PROGRESS");
                broadcastIntent.putExtra("postepInfo", postepInfo);
                sendBroadcast(broadcastIntent);

                builder.setProgress(fileLength, (int) total, false);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
            builder.setContentText("Pobieranie zakończone")
                    .setProgress(0, 0, false);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "File Download";
            String description = "Channel for file download";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
