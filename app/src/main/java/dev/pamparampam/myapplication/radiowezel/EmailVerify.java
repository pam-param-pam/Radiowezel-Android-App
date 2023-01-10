package dev.pamparampam.myapplication.radiowezel;

import static dev.pamparampam.myapplication.radiowezel.helper.Functions.OTP_VERIFY_URL;
import static dev.pamparampam.myapplication.radiowezel.helper.Functions.makeRequest;
import static dev.pamparampam.myapplication.radiowezel.helper.Functions.showProgressDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.CookieBar;
import dev.pamparampam.myapplication.radiowezel.helper.Functions;


public class EmailVerify extends AppCompatActivity {
    private static final String TAG = EmailVerify.class.getSimpleName();

    private TextInputLayout textVerifyCode;
    private MaterialButton verifyBtn, resendBtn;
    private TextView otpCountDown;


    private static final String FORMAT = "%02d:%02d";

    Bundle bundle;


    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        textVerifyCode = findViewById(R.id.AEV_verify_code);
        verifyBtn = findViewById(R.id.AEV_verify_btn);
        resendBtn = findViewById(R.id.AEV_resend_code_btn);
        otpCountDown = findViewById(R.id.AEV_count_down);

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
                showProgressDialog(EmailVerify.this, "Verifying...");
                Map<String, String> params = new HashMap<>();

                params.put("code", code);

                makeRequest(new VolleyCallback() {

                    @Override
                    public void onSuccess(JSONObject result) {
                        Intent switchActivityIntent = new Intent(EmailVerify.this, HomeActivity.class);

                        switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);


                        startActivity(switchActivityIntent);

                    }

                    @Override
                    public void onError(int code, Map<String, ArrayList<String>> message) {
                        if (message.containsKey("code")) {
                            textVerifyCode.setError(String.join("\n", Objects.requireNonNull(message.get("code"))));
                        }

                    }

                },OTP_VERIFY_URL, params, true, this, sp);


            } else {
                textVerifyCode.setError("Please enter verification code");
            }
        });

        resendBtn.setEnabled(false);
        resendBtn.setOnClickListener(v -> {
            Map<String, String> params = new HashMap<>();

            makeRequest(new VolleyCallback() {

                @Override
                public void onSuccess(JSONObject result) {
                    CookieBar.build(EmailVerify.this)
                            .setTitle("Resend!")
                            .setDuration(1500)
                            .setBackgroundColor(R.color.successShine)
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();

                }

                @Override
                public void onError(int code, Map<String, ArrayList<String>> message) {
                    CookieBar.build(EmailVerify.this)
                            .setTitle("Unexpected, report this.")
                            .setDuration(1500)
                            .setBackgroundColor(R.color.actionErrorShine)
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();

                }


            },Functions.RESEND_VERIFY_MAIL, params, true, this, sp);
        });

        countDown();
    }

    private void countDown() {
        new CountDownTimer(10000, 1000) { // adjust the milli seconds here

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
        showProgressDialog(EmailVerify.this, title);
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
