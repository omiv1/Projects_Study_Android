package com.example.lolab_3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class InsertActivity extends AppCompatActivity {
    private EditText mManufacturerEditText;
    private EditText mModelEditText;
    private EditText mAndroidVersionEditText;
    private EditText mWebsiteEditText;
    private ElementViewModel mElementViewModel;
    private Element mElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        mManufacturerEditText = findViewById(R.id.editText_manufacturer);
        mModelEditText = findViewById(R.id.editText_model);
        mAndroidVersionEditText = findViewById(R.id.editText_android_version);
        mWebsiteEditText = findViewById(R.id.editText_website);

        mElementViewModel = new ViewModelProvider(this).get(ElementViewModel.class);
        long elementId = getIntent().getLongExtra("elementId", -1);
        if (elementId != -1) {
            mElementViewModel.getElementById(elementId).observe(this, new Observer<Element>() {
                @Override
                public void onChanged(Element element) {
                    mElement = element;
                    mManufacturerEditText.setText(mElement.getManufacturer());
                    mModelEditText.setText(mElement.getModel());
                    mAndroidVersionEditText.setText(mElement.getAndroidVersion());
                    mWebsiteEditText.setText(mElement.getWebsite());
                }
            });
        }

        Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateFields()) {
                    Toast.makeText(InsertActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent replyIntent = new Intent();
                String manufacturer = mManufacturerEditText.getText().toString();
                String model = mModelEditText.getText().toString();
                String androidVersion = mAndroidVersionEditText.getText().toString();
                String website = mWebsiteEditText.getText().toString();
                replyIntent.putExtra("manufacturer", manufacturer);
                replyIntent.putExtra("model", model);
                replyIntent.putExtra("android_version", androidVersion);
                replyIntent.putExtra("website", website);
                if (mElement != null) {
                    replyIntent.putExtra("id", mElement.getId());
                }
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button openWebsiteButton = findViewById(R.id.button_open_website);
        openWebsiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mWebsiteEditText.getText().toString();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }

    private boolean validateFields() {
        String manufacturer = mManufacturerEditText.getText().toString();
        String model = mModelEditText.getText().toString();
        String androidVersion = mAndroidVersionEditText.getText().toString();
        String website = mWebsiteEditText.getText().toString();

        if (manufacturer.isEmpty()) {
            mManufacturerEditText.setError("Pole nie może być puste");
            return false;
        }

        if (model.isEmpty()) {
            mModelEditText.setError("Pole nie może być puste");
            return false;
        }

        if (androidVersion.isEmpty()) {
            mAndroidVersionEditText.setError("Pole nie może być puste");
            return false;
        }

        if (website.isEmpty()) {
            mWebsiteEditText.setError("Pole nie może być puste");
            return false;
        }

        if (!website.matches("^(www\\.|https?://)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            mWebsiteEditText.setError("Strona musi pasować do wzorca");
            return false;
        }

        return true;
    }
}