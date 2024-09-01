package com.example.zadanie4;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {

    private static final String TAG = "DownloadService";
    private static final int NOTIFICATION_ID = 1;

    private int status;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String urlPath = intent.getStringExtra("urlPath");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setContentTitle("Pobieranie pliku")
                .setContentText(urlPath)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        downloadFile(urlPath);

        return START_NOT_STICKY;
    }

    private void downloadFile(String urlPath) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "WRITE_EXTERNAL_STORAGE nie uzyskało permisji.");
            stopSelf();
            return;
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                HttpURLConnection connection = null;
                InputStream input = null;
                FileOutputStream output = null;
                try {
                    URL url = new URL(urlPath);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }

                    long fileLength = connection.getContentLength();

                    input = connection.getInputStream();
                    File file = new File(Environment.getExternalStorageDirectory(), urlPath.substring(urlPath.lastIndexOf('/') + 1));
                    output = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];
                    long total = 0;
                    int len;
                    while ((len = input.read(buffer)) != -1) {
                        total += len;
                        if (fileLength > 0) {
                            int progress = (int) ((total * 100.0) / fileLength);
                            publishProgress(progress);

                            // Aktualizacja powiadomienia z paskiem postępu
                            Notification notification = new NotificationCompat.Builder(DownloadService.this, MainActivity.CHANNEL_ID)
                                    .setContentTitle("Pobieranie pliku")
                                    .setContentText(urlPath)
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                                    .setContentIntent(pendingIntent)
                                    .setProgress(100, progress, false)
                                    .build();
                            NotificationManagerCompat.from(DownloadService.this).notify(NOTIFICATION_ID, notification);
                        }
                        output.write(buffer, 0, len);

                        // Wysyłanie rozgłoszenia z obiektem PostepInfo
                        Intent intent = new Intent("com.example.zadanie4.ACTION_PROGRESS");
                        PostepInfo postepInfo = new PostepInfo(total, fileLength, status);
                        intent.putExtra("PostepInfo", postepInfo);
                        sendBroadcast(intent);
                    }

                    Log.d(TAG, "Plik pobrany: " + file.getAbsolutePath());

                } catch (Exception e) {
                    Log.e(TAG, "Błąd pobierania pliku", e);
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Błąd zamykania strumieni", e);
                    }
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(DownloadService.this, MainActivity.CHANNEL_ID)
                        .setContentTitle("Pobieranie pliku")
                        .setContentText("Pobieranie zakończone")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setProgress(0, 0, false);
                NotificationManagerCompat.from(DownloadService.this).notify(NOTIFICATION_ID, builder.build());
            }
        }.execute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}