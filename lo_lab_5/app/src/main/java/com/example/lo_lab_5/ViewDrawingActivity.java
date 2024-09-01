package com.example.lo_lab_5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewDrawingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_drawing);

        String filePath = getIntent().getStringExtra("filePath");
        if (filePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            ImageView imageView = findViewById(R.id.savedDrawingImage);
            imageView.setImageBitmap(bitmap);
        }
    }
}
