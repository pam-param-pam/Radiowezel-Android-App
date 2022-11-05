package dev.pamparampam.myapplication.radiowezel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import dev.pamparampam.myapplication.radiowezel.helper.Functions;
import dev.pamparampam.myapplication.R;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

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
        inputUsername = findViewById(R.id.edit_username);
        inputFirstName = findViewById(R.id.edit_first_name);
        inputLastName = findViewById(R.id.edit_last_name);
        inputEmail = findViewById(R.id.edit_email);
        inputPassword = findViewById(R.id.edit_password);
        inputRepeatPassword = findViewById(R.id.edit_repeat_password);

        btnRegister = findViewById(R.id.button_register);
        btnLinkToLogin = findViewById(R.id.button_login);

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    private void init() {
        // Login button Click Event
        btnRegister.setOnClickListener(view -> {
            // Hide Keyboard
            Functions.hideSoftKeyboard(RegisterActivity.this);

            String username = Objects.requireNonNull(inputUsername.getEditText()).getText().toString().trim();
            String firstName = Objects.requireNonNull(inputFirstName.getEditText()).getText().toString().trim();
            String lastName = Objects.requireNonNull(inputLastName.getEditText()).getText().toString().trim();
            String email = Objects.requireNonNull(inputEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(inputPassword.getEditText()).getText().toString().trim();
            String repeatPassword = Objects.requireNonNull(inputRepeatPassword.getEditText()).getText().toString().trim();

            // Check for empty data in the form
            registerUser("pam1", "malachowski", "jedrek", "reallpamparampam.pl@gmail.com", "Jedrek06", "jedrek06");
            /*
            if (!username.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !repeatPassword.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                if (Functions.isValidEmailAddress(email)) {
                    if (password.equals(repeatPassword)) {
                        registerUser(username, firstName, lastName, email, password, repeatPassword);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Passwords are not the same!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Email is not valid!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please enter your details!", Toast.LENGTH_LONG).show();
            }
            */
        });

        // Link to Register Screen
        btnLinkToLogin.setOnClickListener(view -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void registerUser(final String username, final String firstName, final String lastName, final String email, final String password, final String repeatPassword) {
        sp = getSharedPreferences("login", MODE_PRIVATE);
        Functions.register(sp, this, username, firstName, lastName, email, password, repeatPassword, inputUsername, inputFirstName, inputLastName, inputEmail, inputPassword, inputRepeatPassword);

    }

    private void showDialog() {
        Functions.showProgressDialog(RegisterActivity.this, "Registering ...");
    }

    private void hideDialog() {
        Functions.hideProgressDialog(RegisterActivity.this);
    }
}
