package com.example.lo_lab_4_1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lo_lab_4_1.DownloadService;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    private EditText urlEditText;
    private TextView sizeTextView;
    private TextView typeTextView;
    private Button downloadInfoButton;
    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlEditText = findViewById(R.id.urlEditText);
        sizeTextView = findViewById(R.id.sizeTextView);
        typeTextView = findViewById(R.id.typeTextView);
        downloadInfoButton = findViewById(R.id.downloadInfoButton);
        downloadButton = findViewById(R.id.downloadButton);

        downloadInfoButton.setOnClickListener(v -> downloadFileInfo());
        downloadButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION
                );
            } else {
                downloadFile();
            }
        });
    }

    private void downloadFileInfo() {
        // Example implementation to download file info
        String url = urlEditText.getText().toString();
        // Simulate downloading file info
        sizeTextView.setText("File Size: 12345 bytes");
        typeTextView.setText("File Type: application/x-xz");
        // You can implement actual network call to fetch file info here
    }

    private void downloadFile() {
        String url = urlEditText.getText().toString();
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("fileUrl", url);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadFile();
            } else {
                // Show a message to the user and prompt them to grant permissions
                Toast.makeText(this, "Storage permission is required to download files. Please enable it in the app settings.", Toast.LENGTH_LONG).show();
                // Open app settings
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }
}
