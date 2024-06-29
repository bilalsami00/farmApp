package com.example.farmapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameField;
    private EditText lastNameField;
    private EditText mobileNumberField;
    private EditText passwordField;
    private Button registerBtn;
    private ImageView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameField = findViewById(R.id.FirstName);
        lastNameField = findViewById(R.id.lastName);
        mobileNumberField = findViewById(R.id.mobileNumber);
        passwordField = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);
        closeButton = findViewById(R.id.closeButton);

        registerBtn.setOnClickListener(v -> {
            if (validateRegistrationFields()) {
                Toast.makeText(RegisterActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed. Please fill all fields.", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(v -> finish());
    }

    private boolean validateRegistrationFields() {
        boolean isValid = true;

        if (TextUtils.isEmpty(firstNameField.getText().toString().trim())) {
            firstNameField.setError("First Name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(lastNameField.getText().toString().trim())) {
            lastNameField.setError("Last Name is required");
            isValid = false;
        }

        String mobileNumber = mobileNumberField.getText().toString().trim();
        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != 11) {
            mobileNumberField.setError("Mobile Number must be 11 digits");
            isValid = false;
        }

        if (TextUtils.isEmpty(passwordField.getText().toString().trim())) {
            passwordField.setError("Password is required");
            isValid = false;
        }

        return isValid;
    }
}
