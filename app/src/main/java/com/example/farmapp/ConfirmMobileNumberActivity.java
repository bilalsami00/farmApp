package com.example.farmapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConfirmMobileNumberActivity extends AppCompatActivity {

    private EditText mobileNumber, otp;
    private Button confirmBtn;
    private ImageView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_mobile_number);

        mobileNumber = findViewById(R.id.mobileNumber);
        otp = findViewById(R.id.otp);
        confirmBtn = findViewById(R.id.confirmBtn);
        closeButton = findViewById(R.id.closeButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Close the activity when the close button is clicked
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleConfirm();
            }
        });
    }

    private void handleConfirm() {
        String mobile = mobileNumber.getText().toString().trim();
        String otpCode = otp.getText().toString().trim();

        if (mobile.isEmpty() || otpCode.isEmpty()) {
            Toast.makeText(this, "Please enter both mobile number and OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        new ConfirmOtpTask().execute(mobile, otpCode);
    }

    private class ConfirmOtpTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String mobile = params[0];
            String otpCode = params[1];
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("https://slcloudapi.cloudstronic.com/api/Account/confirmMobileNumber");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("mobileNumber", mobile); // Correct field name
                jsonParam.put("code", otpCode); // Correct field name

                DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                int responseCode = urlConnection.getResponseCode();
                Log.d("ConfirmOtpTask", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream(), "utf-8");
                    StringBuilder responseBuilder = new StringBuilder();
                    char[] buffer = new char[1024];
                    int read;
                    while ((read = reader.read(buffer)) != -1) {
                        responseBuilder.append(buffer, 0, read);
                    }
                    return responseBuilder.toString();
                } else {
                    InputStreamReader reader = new InputStreamReader(urlConnection.getErrorStream(), "utf-8");
                    StringBuilder errorResponseBuilder = new StringBuilder();
                    char[] buffer = new char[1024];
                    int read;
                    while ((read = reader.read(buffer)) != -1) {
                        errorResponseBuilder.append(buffer, 0, read);
                    }
                    return "Error: " + errorResponseBuilder.toString();
                }
            } catch (Exception e) {
                Log.e("ConfirmOtpTask", "Error confirming OTP", e);
                return "Exception: " + e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("ConfirmOtpTask", "Result: " + result);

            if (result.startsWith("Error") || result.startsWith("Exception")) {
                try {
                    JSONObject errorResponse = new JSONObject(result.substring(7)); // Remove "Error: " prefix
                    String message = errorResponse.optString("message", "Unknown error");
                    if ("User's SIM already confirmed".equals(message)) {
                        Toast.makeText(ConfirmMobileNumberActivity.this, "User's SIM is already confirmed.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ConfirmMobileNumberActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ConfirmMobileNumberActivity.this, "Failed to confirm mobile number. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("ConfirmOtpTask", "Error parsing error response", e);
                    Toast.makeText(ConfirmMobileNumberActivity.this, "Error parsing error response. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    boolean success = jsonResponse.optBoolean("success", false);

                    if (success) {
                        Toast.makeText(ConfirmMobileNumberActivity.this, "Mobile number confirmed successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ConfirmMobileNumberActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ConfirmMobileNumberActivity.this, "Failed to confirm mobile number. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("ConfirmOtpTask", "Error parsing response", e);
                    Toast.makeText(ConfirmMobileNumberActivity.this, "Error parsing response. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
