package dev.pamparampam.myapplication.radiowezel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.helper.Functions;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private SharedPreferences sp;


    private MaterialButton btnLogin, btnLinkToRegister, btnForgotPass;
    private TextInputLayout inputEmail, inputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            setContentView(R.layout.activity_login);

            inputEmail = findViewById(R.id.edit_email);
            inputPassword = findViewById(R.id.edit_password);
            btnLogin = findViewById(R.id.button_login);
            btnLinkToRegister = findViewById(R.id.button_register);
            btnForgotPass = findViewById(R.id.button_reset);

            init();


    }

    private void init() {
        // Login button Click Event
        btnLogin.setOnClickListener(view -> {


            String email = Objects.requireNonNull(inputEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(inputPassword.getEditText()).getText().toString().trim();

            // Hide Keyboard
            Functions.hideSoftKeyboard(LoginActivity.this);

            // Check for empty data in the form
            if (!email.isEmpty() && !password.isEmpty()) {
                if (Functions.isValidEmailAddress(email)) {
                    // login user
                    loginProcess(email, password);
                } else {
                    Toast.makeText(getApplicationContext(), "Email is not valid!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Prompt user to enter credentials

                Toast.makeText(getApplicationContext(), "Please enter the credentials!", Toast.LENGTH_LONG).show();
            }
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        // Forgot Password Dialog
        btnForgotPass.setOnClickListener(v -> forgotPasswordDialog());
    }

    private void forgotPasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.enter_email, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dialogView).setTitle("Forgot Password").setCancelable(false).setPositiveButton("Reset", (dialog, which) -> {
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();

        TextInputLayout mEditEmail = dialogView.findViewById(R.id.edit_email);

        Objects.requireNonNull(mEditEmail.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                        enterEmailCodeDialog(email);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Email is not valid!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Fill all values!", Toast.LENGTH_SHORT).show();
                }

            });
        });

        alertDialog.show();
    }
    private void enterEmailCodeDialog(String email) {
        View dialogView = getLayoutInflater().inflate(R.layout.enter_code, null);
        Button codeNotArrived = dialogView.findViewById(R.id.code_not_arrived);
        sp = getSharedPreferences("login", MODE_PRIVATE);
        codeNotArrived.setOnClickListener(view -> {
            Functions.resendResetCode(sp, this);
            Toast.makeText(getApplicationContext(), "Resending...", Toast.LENGTH_LONG).show();


        });
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dialogView).setTitle("Check Email").setCancelable(false).setPositiveButton("Verify", (dialog, which) -> {
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();

        TextInputLayout mEmailCode = dialogView.findViewById(R.id.edit_email);

        Objects.requireNonNull(mEmailCode.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
                    if (Functions.isValidVerifyCode(sp, this, code)) {
                        Functions.resetPassword(sp, this);
                        dialog.dismiss();
                        enterNewPasswordsDialog(email, code);
                    } else {
                        Toast.makeText(getApplicationContext(), "Code is not valid!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Fill all values!", Toast.LENGTH_SHORT).show();
                }

            });
        });

        alertDialog.show();
    }

    private void enterNewPasswordsDialog(String email, String code) {
        View dialogView = getLayoutInflater().inflate(R.layout.enter_new_passwords, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dialogView).setTitle("Enter New Password").setCancelable(false).setPositiveButton("Reset", (dialog, which) -> {
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();

        TextInputLayout password1 = dialogView.findViewById(R.id.new_password1);
        TextInputLayout password2 = dialogView.findViewById(R.id.new_password2);

        Objects.requireNonNull(password1.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                        //TODO AUTO LOGING? IDK
                        dialog.dismiss();

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Passwords are not the same!", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Fill all values!", Toast.LENGTH_SHORT).show();
                }

            });
        });

        alertDialog.show();
    }


    private void loginProcess(final String email, final String password) {
        sp = getSharedPreferences("login", MODE_PRIVATE);
        Functions.login(sp,this, email, password);

        /*


        new Login().hashPassword(password);


        Intent switchActivityIntent = new Intent(this, HomeActivity.class);
        hideDialog();
        switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(switchActivityIntent);
        */
    }





    private void showDialog(String title) {
        Functions.showProgressDialog(LoginActivity.this, title);
    }

    private void hideDialog() {
        Functions.hideProgressDialog(LoginActivity.this);
    }
}
