package com.example.farmapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText mobileNumberEditText;
    private EditText passwordEditText;
    private TextView clickHere;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Trust all certificates (for development only)
        NetworkUtils.trustAllCertificates();

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
        String mobileNumber = mobileNumberEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (mobileNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new LoginTask().execute(mobileNumber, password);
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String mobileNumber = params[0];
            String password = params[1];

            try {
                URL url = new URL("https://slcloudapi.cloudstronic.com/api/Account/loginMobile");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("mobile", mobileNumber);
                jsonParam.put("password", password);

                Log.d(TAG, "Request Payload: " + jsonParam.toString());

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                Log.d(TAG, "Response Code: " + code);

                if (code == 200) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d(TAG, "Response: " + response.toString());

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONObject data = jsonResponse.getJSONObject("data");
                        String token = data.getString("token");

                        return token;
                    }
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.e(TAG, "Error Response: " + response.toString());
                    return "Login Failed";
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: ", e);
                return "Login Failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.equals("Login Failed")) {
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Oops Login failed. Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
