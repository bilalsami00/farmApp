package com.example.farmapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText mobileNumberEditText;
    private EditText passwordEditText;
    private TextView clickHere;
    private PopupWindow popupWindow;
    private ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mobileNumberEditText = findViewById(R.id.mobileNumber);
        passwordEditText = findViewById(R.id.password);
        clickHere = findViewById(R.id.clickHere);
        mainLayout = findViewById(R.id.main);

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndLogin();
            }
        });

        clickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignupPopup();
            }
        });
    }

    private void validateAndLogin() {
        String mobileNumber = mobileNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != 11) {
            mobileNumberEditText.setError("Please enter a valid mobile number (11 digits)");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter your password");
            return;
        }

        // Assuming login logic here, for now showing a toast
        Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();
    }

    private void showSignupPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_register, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(mainLayout, android.view.Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });

        Button registerBtn = popupView.findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });
    }
}
