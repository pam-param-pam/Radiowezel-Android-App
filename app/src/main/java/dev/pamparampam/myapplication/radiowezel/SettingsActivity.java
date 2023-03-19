package dev.pamparampam.myapplication.radiowezel;

import static dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions.isValidEmailAddress;
import static dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions.randInt;
import static dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions.showProgressDialog;
import static dev.pamparampam.myapplication.radiowezel.network.NetworkManager.makeRequest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.CookieBar;
import dev.pamparampam.myapplication.radiowezel.network.NetworkManager;
import dev.pamparampam.myapplication.radiowezel.network.Responder;
import dev.pamparampam.myapplication.radiowezel.network.WebSocket;


public class SettingsActivity extends AppCompatActivity {



    private SharedPreferences sp;
    private TextView email, username, firstName, lastName;
    private SeekBar volumeBar;
    private MaterialButton btnChangePassword, btnLogout, emailVerify;
    private WebSocket ws;
    private SwitchCompat smoothPauseSwitch, repeatSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);


        volumeBar = findViewById(R.id.AS_volume_bar);
        emailVerify = findViewById(R.id.AS_verify_email_btn);
        repeatSwitch = findViewById(R.id.AS_settings_repeat_switch);
        email = findViewById(R.id.AS_email);
        username = findViewById(R.id.AS_username);
        firstName = findViewById(R.id.AS_first_name);
        lastName = findViewById(R.id.AS_last_name);
        btnChangePassword = findViewById(R.id.AS_change_password_btn);
        btnLogout = findViewById(R.id.AS_logout_btn);

        smoothPauseSwitch = findViewById(R.id.AS_settings_smooth_pause_switch);
        sp = MyApplication.getInstance().getSP();
        boolean smoothPause = sp.getBoolean("smoothPause", false);
        smoothPauseSwitch.setChecked(smoothPause);

        NoInternetDialogPendulum.Builder builder = new NoInternetDialogPendulum.Builder(
                this,
                getLifecycle()
        );
        builder.build();

        ws = WebSocket.getInstance(SettingsActivity.this);

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
        fetchVolume();
        fetchRepeat();

        Responder responder = new Responder() {
            @Override
            public void receive(String message) throws JSONException {
                CookieBar.dismiss(SettingsActivity.this);
                JSONObject obj = new JSONObject(message);
                CookieBar.Builder cookieBar = CookieBar.build(SettingsActivity.this).setDuration(1500).setCookiePosition(CookieBar.TOP).setTitle(obj.get("info").toString());
                switch(obj.get("status").toString()) {
                    case "success":
                        cookieBar.setBackgroundColor(R.color.successShine);
                        cookieBar.setIcon(R.drawable.ic_success_shine);
                        break;
                    case "warning":
                        cookieBar.setBackgroundColor(R.color.warningShine);
                        cookieBar.setIcon(R.drawable.ic_warning_shine);

                        break;
                    case "info":
                        cookieBar.setBackgroundColor(R.color.infoShine);
                        cookieBar.setIcon(R.drawable.ic_info_shine);

                        break;
                    case "error":
                        cookieBar.setBackgroundColor(R.color.errorShine);
                        cookieBar.setIcon(R.drawable.ic_error_shine);

                        break;
                    default:
                        cookieBar.setBackgroundColor(R.color.actionErrorShine);
                        cookieBar.setIcon(R.drawable.ic_error_retro);

                        cookieBar.setMessage("Unexpected, report this.");
                }

                runOnUiThread(cookieBar::show);

            }
        };
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int taskId = randInt();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject()
                            .put("worker", "player")
                            .put("action", "set_volume").put("extras", new JSONObject()
                                    .put("volume", seekBar.getProgress()))
                            .put("taskId", taskId);
                    ws.addListener(responder, taskId);
                    ws.send(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        smoothPauseSwitch.setOnClickListener(v -> sp.edit().putBoolean("smoothPause", smoothPauseSwitch.isChecked()).apply());

        if (!sp.getBoolean("is_email_verified", false)) {
            emailVerify.setVisibility(View.VISIBLE);
            btnChangePassword.setVisibility(View.INVISIBLE);

        }

        emailVerify.setOnClickListener(v -> {
            Map<String, String> params = new HashMap<>();
            NetworkManager.makeRequest(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    CookieBar.build(SettingsActivity.this)
                            .setTitle("A verify email was sent!")
                            .setDuration(3000)
                            .setBackgroundColor(R.color.infoShine)
                            .setCookiePosition(CookieBar.TOP)
                            .show();


                }

                @Override
                public void onError(int code, Map<String, ArrayList<String>> message) {

                }
            },NetworkManager.RESEND_VERIFY_MAIL, params, true, SettingsActivity.this);

        });

        repeatSwitch.setOnClickListener(v -> {
            int taskId = randInt();

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject()
                        .put("worker", "player")
                        .put("action", "toggle_repeat")
                        .put("taskId", taskId);
                ws.addListener(responder, taskId);

                ws.send(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        username.setOnClickListener(v -> changeInfoDialog("Username", "username"));
        firstName.setOnClickListener(v -> changeInfoDialog("First Name", "first_name"));
        lastName.setOnClickListener(v -> changeInfoDialog("Last Name", "last_name"));


        btnLogout.setOnClickListener(v -> logoutUser(SettingsActivity.this));

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
                                .setCookiePosition(CookieBar.TOP)
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
        dialogBuilder.setTitle("Change Email");
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
                    if (isValidEmailAddress(new_email)) {
                        changeEmail(old_pass, new_email, alertDialog, password, newEmail);
                    }
                    else {
                        CookieBar.build(SettingsActivity.this)
                                .setTitle("Email is not valid!")
                                .setDuration(1500)
                                .setBackgroundColor(R.color.errorShine)
                                .setCookiePosition(CookieBar.TOP)
                                .show();

                    }

                } else {
                    CookieBar.build(SettingsActivity.this)
                            .setTitle("Fill all values!")
                            .setDuration(1500)
                            .setBackgroundColor(R.color.errorShine)
                            .setCookiePosition(CookieBar.TOP)
                            .show();
                }

            });
        });

        alertDialog.show();
    });
}


    private void fetchRepeat() {
        int taskId = randInt();

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject()
                    .put("worker", "player")
                    .put("action", "get_repeat")
                    .put("taskId", taskId);
            ws.addListener(new Responder() {
                @Override
                public void receive(String message) throws JsonProcessingException, JSONException {
                    super.receive(message);
                    JSONObject obj = new JSONObject(message);
                    boolean state = obj.getBoolean("state");

                    runOnUiThread(() -> repeatSwitch.setChecked(state));
                }
            }, taskId);

            ws.send(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fetchVolume() {
        int taskId = randInt();

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject()
                    .put("worker", "player")
                    .put("action", "get_volume")
                    .put("taskId", taskId);
            ws.addListener(new Responder() {
                @Override
                public void receive(String message) throws JsonProcessingException, JSONException {
                    super.receive(message);
                    JSONObject obj = new JSONObject(message);
                    int volume = obj.getInt("volume");

                    runOnUiThread(() -> volumeBar.setProgress(volume));
                }
            }, taskId);

            ws.send(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void changeInfoDialog(String text, String type) {

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
                            .setCookiePosition(CookieBar.TOP)
                            .show();
                }
            });
        });

        alertDialog.show();
    }

    private void changeInfo(String type, String newValue, TextInputLayout mEditInfo, AlertDialog alertDialog) {
        showProgressDialog(SettingsActivity.this, "Changing...");

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
                        .setCookiePosition(CookieBar.TOP)
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
        }, NetworkManager.CHANGE_INFO, params, true, this);

    }
    private void logoutUser(Activity activity) {
        NetworkManager.logoutUser(activity);
    }

    private void changePassword(String old_pass, String new_pass, AlertDialog dialog, TextInputLayout newPassword, TextInputLayout oldPassword) {

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
                        .setCookiePosition(CookieBar.TOP)
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

        }, NetworkManager.CHANGE_PASSWORD, params, true, this);

    }

    private void changeEmail(String passwordTxt, String new_email, AlertDialog dialog, TextInputLayout password, TextInputLayout newEmail) {
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
                CookieBar.build(SettingsActivity.this)
                        .setTitle("A verify email was sent to " + new_email + "!")
                        .setDuration(3000)
                        .setBackgroundColor(R.color.infoShine)
                        .setCookiePosition(CookieBar.TOP)
                        .show();
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

        }, NetworkManager.CHANGE_EMAIL, params, false, this);

    }
    public void refreshSetting() {

        String usernameSp = sp.getString("username", "Username N/A");
        username.setText(usernameSp);
        String emailSp = sp.getString("email", "Email N/A");
        email.setText(emailSp);
        String firstNameSp = sp.getString("first_name", "First Name N/A");
        firstName.setText(firstNameSp);
        String lastNameSp = sp.getString("last_name", "Last Name N/A");
        lastName.setText(lastNameSp);


    }

}