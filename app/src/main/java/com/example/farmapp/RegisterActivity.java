package com.example.farmapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstName, lastName, mobileNumber, password, confirmPassword, accountHolderID;
    private Button registerBtn;
    private ImageView closeButton;
    private TextView confirmOtpLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        mobileNumber = findViewById(R.id.mobileNumber);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        accountHolderID = findViewById(R.id.accountHolderID);
        registerBtn = findViewById(R.id.registerBtn);
        closeButton = findViewById(R.id.closeButton);
        confirmOtpLink = findViewById(R.id.confirmOtpLink);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Close the activity when the close button is clicked
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRegister();
            }
        });

        confirmOtpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickConfirmOtp(view); // Delegate to the method to handle the click
            }
        });
    }

    // Method to handle click on confirmOtpLink TextView
    public void onClickConfirmOtp(View view) {
        Intent intent = new Intent(RegisterActivity.this, ConfirmMobileNumberActivity.class);
        startActivity(intent);
    }

    private void handleRegister() {
        // Registration logic here
        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        String mobileNumberText = mobileNumber.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String confirmPasswordText = confirmPassword.getText().toString().trim();
        String accountHolderIDText = accountHolderID.getText().toString().trim();

        // Perform validation (e.g., empty fields check, password match check)
        if (firstNameText.isEmpty() || lastNameText.isEmpty() || mobileNumberText.isEmpty()
                || passwordText.isEmpty() || confirmPasswordText.isEmpty() || accountHolderIDText.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordText.equals(confirmPasswordText)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // If all validation passes, proceed with registration process
        new RegisterTask(firstNameText, lastNameText, mobileNumberText, passwordText, confirmPasswordText, accountHolderIDText).execute();
    }

    private class RegisterTask extends AsyncTask<Void, Void, String> {
        private String firstName, lastName, mobileNumber, password, confirmPassword, accountHolderID;

        RegisterTask(String firstName, String lastName, String mobileNumber, String password, String confirmPassword, String accountHolderID) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.mobileNumber = mobileNumber;
            this.password = password;
            this.confirmPassword = confirmPassword;
            this.accountHolderID = accountHolderID;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("https://slcloudapi.cloudstronic.com/api/Account/signupmobile"); // Replace with your actual API endpoint
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject jsonInput = new JSONObject();
                jsonInput.put("firstName", firstName);
                jsonInput.put("lastName", lastName);
                jsonInput.put("mobileNumber", mobileNumber);
                jsonInput.put("password", password);
                jsonInput.put("confirmPassword", confirmPassword);
                jsonInput.put("accountHolderID", accountHolderID);

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = urlConnection.getResponseCode();
                Log.d("RegisterTask", "Response Code: " + code);

                if (code == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                        StringBuilder responseBuilder = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            responseBuilder.append(responseLine.trim());
                        }
                        response = responseBuilder.toString();
                    }
                } else {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(urlConnection.getErrorStream(), "utf-8"))) {
                        StringBuilder errorResponseBuilder = new StringBuilder();
                        String errorLine;
                        while ((errorLine = br.readLine()) != null) {
                            errorResponseBuilder.append(errorLine.trim());
                        }
                        response = "Error: " + errorResponseBuilder.toString();
                    }
                }
            } catch (Exception e) {
                Log.e("RegisterTask", "Error during registration", e);
                response = "Exception: " + e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("RegisterTask", "Result: " + result);

            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONObject data = jsonResponse.getJSONObject("data");
                String otp = data.optString("otp", null);

                if (result.startsWith("Error") || result.startsWith("Exception")) {
                    Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    if (otp != null) {
                        intent.putExtra("otp", otp); // Pass OTP to the next activity if needed
                    }
                    startActivity(intent);
                    finish(); // Finish current activity
                }
            } catch (Exception e) {
                Log.e("RegisterTask", "Error parsing response", e);
                Toast.makeText(RegisterActivity.this, "Error parsing response. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
