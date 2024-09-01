package com.example.lo_lab_4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUrl;
    private TextView textViewFileInfo;
    private Button buttonDownloadInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUrl = findViewById(R.id.editTextUrl);
        textViewFileInfo = findViewById(R.id.textViewFileInfo);
        buttonDownloadInfo = findViewById(R.id.buttonDownloadInfo);

        buttonDownloadInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editTextUrl.getText().toString();
                new DownloadInfoTask() {
                    @Override
                    protected void onPostExecute(String fileInfo) {
                        textViewFileInfo.setText(fileInfo);
                    }
                }.execute(url);
            }
        });
    }
}