package com.example.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log; // Add this import for logging
import androidx.appcompat.app.AppCompatActivity;
import com.example.chatapp.R;
import com.example.chatapp.main_chat_app;

public class LoginotpActivity extends AppCompatActivity {

    private static final String HARD_CODED_OTP = "123456"; // Replace with your desired OTP
    private static final String TAG = "LoginotpActivity"; // Tag for logging

    private EditText otpInput;
    private Button nextButton;
    private ProgressBar progressBar;
    private TextView resendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_otp); // Ensure this layout is correct

        // Initialize UI components
        try {
            otpInput = findViewById(R.id.login_otp);
            nextButton = findViewById(R.id.login_progress_bar);
            progressBar = findViewById(R.id.progress);
            resendOtp = findViewById(R.id.resend_otp);

            progressBar.setVisibility(View.GONE); // Hide progress bar initially

            // Set OnClickListener for NEXT button
            nextButton.setOnClickListener(v -> {
                String enteredOtp = otpInput.getText().toString().trim();
                verifyOtp(enteredOtp);
            });

            // Optional: Set OnClickListener for Resend OTP
            resendOtp.setOnClickListener(v -> {
                // Handle resend OTP logic if needed
            });

        } catch (Exception e) {
            Log.e(TAG, "Error initializing UI components", e);
        }
    }

    private void verifyOtp(String enteredOtp) {
        // Show the progress bar while verifying
        progressBar.setVisibility(View.VISIBLE);

        // Simulate OTP verification delay
        new android.os.Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE); // Hide the progress bar after verification

            // Check if the entered OTP matches the hard-coded OTP
            if (enteredOtp.equals(HARD_CODED_OTP)) {
                // Navigate to the main chat app activity on successful verification
                Intent intent = new Intent(LoginotpActivity.this, Userprofile.class);
                startActivity(intent);
                finish(); // Close this activity
            } else {
                // Show error message for incorrect OTP
                otpInput.setError("Invalid OTP. Please try again.");
            }
        }, 2000); // Simulate a 2-second delay for verification
    }
}
