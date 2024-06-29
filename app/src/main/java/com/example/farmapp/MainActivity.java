package com.example.farmapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText mobileNumberEditText;
    private EditText passwordEditText;
    private TextView clickHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mobileNumberEditText = findViewById(R.id.mobileNumber);
        passwordEditText = findViewById(R.id.password);
        clickHere = findViewById(R.id.clickHere);

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> validateAndLogin());

        clickHere.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void validateAndLogin() {
        String mobileNumber = mobileNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (mobileNumber.length() != 11) {
            mobileNumberEditText.setError("Please enter a valid mobile number (11 digits)");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Please enter your password");
            return;
        }

        // Assuming login logic here, for now showing a toast
        Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();
    }
}
