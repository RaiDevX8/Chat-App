package com.example.chatapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.activity.Userprofile; // Import your Userprofile class
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

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
                        // Successfully signed in
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tokenTask -> {
                            if (tokenTask.isSuccessful()) {
                                String token = tokenTask.getResult();
                                // Store the token securely
                                storeToken(token);
                            } else {
                                Toast.makeText(LoginotpActivity.this, "Failed to get token", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Navigate to main chat app activity
                        Intent intent = new Intent(LoginotpActivity.this, Userprofile.class);
                        startActivity(intent);
                        finish(); // Close this activity
                    } else {
                        // Show error message for incorrect OTP
                        otpInput.setError("Invalid OTP. Please try again.");
                    }
                });
    }

    // Function to store the token
    private void storeToken(String token) {
        // Here you can store the token in SharedPreferences or your preferred method
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("firebaseToken", token);
        editor.apply();
    }
}
