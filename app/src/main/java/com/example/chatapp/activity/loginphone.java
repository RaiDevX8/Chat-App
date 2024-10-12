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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class loginphone extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    private EditText phoneInput;
    private Button sendOtp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        countryCodePicker = findViewById(R.id.login_country_code);
        phoneInput = findViewById(R.id.login_mobile_no);
        sendOtp = findViewById(R.id.send_otp);
        progressBar = findViewById(R.id.login_bar);
        progressBar.setVisibility(View.GONE);

        // Register the phone input field with the CountryCodePicker
        countryCodePicker.registerCarrierNumberEditText(phoneInput);

        sendOtp.setOnClickListener(v -> {
            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Phone number not valid");
                return;
            }

            // Send OTP
            String phoneNumber = countryCodePicker.getFullNumberWithPlus();
            sendOtp(phoneNumber);
        });
    }

    private void sendOtp(String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
                .setActivity(this)                 // Activity for callback binding
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // Automatically verified, proceed to the next screen
                        progressBar.setVisibility(View.GONE);
                        goToNextScreen(phoneNumber);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(loginphone.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        progressBar.setVisibility(View.GONE);
                        // Proceed to the OTP input screen, passing the verification ID
                        Intent intent = new Intent(loginphone.this, LoginotpActivity.class);
                        intent.putExtra("verificationId", verificationId); // Pass the verification ID
                        startActivity(intent);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void goToNextScreen(String phoneNumber) {
        Intent intent = new Intent(this, LoginotpActivity.class);
        intent.putExtra("phone", phoneNumber);
        startActivity(intent);
        finish();
    }
}
