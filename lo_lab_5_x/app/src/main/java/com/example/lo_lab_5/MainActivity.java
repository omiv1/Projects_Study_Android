package com.example.lo_lab_5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_CODE = 100;
    private static final int READ_CODE = 101;
    private DrawingSurface drawingSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingSurface = findViewById(R.id.drawingSurface);

        findViewById(R.id.buttonClear).setOnClickListener(view -> drawingSurface.clearCanvas());
        findViewById(R.id.buttonSave).setOnClickListener(view -> {
            saveImagePermissions();
        });
        findViewById(R.id.buttonViewSaved).setOnClickListener(view -> browseImagesPermissions());

        Button buttonBlack = findViewById(R.id.buttonBlack);
        Button buttonBlue = findViewById(R.id.buttonBlue);
        Button buttonGreen = findViewById(R.id.buttonGreen);
        Button buttonRed = findViewById(R.id.buttonRed);
        Button buttonWhite = findViewById(R.id.buttonWhite);

        buttonBlack.setBackgroundColor(Color.BLACK);
        buttonBlue.setBackgroundColor(Color.BLUE);
        buttonGreen.setBackgroundColor(Color.GREEN);
        buttonRed.setBackgroundColor(Color.RED);
        buttonWhite.setBackgroundColor(Color.WHITE);

        buttonBlack.setOnClickListener(view -> drawingSurface.setPaintColor(Color.BLACK));
        buttonBlue.setOnClickListener(view -> drawingSurface.setPaintColor(Color.BLUE));
        buttonGreen.setOnClickListener(view -> drawingSurface.setPaintColor(Color.GREEN));
        buttonRed.setOnClickListener(view -> drawingSurface.setPaintColor(Color.RED));
        buttonWhite.setOnClickListener(view -> drawingSurface.setPaintColor(Color.WHITE));
    }

    private void saveImagePermissions() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED ||
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawingSurface.saveDrawing();
            navigateToSavedDrawings();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_CODE);
        }
    }

    private void browseImagesPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED ||
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            navigateToSavedDrawings();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                drawingSurface.saveDrawing();
                navigateToSavedDrawings();
            }
        } else if (requestCode == READ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateToSavedDrawings();
            }
        }
    }

    private void navigateToSavedDrawings() {
        Intent intent = new Intent(MainActivity.this, SavedDrawingsActivity.class);
        startActivity(intent);
    }
}
