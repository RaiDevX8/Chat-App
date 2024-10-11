package com.example.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp.R;
import com.hbb20.CountryCodePicker;

public class loginphone extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneinput;
    Button sendotp;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        countryCodePicker=findViewById(R.id.login_country_code);
        phoneinput=findViewById(R.id.login_mobile_no);
        sendotp=findViewById(R.id.send_otp);
        progressBar=findViewById(R.id.login_bar);
        progressBar.setVisibility(View.GONE);
        countryCodePicker.registerCarrierNumberEditText(phoneinput);

        sendotp.setOnClickListener((v)->
        {
            if(!countryCodePicker.isValidFullNumber())
            {
                phoneinput.setError("phone number not valid");
                return;
            }
            Intent intent=new Intent(this,LoginotpActivity.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);

        });

    }
}