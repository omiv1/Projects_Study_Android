package com.example.lo_lab_5;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DrawingSurface drawingSurface;

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

    private void navigateToSavedDrawings() {
        Intent intent = new Intent(MainActivity.this, SavedDrawingsActivity.class);
        startActivity(intent);
    }
}
