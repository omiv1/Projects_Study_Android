package com.example.lo_lab_5_1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements FragmentList.FragmentListListener {

    private static final String TAG = "MainActivity";
    private DrawingSurface mDrawingSurface;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawingSurface = findViewById(R.id.drawing_surface);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(5);

        Button btnRed = findViewById(R.id.btn_red);
        Button btnGreen = findViewById(R.id.btn_green);
        Button btnBlue = findViewById(R.id.btn_blue);
        Button btnClear = findViewById(R.id.btn_clear);
        Button btnSave = findViewById(R.id.btn_save);

        btnRed.setOnClickListener(v -> mPaint.setColor(Color.RED));
        btnGreen.setOnClickListener(v -> mPaint.setColor(Color.GREEN));
        btnBlue.setOnClickListener(v -> mPaint.setColor(Color.BLUE));
        btnClear.setOnClickListener(v -> mDrawingSurface.clearCanvas());
        btnSave.setOnClickListener(v -> saveImage());

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        // Set up fragments
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            FragmentList firstFragment = new FragmentList();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".png";
        imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
        }
        mImageUri = resolver.insert(imageCollection, imageDetails);
        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(mImageUri, "w", null);
             FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor())) {
            mDrawingSurface.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear();
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(mImageUri, imageDetails, null, null);
        }
    }

    @Override
    public void onItemSelected(long imageId) {
        FragmentDetails detailFragment = (FragmentDetails) getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
        if (detailFragment != null && detailFragment.isInLayout()) {
            detailFragment.setImageUri(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId));
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("imageId", imageId);
            startActivity(intent);
        }
    }
}
