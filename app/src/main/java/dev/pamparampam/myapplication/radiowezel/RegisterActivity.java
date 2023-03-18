package dev.pamparampam.myapplication.radiowezel;

import static dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions.hideSoftKeyboard;
import static dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions.isValidEmailAddress;
import static dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions.showProgressDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

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


public class RegisterActivity extends AppCompatActivity {

    private MaterialButton btnRegister, btnLinkToLogin;
    private TextInputLayout inputUsername;
    private TextInputLayout inputFirstName;
    private TextInputLayout inputLastName;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private TextInputLayout inputRepeatPassword;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUsername = findViewById(R.id.AR_username_input);
        inputFirstName = findViewById(R.id.AR_first_input_name);
        inputLastName = findViewById(R.id.AR_last_name_input);
        inputEmail = findViewById(R.id.AR_email_input);
        inputPassword = findViewById(R.id.AR_password_input);
        inputRepeatPassword = findViewById(R.id.AR_repeat_password_input);
        btnRegister = findViewById(R.id.AR_register_btn);
        btnLinkToLogin = findViewById(R.id.AR_login_btn);
        sp = MyApplication.getInstance().getSP();
        NoInternetDialogPendulum.Builder builder = new NoInternetDialogPendulum.Builder(
                this,
                getLifecycle()
        );
        builder.build();
        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    private void init() {
        // Login button Click Event
        btnRegister.setOnClickListener(view -> {
            // Hide Keyboard
            hideSoftKeyboard(RegisterActivity.this);

            String username = Objects.requireNonNull(inputUsername.getEditText()).getText().toString().trim();
            String firstName = Objects.requireNonNull(inputFirstName.getEditText()).getText().toString().trim();
            String lastName = Objects.requireNonNull(inputLastName.getEditText()).getText().toString().trim();
            String email = Objects.requireNonNull(inputEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(inputPassword.getEditText()).getText().toString().trim();
            String repeatPassword = Objects.requireNonNull(inputRepeatPassword.getEditText()).getText().toString().trim();

            // Check for empty data in the form

            if (!username.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !repeatPassword.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                if (isValidEmailAddress(email)) {
                    if (password.equals(repeatPassword)) {
                        registerUser(username, firstName, lastName, email, password, repeatPassword);
                    }
                    else {
                        CookieBar.build(RegisterActivity.this)
                                .setTitle("Passwords are not the same!")
                                .setDuration(1500)
                                .setBackgroundColor(R.color.errorShine)
                                .setCookiePosition(CookieBar.TOP)
                                .show();
                    }
                } else {
                    CookieBar.build(RegisterActivity.this)
                            .setTitle("Email is not valid!")
                            .setDuration(1500)
                            .setBackgroundColor(R.color.errorShine)
                            .setCookiePosition(CookieBar.TOP)
                            .show();
                }
            } else {
                CookieBar.build(RegisterActivity.this)
                        .setTitle("Please enter your details!")
                        .setDuration(1500)
                        .setBackgroundColor(R.color.errorShine)
                        .setCookiePosition(CookieBar.TOP)
                        .show();
            }

        });

        // Link to Register Screen
        btnLinkToLogin.setOnClickListener(view -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            finish();

        });
    }

    private void registerUser(final String username, final String firstName, final String lastName, final String email, final String password, final String repeatPassword) {
        showDialog("Registering");
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("email", email);
        params.put("password", password);
        params.put("repeatPassword", repeatPassword);
        NetworkManager.makeRequest(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                CookieBar.build(RegisterActivity.this)
                        .setTitle("A verify email was sent! Verify it first and then login.")
                        .setDuration(3000)
                        .setBackgroundColor(R.color.infoShine)
                        .setCookiePosition(CookieBar.TOP)
                        .show();

            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {
                inputUsername.setErrorEnabled(false);
                inputFirstName.setErrorEnabled(false);
                inputLastName.setErrorEnabled(false);
                inputEmail.setErrorEnabled(false);
                inputPassword.setErrorEnabled(false);
                inputRepeatPassword.setErrorEnabled(false);

                if (message.containsKey("username")) {
                    inputUsername.setError(String.join("\n", Objects.requireNonNull(message.get("username"))));
                }
                if (message.containsKey("email")) {
                    inputEmail.setError(String.join("\n", Objects.requireNonNull(message.get("email"))));
                }
                if (message.containsKey("first_name")) {
                    inputFirstName.setError(String.join("\n", Objects.requireNonNull(message.get("first_name"))));
                }
                if (message.containsKey("last_name")) {
                    inputLastName.setError(String.join("\n", Objects.requireNonNull(message.get("last_name"))));
                }
                if (message.containsKey("password")) {
                    inputPassword.setError(String.join("\n", Objects.requireNonNull(message.get("password"))));
                }
                if (message.containsKey("repeatPassword")) {
                    inputRepeatPassword.setError(String.join("\n", Objects.requireNonNull(message.get("repeatPassword"))));
                }

            }
        }, NetworkManager.REGISTER_URL, params, false, this);

    }

    private void showDialog(String title) {
        showProgressDialog(RegisterActivity.this, title);
    }

}
