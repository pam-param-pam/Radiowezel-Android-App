package dev.pamparampam.myapplication.radiowezel;

import static dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions.hideSoftKeyboard;
import static dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions.isValidEmailAddress;
import static dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions.showProgressDialog;
import static dev.pamparampam.myapplication.radiowezel.network.NetworkManager.makeRequest;

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
import dev.pamparampam.myapplication.radiowezel.network.NetworkManager;


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
        sp = MyApplication.getInstance().getSP();

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
            hideSoftKeyboard(LoginActivity.this);
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
                    if (isValidEmailAddress(email)) {
                        reset(sp, email, alertDialog, mEditEmail);
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

    private void reset(SharedPreferences sp, String email, AlertDialog alertDialog, TextInputLayout mEditEmail) {
        showProgressDialog(LoginActivity.this, "Sending email...");
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        makeRequest(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject result) {

                alertDialog.dismiss();
                CookieBar.build(LoginActivity.this)
                        .setTitle("An Email was sent with reset instructions.")
                        .setDuration(3000)
                        .setBackgroundColor(R.color.infoShine)
                        .setCookiePosition(CookieBar.TOP)
                        .show();
            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {
                if (message.containsKey("email")) {
                    mEditEmail.setError(String.join("\n", Objects.requireNonNull(message.get("email"))));
                }
            }

        }, NetworkManager.RESET, params, false, this);
    }



    private void loginProcess(final String email, final String password) {
        showDialog("Logging...");
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
        }, NetworkManager.LOGIN_URL, params, false, this);


    }

    private void showDialog(String title) {
        showProgressDialog(LoginActivity.this, title);
    }
}


