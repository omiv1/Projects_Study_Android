// GradesActivity.java
package com.example.lolab2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GradesActivity extends AppCompatActivity implements GradeAdapter.OnGradeChangeListener {

    private RecyclerView recyclerView;
    private GradeAdapter gradeAdapter;
    private ArrayList<String> subjects;
    private ArrayList<Integer> grades;
    private Button calculateAverageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(R.string.returnx);

        recyclerView = findViewById(R.id.recyclerView);
        calculateAverageButton = findViewById(R.id.calculateAverageButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Pobanie liczbę ocen
        int liczbaOcen = getIntent().getIntExtra("liczbaOcen", 0);

        // Pobiernie listy przedmiotów
        String[] allSubjects = getResources().getStringArray(R.array.subject);
        subjects = new ArrayList<>(Arrays.asList(allSubjects).subList(0, liczbaOcen));

        if (grades == null) {
            grades = new ArrayList<>(Collections.nCopies(liczbaOcen, 0));
        }

        if (savedInstanceState != null) {
            grades = savedInstanceState.getIntegerArrayList("grades");
        }

        gradeAdapter = new GradeAdapter(subjects, grades, this);
        recyclerView.setAdapter(gradeAdapter);

        checkAllGradesSelected();

        // Obliczenie średniej
        calculateAverageButton.setOnClickListener(v -> {
            double sum = 0;
            for (int grade : grades) {
                sum += grade;
            }
            double average = sum / grades.size();

            //Intent intent = new Intent(GradesActivity.this, MainActivity.class);
            Intent intent = new Intent();
            intent.putExtra("average", average);
            setResult(RESULT_OK, intent);
            //startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Zapisz
        outState.putIntegerArrayList("grades", grades);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        grades = savedInstanceState.getIntegerArrayList("grades");
        gradeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGradeChange() {
        // Sprawdź, czy wszystkie Radio są zaznaczone
        checkAllGradesSelected();
    }

    private void checkAllGradesSelected() {
        for (int grade : grades) {
            if (grade < 2) {
                calculateAverageButton.setEnabled(false);
                return;
            }
        }
        calculateAverageButton.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}