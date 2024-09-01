package com.example.lo_lab_5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class SavedDrawingsActivity extends AppCompatActivity {

    private ListView listView;
    private File[] savedDrawings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_drawings);

        listView = findViewById(R.id.savedDrawingsList);
        loadSavedDrawings();

        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
    }

    private void loadSavedDrawings() {
        Context context = this;
        if (context != null) {
            File directory = context.getFilesDir();
            savedDrawings = directory.listFiles((dir, name) -> name.endsWith(".png"));
            String[] fileNames = new String[savedDrawings.length];
            for (int i = 0; i < savedDrawings.length; i++) {
                fileNames[i] = savedDrawings[i].getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, fileNames);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(context, ViewDrawingActivity.class);
                intent.putExtra("filePath", savedDrawings[position].getAbsolutePath());
                startActivity(intent);
            });
        }
    }
}
