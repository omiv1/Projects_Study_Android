package com.example.lo_lab_5;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import android.view.MenuItem;

public class SavedDrawingsActivity extends AppCompatActivity implements SavedDrawingsFragment.OnDrawingSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_drawings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            SavedDrawingsFragment savedDrawingsFragment = new SavedDrawingsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.savedDrawingsFragmentContainer, savedDrawingsFragment)
                    .commit();
        }
    }

    @Override
    public void onDrawingSelected(String drawingPath) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape mode, show fragment
            ViewDrawingFragment viewDrawingFragment = ViewDrawingFragment.newInstance(drawingPath);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.drawingDetailContainer, viewDrawingFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            // Portrait mode, start new activity
            Intent intent = new Intent(this, ViewDrawingActivity.class);
            intent.putExtra("drawingPath", drawingPath);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the main activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
