package com.example.lo_lab_5_1;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        long imageId = intent.getLongExtra("imageId", 0);

        FragmentDetails fragment = (FragmentDetails) getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
        fragment.setImageUri(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId));
    }
}
