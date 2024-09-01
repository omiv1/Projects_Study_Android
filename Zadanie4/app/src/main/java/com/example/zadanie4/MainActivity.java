package com.example.zadanie4;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "DownloadServiceChannel";
    private ProgressBar progressBar;
    private TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        Button downloadButton = findViewById(R.id.downloadButton);
        Button infoButton = findViewById(R.id.infoButton);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        downloadButton.setOnClickListener(v -> {
            String url = ((EditText) findViewById(R.id.urlInput)).getText().toString();
            Intent intent = new Intent(MainActivity.this, DownloadService.class);
            intent.putExtra("urlPath", url);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        });

        infoButton.setOnClickListener(v -> {
            String url = ((EditText) findViewById(R.id.urlInput)).getText().toString();
            new DownloadInfoTask().execute(url);
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Kanał dla Download Service ";
            String description = "Kanał dla Download Service";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PostepInfo postepInfo = intent.getParcelableExtra("PostepInfo");
            progressText.setText("Pobrano: " + postepInfo.mPobranychBajtow + " bajtów");
            progressBar.setProgress((int) ((postepInfo.mPobranychBajtow * 100.0) / postepInfo.mRozmiar));
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("com.example.zadanie4.ACTION_PROGRESS"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private class DownloadInfoTask extends AsyncTask<String, Void, FileInfo> {
        protected FileInfo doInBackground(String... urls) {
            FileInfo fileInfo = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                int fileSize = connection.getContentLength();
                String fileType = connection.getContentType();

                fileInfo = new FileInfo(fileSize, fileType);

            } catch (IOException e) {
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return fileInfo;
        }

        @Override
        protected void onPostExecute(FileInfo result) {
            if (result != null) {
                ((TextView) findViewById(R.id.fileSize)).setText("Rozmiar pliku: " + result.size);
                ((TextView) findViewById(R.id.fileType)).setText("Typ pliku: " + result.type);
            }
        }
    }

    private static class FileInfo {
        long size;
        String type;

        FileInfo(long size, String type) {
            this.size = size;
            this.type = type;
        }
    }
}
