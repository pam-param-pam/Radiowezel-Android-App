package dev.pamparampam.myapplication.radiowezel;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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
    private MaterialButton verifyBtn, resendBtn;
    private TextView otpCountDown;


    private static final String FORMAT = "%02d:%02d";

    Bundle bundle;

    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        textVerifyCode = findViewById(R.id.verify_code);
        verifyBtn = findViewById(R.id.btnVerify);
        resendBtn = findViewById(R.id.btnResendCode);
        otpCountDown = findViewById(R.id.otpCountDown);

        bundle = getIntent().getExtras();


        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    private void init() {
        sp = getSharedPreferences("login", MODE_PRIVATE);
        verifyBtn.setOnClickListener(v -> {
            // Hide Keyboard
            Functions.hideSoftKeyboard(EmailVerify.this);

            String code = Objects.requireNonNull(textVerifyCode.getEditText()).getText().toString();

            if (!code.isEmpty()) {
                if(Functions.isValidVerifyCode(sp, this, code)) {
                    textVerifyCode.setErrorEnabled(false);
                }

            } else {
                textVerifyCode.setError("Please enter verification code");
            }
        });

        resendBtn.setEnabled(false);
        resendBtn.setOnClickListener(v -> {

            Functions.resendEmailVerifyCode(sp, this);
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
                resendBtn.setEnabled(true);
            }
        }.start();
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
