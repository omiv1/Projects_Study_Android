package com.example.lo_lab_4_2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText urlEditText;
    private TextView fileSizeTextView;
    private TextView fileTypeTextView;
    private Button fetchInfoButton;
    private Button downloadButton;
    private ProgressBar progressBar;
    private TextView progressTextView;

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;

    private static final String KEY_URL = "url";
    private static final String KEY_FILE_SIZE = "file_size";
    private static final String KEY_FILE_TYPE = "file_type";
    private static final String KEY_PROGRESS = "progress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlEditText = findViewById(R.id.urlEditText);
        fileSizeTextView = findViewById(R.id.fileSizeTextView);
        fileTypeTextView = findViewById(R.id.fileTypeTextView);
        fetchInfoButton = findViewById(R.id.fetchInfoButton);
        downloadButton = findViewById(R.id.downloadButton);
        progressBar = findViewById(R.id.progressBar);
        progressTextView = findViewById(R.id.progressTextView);

        fetchInfoButton.setOnClickListener(v -> new FetchFileInfoTask().execute(urlEditText.getText().toString()));
        downloadButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //posiadane uprawnienia
                startDownloadService();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //prosba po odmowie (utracie)
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Needed")
                            .setMessage("This permission is needed to save files to your device.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            }

        });

        if (savedInstanceState != null) {
            urlEditText.setText(savedInstanceState.getString(KEY_URL));
            fileSizeTextView.setText(savedInstanceState.getString(KEY_FILE_SIZE));
            fileTypeTextView.setText(savedInstanceState.getString(KEY_FILE_TYPE));
            progressTextView.setText(savedInstanceState.getString(KEY_PROGRESS));
        }
    }

    private void startDownloadService() {
        String url = urlEditText.getText().toString();
        DownloadService.startActionDownload(this, url);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownloadService();
            } else {
                Toast.makeText(this, "Permission denied to write to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_URL, urlEditText.getText().toString());
        outState.putString(KEY_FILE_SIZE, fileSizeTextView.getText().toString());
        outState.putString(KEY_FILE_TYPE, fileTypeTextView.getText().toString());
        outState.putString(KEY_PROGRESS, progressTextView.getText().toString());
    }

    private class FetchFileInfoTask extends AsyncTask<String, Void, FileInfo> {

        @Override
        protected FileInfo doInBackground(String... strings) {
            String urlString = strings[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int length = connection.getContentLength();
                String type = connection.getContentType();

                connection.disconnect();

                return new FileInfo(length, type);
            } catch (Exception e) {
                e.printStackTrace();
                return new FileInfo(0, "Error fetching file info");
            }
        }

        @Override
        protected void onPostExecute(FileInfo result) {
            fileSizeTextView.setText(String.valueOf(result.length));
            fileTypeTextView.setText(result.type);
        }
    }

    private static class FileInfo {
        int length;
        String type;

        FileInfo(int length, String type) {
            this.length = length;
            this.type = type;
        }
    }

    private BroadcastReceiver downloadProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloaded = intent.getLongExtra("downloaded", 0);
            long total = intent.getLongExtra("total", 0);
            int progress = (int) (downloaded * 100 / total);
            progressBar.setProgress(progress);
            progressTextView.setText("Ilość pobranych bajtów: " + downloaded);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadProgressReceiver,
                new IntentFilter("com.example.lo_lab_4_2.DOWNLOAD_PROGRESS"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadProgressReceiver);
    }
}
