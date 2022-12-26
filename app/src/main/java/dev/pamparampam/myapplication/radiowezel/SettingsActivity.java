package dev.pamparampam.myapplication.radiowezel;

import static dev.pamparampam.myapplication.radiowezel.helper.Functions.hideProgressDialog;
import static dev.pamparampam.myapplication.radiowezel.helper.Functions.makeRequest;
import static dev.pamparampam.myapplication.radiowezel.helper.Functions.refreshSetting;
import static dev.pamparampam.myapplication.radiowezel.helper.Functions.showProgressDialog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.helper.Functions;


public class SettingsActivity extends AppCompatActivity {



    private SharedPreferences sp;
    private static SettingsActivity instance;

    public static SettingsActivity getInstance() {
        return instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance =  this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
    private void init() {
        refreshSetting();

        TextView email, username, firstName, lastName;

        MaterialButton btnChangePassword, btnLogout;
        email = findViewById(R.id.AS_email);
        username = findViewById(R.id.AS_username);
        firstName = findViewById(R.id.settings_first_name);
        lastName = findViewById(R.id.AS_last_name);

        btnChangePassword = findViewById(R.id.change_password);
        btnLogout = findViewById(R.id.logout);


        username.setOnClickListener(v -> {
            changeInfoDialog("Username", "username");

        });
        firstName.setOnClickListener(v -> {
            changeInfoDialog("First Name", "first_name");

        });
        lastName.setOnClickListener(v -> {
            changeInfoDialog("Last Name", "last_name");

        });

        btnLogout.setOnClickListener(v -> logoutUser());

        btnChangePassword.setOnClickListener(v -> {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.as_change_password, null);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Change Password");
            dialogBuilder.setCancelable(false);

            final TextInputLayout oldPassword = dialogView.findViewById(R.id.as_CP_old_password_input);
            final TextInputLayout newPassword = dialogView.findViewById(R.id.as_CP_new_password_input);

            dialogBuilder.setPositiveButton("Change", (dialog, which) -> {
                //empty
            });

            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            final AlertDialog alertDialog = dialogBuilder.create();

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(Objects.requireNonNull(oldPassword.getEditText()).getText().length() > 0 && Objects.requireNonNull(newPassword.getEditText()).getText().length() > 0);
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
                    String old_pass = oldPassword.getEditText().getText().toString();
                    String new_pass = newPassword.getEditText().getText().toString();

                    if (!old_pass.isEmpty() && !new_pass.isEmpty()) {
                        changePassword(old_pass, new_pass, alertDialog, newPassword, oldPassword);
                    } else {
                        CookieBar.build(SettingsActivity.this)
                                .setTitle("Fill all values!")
                                .setDuration(1500)
                                .setBackgroundColor(R.color.errorShine)
                                .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                .show();
                    }

                });
            });

            alertDialog.show();
        });


    email.setOnClickListener(v -> {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.as_change_email, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Change Password");
        dialogBuilder.setCancelable(false);

        final TextInputLayout password = dialogView.findViewById(R.id.as_CE_password_input);
        final TextInputLayout newEmail = dialogView.findViewById(R.id.as_CE_new_email_input);

        dialogBuilder.setPositiveButton("Change", (dialog, which) -> {
            //empty
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        final AlertDialog alertDialog = dialogBuilder.create();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(Objects.requireNonNull(password.getEditText()).getText().length() > 0 && Objects.requireNonNull(password.getEditText()).getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        Objects.requireNonNull(password.getEditText()).addTextChangedListener(textWatcher);
        Objects.requireNonNull(newEmail.getEditText()).addTextChangedListener(textWatcher);

        alertDialog.setOnShowListener(dialog -> {
            final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setEnabled(false);

            b.setOnClickListener(view -> {
                String old_pass = password.getEditText().getText().toString();
                String new_email = newEmail.getEditText().getText().toString();

                if (!old_pass.isEmpty() && !new_email.isEmpty()) {
                    if (Functions.isValidEmailAddress(new_email)) {
                        changeEmail(old_pass, new_email, alertDialog, password, newEmail);
                    }
                    else {
                        CookieBar.build(SettingsActivity.this)
                                .setTitle("Email is not valid!")
                                .setDuration(1500)
                                .setBackgroundColor(R.color.errorShine)
                                .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                .show();

                    }

                } else {
                    CookieBar.build(SettingsActivity.this)
                            .setTitle("Fill all values!")
                            .setDuration(1500)
                            .setBackgroundColor(R.color.errorShine)
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();
                }

            });
        });

        alertDialog.show();
    });
}


    private void changeInfoDialog(String text, String type) {
        sp = getSharedPreferences("login", MODE_PRIVATE);

        View dialogView = getLayoutInflater().inflate(R.layout.as_change_info, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dialogView).setTitle(text).setCancelable(false).setPositiveButton("Change", (dialog, which) -> {
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();

        TextInputLayout mEditInfo = dialogView.findViewById(R.id.as_CI_edit_info_input);
        mEditInfo.setHint("New " + text);
        Objects.requireNonNull(mEditInfo.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditInfo.setErrorEnabled(false);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(mEditInfo.getEditText().getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        alertDialog.setOnShowListener(dialog -> {

            final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setEnabled(false);

            b.setOnClickListener(view -> {
                String info = mEditInfo.getEditText().getText().toString();

                if (!info.isEmpty()) {
                    changeInfo(type, info, mEditInfo, alertDialog);


                } else {
                    CookieBar.build(SettingsActivity.this)
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



    private void changeInfo(String type, String newValue, TextInputLayout mEditInfo, AlertDialog alertDialog) {
        showProgressDialog(SettingsActivity.this, "Changing...");

        sp = getSharedPreferences("login", MODE_PRIVATE);
        Map<String, String> params = new HashMap<>();
        params.put(type, newValue);

        makeRequest(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                sp.edit().putString(type, newValue).apply();

                refreshSetting();
                alertDialog.dismiss();
                CookieBar.build(SettingsActivity.this)
                        .setTitle(type + " changed")
                        .setDuration(1500)
                        .setBackgroundColor(R.color.successShine)
                        .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                        .show();
            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {
                if (message.containsKey("new_password")) {
                    mEditInfo.setError(String.join("\n", Objects.requireNonNull(message.get("new_password"))));
                }
                if (message.containsKey("old_password")) {
                    mEditInfo.setError(String.join("\n", Objects.requireNonNull(message.get("old_password"))));
                }
                if (message.containsKey("username")) {
                    mEditInfo.setError(String.join("\n", Objects.requireNonNull(message.get("username"))));
                }

            }
        },Functions.CHANGE_INFO, params, true, this, sp);

    }
    private void logoutUser() {
        sp = getSharedPreferences("login", MODE_PRIVATE);

        sp.edit().clear().apply();
        Intent switchActivityIntent = new Intent(this, LoginActivity.class);
        switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        finish();


        startActivity(switchActivityIntent);

    }

    private void changePassword(String old_pass, String new_pass, AlertDialog dialog, TextInputLayout newPassword, TextInputLayout oldPassword) {
        sp = getSharedPreferences("login", MODE_PRIVATE);
        showProgressDialog(SettingsActivity.this, "Changing...");
        Map<String, String> params = new HashMap<>();

        params.put("old_password", old_pass);
        params.put("new_password", new_pass);

        makeRequest(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject result) {
                CookieBar.build(SettingsActivity.this)
                        .setTitle("Password changed")
                        .setDuration(1500)
                        .setBackgroundColor(R.color.successShine)
                        .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                        .show();

                dialog.dismiss();

            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {
                newPassword.setErrorEnabled(false);
                oldPassword.setErrorEnabled(false);
                if (message.containsKey("new_password")) {
                    newPassword.setError(String.join("\n", Objects.requireNonNull(message.get("new_password"))));
                }
                if (message.containsKey("old_password")) {
                    oldPassword.setError(String.join("\n", Objects.requireNonNull(message.get("old_password"))));
                }
            }



        },Functions.CHANGE_PASSWORD, params, true, this, sp);

    }

    private void changeEmail(String passwordTxt, String new_email, AlertDialog dialog, TextInputLayout password, TextInputLayout newEmail) {
        sp = getSharedPreferences("login", MODE_PRIVATE);
        showProgressDialog(SettingsActivity.this, "Changing...");
        Map<String, String> params = new HashMap<>();

        params.put("password", passwordTxt);
        params.put("email", new_email);

        makeRequest(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject result) {

                sp.edit().putString("email", new_email).apply();
                refreshSetting();

                dialog.dismiss();

                Intent switchActivityIntent = new Intent(SettingsActivity.this, EmailVerify.class);

                switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();

                startActivity(switchActivityIntent);

            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {

                if (message.containsKey("email")) {
                    newEmail.setError(String.join("\n", Objects.requireNonNull(message.get("email"))));
                }
                if (message.containsKey("password")) {
                    password.setError(String.join("\n", Objects.requireNonNull(message.get("email"))));
                }

            }

        }, Functions.CHANGE_EMAIL, params, false, this, sp);

    }

    private void showDialog() {
        showProgressDialog(SettingsActivity.this, "Please wait...");
    }

    private void hideDialog() {
        hideProgressDialog(SettingsActivity.this);
    }
}