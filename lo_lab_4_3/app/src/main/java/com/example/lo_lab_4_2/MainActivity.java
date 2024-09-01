package com.example.lo_lab_4_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.Manifest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText urlEditText;
    private Button fetchInfoButton;
    private TextView fileInfoTextView;
    private Button downloadFileButton;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PostepInfo postepInfo = intent.getParcelableExtra("postepInfo");
            if (postepInfo != null) {
                fileInfoTextView.setText("Pobrano: " + postepInfo.mPobranychBajtow + "/" + postepInfo.mRozmiar + " bajtów");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlEditText = findViewById(R.id.urlEditText);
        fetchInfoButton = findViewById(R.id.fetchInfoButton);
        fileInfoTextView = findViewById(R.id.fileInfoTextView);
        downloadFileButton = findViewById(R.id.downloadFileButton);

        fetchInfoButton.setOnClickListener(v -> {
            String url = urlEditText.getText().toString();
            new FetchFileInfoTask().execute(url);
        });

        downloadFileButton.setOnClickListener(v -> {
            String url = urlEditText.getText().toString();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                FileDownloadService.startActionDownload(MainActivity.this, url);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.example.lo_lab_4_2.DOWNLOAD_PROGRESS");
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String url = urlEditText.getText().toString();
                FileDownloadService.startActionDownload(this, url);
            } else {
                Log.e("MainActivity", "Permission denied");
            }
        }
    }

    private class FetchFileInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String fileInfo = "";
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int fileSize = connection.getContentLength();
                String fileType = connection.getContentType();
                fileInfo = "Size: " + fileSize + " bytes\nType: " + fileType;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return fileInfo;
        }

        @Override
        protected void onPostExecute(String fileInfo) {
            fileInfoTextView.setText(fileInfo);
        }
    }
}
