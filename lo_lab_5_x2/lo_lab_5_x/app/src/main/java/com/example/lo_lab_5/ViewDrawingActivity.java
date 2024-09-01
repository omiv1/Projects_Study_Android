package com.example.lo_lab_5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

public class ViewDrawingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drawing);

        String drawingPath = getIntent().getStringExtra("drawingPath");
        ImageView imageView = findViewById(R.id.savedDrawingImage);

        Bitmap bitmap = BitmapFactory.decodeFile(drawingPath);
        imageView.setImageBitmap(bitmap);
    }
}
