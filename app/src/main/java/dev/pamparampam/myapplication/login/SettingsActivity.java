package dev.pamparampam.myapplication.login;

import static dev.pamparampam.myapplication.login.MyApplication.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.login.helper.Functions;


public class SettingsActivity extends AppCompatActivity {

    private MaterialButton btnChangePass, btnLogout;
    private HashMap<String,String> user = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnChangePass = findViewById(R.id.change_password);
        btnLogout = findViewById(R.id.logout);

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }
    private void init() {

        btnLogout.setOnClickListener(v -> logoutUser());

        btnChangePass.setOnClickListener(v -> {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.change_password, null);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Change Password");
            dialogBuilder.setCancelable(false);

            final TextInputLayout oldPassword = dialogView.findViewById(R.id.old_password);
            final TextInputLayout newPassword = dialogView.findViewById(R.id.new_password);

            dialogBuilder.setPositiveButton("Change", (dialog, which) -> {
                // empty
            });

            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            final AlertDialog alertDialog = dialogBuilder.create();

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(Objects.requireNonNull(oldPassword.getEditText()).getText().length() > 0 &&
                            Objects.requireNonNull(newPassword.getEditText()).getText().length() > 0);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            Objects.requireNonNull(oldPassword.getEditText()).addTextChangedListener(textWatcher);
            Objects.requireNonNull(newPassword.getEditText()).addTextChangedListener(textWatcher);

            alertDialog.setOnShowListener(dialog -> {
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setEnabled(false);

                b.setOnClickListener(view -> {
                    String email = user.get("email");
                    String old_pass = oldPassword.getEditText().getText().toString();
                    String new_pass = newPassword.getEditText().getText().toString();

                    if (!old_pass.isEmpty() && !new_pass.isEmpty()) {
                        changePassword(email, old_pass, new_pass);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Fill all values!", Toast.LENGTH_SHORT).show();
                    }

                });
            });

            alertDialog.show();
        });
    }

    private void logoutUser() {
        SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);

        sp.edit().putBoolean("logged",false).apply();

        Intent switchActivityIntent = new Intent(this, LoginActivity.class);
        switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        finish();



        startActivity(switchActivityIntent);

    }

    private void changePassword(final String email, final String old_pass, final String new_pass) {
        // Tag used to cancel the request



    }
    private void showDialog() {
        Functions.showProgressDialog(SettingsActivity.this, "Please wait...");
    }

    private void hideDialog() {
        Functions.hideProgressDialog(SettingsActivity.this);
    }
}