package com.example.lolab2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        EditText editTextImie = findViewById(R.id.editTextText3);
        EditText editTextNazwisko = findViewById(R.id.editTextText4);
        EditText editTextLiczbaOcen = findViewById(R.id.editTextText5);
        Button button = findViewById(R.id.button);

        button.setVisibility(View.GONE);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String imie = editTextImie.getText().toString();
                String nazwisko = editTextNazwisko.getText().toString();
                String liczbaOcenStr = editTextLiczbaOcen.getText().toString();

                if (!imie.isEmpty() && !nazwisko.isEmpty() && !liczbaOcenStr.isEmpty()) {
                    try {
                        int liczbaOcen = Integer.parseInt(liczbaOcenStr);
                        if (liczbaOcen >= 5 && liczbaOcen <= 15) {
                            button.setVisibility(View.VISIBLE);
                        } else {
                            button.setVisibility(View.GONE);
                        }
                    } catch (NumberFormatException e) {
                        button.setVisibility(View.GONE);
                    }
                } else {
                    button.setVisibility(View.GONE);
                }
            }
        };

        editTextImie.addTextChangedListener(textWatcher);
        editTextNazwisko.addTextChangedListener(textWatcher);
        editTextLiczbaOcen.addTextChangedListener(textWatcher);

        editTextImie.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String imie = editTextImie.getText().toString();
                    if (imie.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Imie nie moze byc puste", Toast.LENGTH_SHORT).show();
                        editTextImie.setError("Imie nie moze byc puste");
                    }
                }
            }
        });

        editTextNazwisko.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String nazwisko = editTextNazwisko.getText().toString();
                    if (nazwisko.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Nazwisko nie moze byc puste", Toast.LENGTH_SHORT).show();
                        editTextNazwisko.setError("Nazwisko nie moze byc puste");
                    }
                }
            }
        });

        editTextLiczbaOcen.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String liczbaOcenStr = editTextLiczbaOcen.getText().toString();
                    if (!liczbaOcenStr.isEmpty()) {
                        try {
                            int liczbaOcen = Integer.parseInt(liczbaOcenStr);
                            if (liczbaOcen < 5 || liczbaOcen > 15) {
                                Toast.makeText(MainActivity.this, "Liczba ocen musi byc pomiedzy 5 a 15", Toast.LENGTH_SHORT).show();
                                editTextLiczbaOcen.setError("Liczba ocen musi byc pomiedzy 5 a 15");
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Liczba ocen musi byc liczba", Toast.LENGTH_SHORT).show();
                            editTextLiczbaOcen.setError("Liczba ocen musi byc liczba");
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Liczba ocen nie moze byc pusta", Toast.LENGTH_SHORT).show();
                        editTextLiczbaOcen.setError("Liczba ocen nie moze byc pusta");
                    }
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GradesActivity.class);
                String liczbaOcenStr = editTextLiczbaOcen.getText().toString();
                int liczbaOcen = Integer.parseInt(liczbaOcenStr);
                intent.putExtra("liczbaOcen", liczbaOcen);
                startActivityForResult(intent, 1);
            }
        });

        TextView averageTextView = findViewById(R.id.averageTextView);
        Button averageButton = findViewById(R.id.averageButton);

        if (savedInstanceState != null) {
            averageTextView.setVisibility(savedInstanceState.getInt("averageTextViewVisibility"));
            averageButton.setVisibility(savedInstanceState.getInt("averageButtonVisibility"));
            averageTextView.setText(savedInstanceState.getString("averageTextViewText"));
            averageButton.setText(savedInstanceState.getString("averageButtonText"));
        }
        averageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double average = getIntent().getDoubleExtra("average", 0);
                if (average >= 3) {
                    Toast.makeText(MainActivity.this, R.string.you_pass, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.you_not_pass, Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                double average = data.getDoubleExtra("average", 0);
                TextView averageTextView = findViewById(R.id.averageTextView);
                Button averageButton = findViewById(R.id.averageButton);
                if (average > 0) {
                    averageTextView.setText(getString(R.string.average) + average);
                    averageTextView.setVisibility(View.VISIBLE);
                    if (average >= 3) {
                        averageButton.setText(R.string.super_text);
                    } else {
                        averageButton.setText(R.string.this_time);
                    }
                    averageButton.setVisibility(View.VISIBLE);
                } else {
                    averageTextView.setVisibility(View.GONE);
                    averageButton.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        TextView averageTextView = findViewById(R.id.averageTextView);
        Button averageButton = findViewById(R.id.averageButton);
        outState.putInt("averageTextViewVisibility", averageTextView.getVisibility());
        outState.putInt("averageButtonVisibility", averageButton.getVisibility());
        outState.putString("averageTextViewText", averageTextView.getText().toString());
        outState.putString("averageButtonText", averageButton.getText().toString());
    }
}