package com.example.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class LoginotpActivity extends AppCompatActivity {

    private EditText otpInput;
    private Button nextButton;
    private ProgressBar progressBar;
    private String verificationId; // To hold the verification ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_otp); // Ensure this layout is correct

        // Initialize UI components
        otpInput = findViewById(R.id.login_otp);
        nextButton = findViewById(R.id.login_progress_bar);
        progressBar = findViewById(R.id.progress);

        // Get verification ID from intent
        verificationId = getIntent().getStringExtra("verificationId");

        progressBar.setVisibility(View.GONE); // Hide progress bar initially

        nextButton.setOnClickListener(v -> {
            String enteredOtp = otpInput.getText().toString().trim();
            verifyOtp(enteredOtp);
        });
    }

    private void verifyOtp(String enteredOtp) {
        progressBar.setVisibility(View.VISIBLE);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredOtp);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Navigate to main chat app activity on successful verification
                        Intent intent = new Intent(LoginotpActivity.this, Userprofile.class);
                        startActivity(intent);
                        finish(); // Close this activity
                    } else {
                        // Show error message for incorrect OTP
                        otpInput.setError("Invalid OTP. Please try again.");
                    }
                });
    }
}
