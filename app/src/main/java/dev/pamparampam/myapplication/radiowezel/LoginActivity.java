package dev.pamparampam.myapplication.radiowezel;

import static dev.pamparampam.myapplication.radiowezel.helper.Functions.RESET_CHECK_URL;
import static dev.pamparampam.myapplication.radiowezel.helper.Functions.makeRequest;
import static dev.pamparampam.myapplication.radiowezel.helper.Functions.showProgressDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.CookieBar;
import dev.pamparampam.myapplication.radiowezel.helper.Functions;


public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private AlertDialog alertDialog;
    private MaterialButton btnLogin, btnLinkToRegister, btnForgotPass;
    private TextInputLayout inputEmail, inputPassword;
    private static LoginActivity instance;

    public static LoginActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        sp = getSharedPreferences("login", MODE_PRIVATE);

        if (sp.getBoolean("logged", false)) {

            Intent switchActivityIntent = new Intent(this, HomeActivity.class);
            startActivity(switchActivityIntent);
        } else {

            setContentView(R.layout.activity_login);

            inputEmail = findViewById(R.id.AL_email_input);
            inputPassword = findViewById(R.id.AL_password_input);
            btnLogin = findViewById(R.id.AL_login_btn);
            btnLinkToRegister = findViewById(R.id.AL_register_btn);
            btnForgotPass = findViewById(R.id.AL_reset_btn);
            NoInternetDialogPendulum.Builder builder = new NoInternetDialogPendulum.Builder(
                    this,
                    getLifecycle()
            );
            builder.build();
            listeners();
        }


    }

    private void listeners() {
        // Login button Click Event
        btnLogin.setOnClickListener(view -> {
            String email = Objects.requireNonNull(inputEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(inputPassword.getEditText()).getText().toString().trim();
            Functions.hideSoftKeyboard(LoginActivity.this);
            if (!email.isEmpty() && !password.isEmpty()) {

                loginProcess(email, password);
            } else {
                CookieBar.build(LoginActivity.this)
                        .setTitle("Please enter the credentials!")
                        .setDuration(1500)
                        .setBackgroundColor(R.color.errorShine)
                        .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                        .show();
            }
        });
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });
        // Forgot Password Dialog
        btnForgotPass.setOnClickListener(view -> forgotPasswordDialog());
    }

    private void forgotPasswordDialog() {
        sp = getSharedPreferences("login", MODE_PRIVATE);
        View dialogView = getLayoutInflater().inflate(R.layout.al_enter_email, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dialogView).setTitle("Forgot Password").setCancelable(false).setPositiveButton("Reset", (dialog, which) -> {
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();

        TextInputLayout mEditEmail = dialogView.findViewById(R.id.al_EE_email_input);
        Objects.requireNonNull(mEditEmail.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditEmail.setErrorEnabled(false);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(mEditEmail.getEditText().getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        alertDialog.setOnShowListener(dialog -> {
            final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setEnabled(false);
            b.setOnClickListener(view -> {
                String email = mEditEmail.getEditText().getText().toString();
                if (!email.isEmpty()) {
                    if (Functions.isValidEmailAddress(email)) {
                        resetStart(sp, email, alertDialog, mEditEmail);
                    } else {
                        CookieBar.build(LoginActivity.this)
                                .setTitle("Email is not valid!")
                                .setDuration(1500)
                                .setBackgroundColor(R.color.errorShine)
                                .setCookiePosition(CookieBar.TOP)
                                .show();
                    }
                } else {
                    CookieBar.build(LoginActivity.this)
                            .setTitle("Fill all values!")
                            .setDuration(1500)
                            .setBackgroundColor(R.color.errorShine)
                            .setCookiePosition(CookieBar.TOP)
                            .show();
                }
            });
        });

        alertDialog.show();
    }

    private void resetStart(SharedPreferences sp, String email, AlertDialog alertDialog, TextInputLayout mEditEmail) {
        showProgressDialog(LoginActivity.this, "Sending email...");
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        makeRequest(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject result) {
                LoginActivity.getInstance().enterEmailCodeDialog(email);
                alertDialog.dismiss();
            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {
                if (message.containsKey("email")) {
                    mEditEmail.setError(String.join("\n", Objects.requireNonNull(message.get("email"))));
                }
            }

        }, Functions.RESET_START_URL, params, false, this, sp);
    }

    public void enterEmailCodeDialog(String email) {

        View dialogView = getLayoutInflater().inflate(R.layout.al_enter_code, null);
        Button codeNotArrived = dialogView.findViewById(R.id.al_code_not_arrived_btn);
        TextInputLayout mEmailCode = dialogView.findViewById(R.id.al_EC_email_code_input);
        alertDialog = new AlertDialog.Builder(this).setView(dialogView).setTitle("Check Email").setCancelable(false).setPositiveButton("Verify", (dialog, which) -> {
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();

        sp = getSharedPreferences("login", MODE_PRIVATE);
        codeNotArrived.setOnClickListener(view -> {
            resetStart(sp, email, alertDialog, mEmailCode);
            CookieBar.build(LoginActivity.this)
                    .setTitle("Resending...")
                    .setDuration(1500)
                    .setBackgroundColor(R.color.successShine)
                    .setCookiePosition(CookieBar.TOP)
                    .show();
        });

        Objects.requireNonNull(mEmailCode.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmailCode.setErrorEnabled(false);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(mEmailCode.getEditText().getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        alertDialog.setOnShowListener(dialog -> {
            final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setEnabled(false);

            b.setOnClickListener(view -> {
                String code = mEmailCode.getEditText().getText().toString();

                if (!code.isEmpty()) {
                    showProgressDialog(LoginActivity.this, "Checking...");
                    Map<String, String> params = new HashMap<>();

                    params.put("email", email);
                    params.put("code", code);

                    makeRequest(new VolleyCallback() {

                        @Override
                        public void onSuccess(JSONObject result) {
                            LoginActivity.getInstance().enterNewPasswordsDialog(email, code);
                            alertDialog.dismiss();

                        }

                        @Override
                        public void onError(int code, Map<String, ArrayList<String>> message) {
                            if (message.containsKey("code")) {
                                mEmailCode.setError(String.join("\n", Objects.requireNonNull(message.get("code"))));
                            }
                            if (message.containsKey("email")) {
                                mEmailCode.setError(String.join("\n", Objects.requireNonNull(message.get("email"))));
                            }
                        }
                    }, RESET_CHECK_URL, params, true, this, sp);

                } else {
                    CookieBar.build(LoginActivity.this)
                            .setTitle("Fill all values!")
                            .setDuration(1500)
                            .setBackgroundColor(R.color.errorShine)
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();
                }
            });
        });

        alertDialog.show();
    }

    public void enterNewPasswordsDialog(String email, String code) {
        View dialogView = getLayoutInflater().inflate(R.layout.al_enter_new_passwords, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dialogView).setTitle("Enter New Password").setCancelable(false).setPositiveButton("Reset", (dialog, which) -> {
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();

        TextInputLayout password1 = dialogView.findViewById(R.id.al_ENP_new_password_input);
        TextInputLayout password2 = dialogView.findViewById(R.id.al_ENP_repeat_new_password);

        Objects.requireNonNull(password1.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password1.setErrorEnabled(false);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(password1.getEditText().getText().length() > 0 && Objects.requireNonNull(password2.getEditText()).getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Objects.requireNonNull(password2.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password2.setErrorEnabled(false);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(password1.getEditText().getText().length() > 0 && Objects.requireNonNull(password2.getEditText()).getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        alertDialog.setOnShowListener(dialog -> {
            final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setEnabled(false);

            b.setOnClickListener(view -> {
                String password = password1.getEditText().getText().toString();
                String repeatPassword = password2.getEditText().getText().toString();

                if (!password.isEmpty() && !repeatPassword.isEmpty()) {
                    if (password.equals(repeatPassword)) {

                        showProgressDialog(LoginActivity.this, "Reseting...");
                        Map<String, String> params = new HashMap<>();
                        params.put("email", email);
                        params.put("code", code);
                        params.put("new_password", password);
                        makeRequest(new VolleyCallback() {

                            @Override
                            public void onSuccess(JSONObject result) {

                                CookieBar.build(LoginActivity.this)
                                        .setTitle("Password reset")
                                        .setDuration(1500)
                                        .setBackgroundColor(R.color.successShine)
                                        .setCookiePosition(CookieBar.TOP)
                                        .show();
                                alertDialog.dismiss();
                            }

                            @Override
                            public void onError(int code, Map<String, ArrayList<String>> message) {
                                if (message.containsKey("new_password")) {
                                    password1.setError(String.join("\n", Objects.requireNonNull(message.get("new_password"))));
                                    password2.setError(String.join("\n", Objects.requireNonNull(message.get("new_password"))));

                                }
                                if (message.containsKey("non_field_errors")) {
                                    password1.setError(String.join("\n", Objects.requireNonNull(message.get("non_field_errors"))));
                                    password2.setError(String.join("\n", Objects.requireNonNull(message.get("non_field_errors"))));

                                }
                            }

                        }, Functions.RESET_FINISH_URL, params, false, this, sp);

                    } else {
                        CookieBar.build(LoginActivity.this)
                                .setTitle("Passwords are not the same!")
                                .setDuration(1500)
                                .setBackgroundColor(R.color.errorShine)
                                .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                .show();

                    }
                } else {
                    CookieBar.build(LoginActivity.this)
                            .setTitle("Fill all values!")
                            .setDuration(1500)
                            .setBackgroundColor(R.color.errorShine)
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();
                }
            });
        });

        alertDialog.show();
    }

    private void loginProcess(final String email, final String password) {
        showDialog("Logging...");
        sp = getSharedPreferences("login", MODE_PRIVATE);
        Map<String, String> params = new HashMap<>();
        params.put("username", email);
        params.put("password", password);
        makeRequest(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject result) {
                String token = result.optString("access");

                sp.edit().putString("token", token).apply();
                String refresh = result.optString("refresh");
                sp.edit().putString("refresh", refresh).apply();

                sp.edit().putBoolean("logged", true).apply();

                int lifetime = result.optInt("lifetime");
                sp.edit().putInt("token_lifetime", lifetime).apply();

                String username = result.optString("username");
                sp.edit().putString("username", username).apply();

                String email = result.optString("email");
                sp.edit().putString("email", email).apply();

                String first_name = result.optString("first_name");
                sp.edit().putString("first_name", first_name).apply();

                String last_name = result.optString("last_name");
                sp.edit().putString("last_name", last_name).apply();

                boolean email_verified = result.optBoolean("is_email_verified");
                sp.edit().putBoolean("is_email_verified", email_verified).apply();


                Intent switchActivityIntent = new Intent(LoginActivity.this, HomeActivity.class);

                switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(switchActivityIntent);
            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {

                if (message.containsKey("detail")) {
                    inputPassword.setError(String.join("\n", Objects.requireNonNull(message.get("detail"))));
                    inputEmail.setError(String.join("\n", Objects.requireNonNull(message.get("detail"))));

                }
                if (message.containsKey("password")) {
                    inputPassword.setError(String.join("\n", Objects.requireNonNull(message.get("password"))));

                }
                if (message.containsKey("email")) {
                    inputEmail.setError(String.join("\n", Objects.requireNonNull(message.get("email"))));

                }

            }
        }, Functions.LOGIN_URL, params, false, this, sp);


    }

    private void showDialog(String title) {
        showProgressDialog(LoginActivity.this, title);
    }
}


