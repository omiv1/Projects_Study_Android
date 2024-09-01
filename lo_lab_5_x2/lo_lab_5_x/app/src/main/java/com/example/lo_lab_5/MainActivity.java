package com.example.lo_lab_5;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_CODE = 100;
    private DrawingSurface drawingSurface;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingSurface = findViewById(R.id.drawingSurface);

        findViewById(R.id.buttonClear).setOnClickListener(view -> drawingSurface.clearCanvas());
        findViewById(R.id.buttonSave).setOnClickListener(view -> {
            drawingSurface.saveDrawing();
            navigateToSavedDrawings();
        });
        findViewById(R.id.buttonViewSaved).setOnClickListener(view -> navigateToSavedDrawings());

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
            saveImage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_CODE);
        }
    }

    private void saveImage() {
        Log.d(TAG, "save image");
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri imageCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentValues imageDetails = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "IMG_" + timeStamp + ".png";
        imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        Uri imageUri = resolver.insert(imageCollection, imageDetails);

        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(imageUri, "w", null);
             FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor())) {
            synchronized (drawingSurface) {
                Bitmap bitmap = drawingSurface.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear();
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(imageUri, imageDetails, null, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            }
        }
    }

    private void navigateToSavedDrawings() {
        Intent intent = new Intent(MainActivity.this, SavedDrawingsActivity.class);
        startActivity(intent);
    }
}