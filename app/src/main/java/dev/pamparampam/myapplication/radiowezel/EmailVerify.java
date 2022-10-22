package dev.pamparampam.myapplication.radiowezel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.helper.Functions;


public class EmailVerify extends AppCompatActivity {
    private static final String TAG = EmailVerify.class.getSimpleName();

    private TextInputLayout textVerifyCode;
    private MaterialButton btnVerify, btnResend;
    private TextView otpCountDown;


    private static final String FORMAT = "%02d:%02d";

    Bundle bundle;

    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        textVerifyCode = findViewById(R.id.verify_code);
        btnVerify = findViewById(R.id.btnVerify);
        btnResend = findViewById(R.id.btnResendCode);
        otpCountDown = findViewById(R.id.otpCountDown);

        bundle = getIntent().getExtras();


        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    private void init() {
        btnVerify.setOnClickListener(v -> {
            // Hide Keyboard
            Functions.hideSoftKeyboard(EmailVerify.this);

            String email = bundle.getString("email");
            String otp = Objects.requireNonNull(textVerifyCode.getEditText()).getText().toString();

            if (!otp.isEmpty()) {
                verifyCode(email, otp);
                textVerifyCode.setErrorEnabled(false);
            } else {
                textVerifyCode.setError("Please enter verification code");
            }
        });

        btnResend.setEnabled(false);
        btnResend.setOnClickListener(v -> {
            String email = bundle.getString("email");
            resendCode(email);
        });

        countDown();
    }

    private void countDown() {
        new CountDownTimer(70000, 1000) { // adjust the milli seconds here

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void onTick(long millisUntilFinished) {
                otpCountDown.setVisibility(View.VISIBLE);
                otpCountDown.setText("" + String.format(FORMAT, TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                otpCountDown.setVisibility(View.GONE);
                btnResend.setEnabled(true);
            }
        }.start();
    }

    private void verifyCode(final String email, final String otp) {

    }

    private void resendCode(final String email) {
        // Tag used to cancel the request


        showDialog("Resending code ...");


    }

    private void showDialog(String title) {
        Functions.showProgressDialog(EmailVerify.this, title);
    }

    private void hideDialog() {
        Functions.hideProgressDialog(EmailVerify.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        countDown();
    }
}
