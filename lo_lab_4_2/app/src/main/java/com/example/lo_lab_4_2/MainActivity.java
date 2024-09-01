package com.example.lo_lab_4_2;

import android.Manifest;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 112;
    private EditText urlEditText;
    private TextView fileSizeTextView;
    private TextView fileTypeTextView;
    private Button fetchInfoButton;
    private Button downloadButton;
    private ProgressBar progressBar;
    private TextView progressTextView;

    private static final String STATE_FILE_SIZE = "stateFileSize";
    private static final String STATE_FILE_TYPE = "stateFileType";
    private static final String STATE_PROGRESS = "stateProgress";
    private static final String STATE_PROGRESS_TEXT = "stateProgressText";

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

        fetchInfoButton.setOnClickListener(v -> {
            String url = urlEditText.getText().toString();
            new FetchFileInfoTask().execute(url);
        });

        downloadButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //posiadane uprawnienia
                startDownloadService();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //prośba po odmowie (utracie)
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

        LocalBroadcastManager.getInstance(this).registerReceiver(downloadProgressReceiver,
                new IntentFilter("com.example.lo_lab_4_2.DOWNLOAD_PROGRESS"));

        if (savedInstanceState != null) {
            fileSizeTextView.setText(savedInstanceState.getString(STATE_FILE_SIZE));
            fileTypeTextView.setText(savedInstanceState.getString(STATE_FILE_TYPE));
            progressBar.setProgress(savedInstanceState.getInt(STATE_PROGRESS));
            progressTextView.setText(savedInstanceState.getString(STATE_PROGRESS_TEXT));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadProgressReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_FILE_SIZE, fileSizeTextView.getText().toString());
        outState.putString(STATE_FILE_TYPE, fileTypeTextView.getText().toString());
        outState.putInt(STATE_PROGRESS, progressBar.getProgress());
        outState.putString(STATE_PROGRESS_TEXT, progressTextView.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileSizeTextView.setText(savedInstanceState.getString(STATE_FILE_SIZE));
        fileTypeTextView.setText(savedInstanceState.getString(STATE_FILE_TYPE));
        progressBar.setProgress(savedInstanceState.getInt(STATE_PROGRESS));
        progressTextView.setText(savedInstanceState.getString(STATE_PROGRESS_TEXT));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Uprawnienia przyznane
                startDownloadService();
            } else {
                Log.e("MainActivity", "Write permission is not granted.");
            }
        }
    }

    private void startDownloadService() {
        String url = urlEditText.getText().toString();
        DownloadService.startActionDownload(MainActivity.this, url);
    }

    private BroadcastReceiver downloadProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PostepInfo postepInfo = intent.getParcelableExtra("postepInfo");
            if (postepInfo != null) {
                long downloaded = postepInfo.mPobranychBajtow;
                long total = postepInfo.mRozmiar;
                int progress = (int) (downloaded * 100 / total);
                progressBar.setProgress(progress);
                progressTextView.setText("Ilość pobranych bajtów: " + downloaded + "/" + total + " (" + postepInfo.mStatus + ")");
            }
        }
    };

    private class FetchFileInfoTask extends AsyncTask<String, Void, FileInfo> {

        @Override
        protected FileInfo doInBackground(String... strings) {
            String urlString = strings[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int length = connection.getContentLength();
                String type = connection.getContentType();
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
}
