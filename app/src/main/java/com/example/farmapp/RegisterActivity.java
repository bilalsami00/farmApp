package com.example.farmapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText mobileNumberEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        firstNameEditText = findViewById(R.id.FirstName);
        lastNameEditText = findViewById(R.id.lastName);
        mobileNumberEditText = findViewById(R.id.mobileNumber);
        passwordEditText = findViewById(R.id.password);

        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndRegister();
            }
        });
    }

    private void validateAndRegister() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String mobileNumber = mobileNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if any field is empty and display error if true
        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("First name is required");
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Last name is required");
            return;
        }

        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != 11) {
            mobileNumberEditText.setError("Please enter a valid mobile number (11 digits)");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        // Simulate registration logic
        boolean registrationSuccessful = performRegistration(firstName, lastName, mobileNumber, password);

        // Display appropriate toast message based on registration result
        if (registrationSuccessful) {
            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Simulated registration method (replace with actual logic)
    private boolean performRegistration(String firstName, String lastName, String mobileNumber, String password) {
        // Simulate successful registration if all fields are non-empty
        return !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
                && !TextUtils.isEmpty(mobileNumber) && mobileNumber.length() == 11
                && !TextUtils.isEmpty(password);
    }
}
