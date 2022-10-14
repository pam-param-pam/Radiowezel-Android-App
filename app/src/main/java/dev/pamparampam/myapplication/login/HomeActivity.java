package dev.pamparampam.myapplication.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import dev.pamparampam.myapplication.R;


import dev.pamparampam.myapplication.login.helper.Functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Akshay Raj on 6/16/2016.
 * akshay@snowcorp.org
 * www.snowcorp.org
 */

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    private MaterialButton btnChangePass, btnLogout;

    private HashMap<String,String> user = new HashMap<>();
    private RecyclerView recyclerView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        btnChangePass = findViewById(R.id.change_password);
        btnLogout = findViewById(R.id.logout);


        recyclerView = findViewById(R.id.recycler_view);

        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("Don't care");
        arrayList.add("Didn't ask");
        arrayList.add("Ratio");
        arrayList.add("Counter ratio");
        arrayList.add("Skill issue");
        arrayList.add("Cry about it");
        arrayList.add("Pinged owner");
        arrayList.add("Seethe");
        arrayList.add("Mald");
        arrayList.add("Fatherless");
        arrayList.add("Stfu");
        arrayList.add("No life");
        arrayList.add("Touch grass");
        arrayList.add("Cancelled");
        arrayList.add("Denied");
        arrayList.add("Exposed");
        arrayList.add("Rat");
        arrayList.add("Back pilled");
        arrayList.add("Stay bad");
        arrayList.add("Blocked");
        arrayList.add("Stay mad");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                arrayList );


        String s1[] = getResources().getStringArray(R.array.music_titles);
        String s2[] = getResources().getStringArray(R.array.music_desc);
        int images[] = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};
        MyAdapter myAdapter = new MyAdapter(this, s1, s2, images);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));





        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    private void init() {
        btnLogout.setOnClickListener(v -> logoutUser());

        btnChangePass.setOnClickListener(v -> {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
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
                    if(Objects.requireNonNull(oldPassword.getEditText()).getText().length() > 0 &&
                            Objects.requireNonNull(newPassword.getEditText()).getText().length() > 0){
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    } else {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
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
                        Toast.makeText(HomeActivity.this, "Fill all values!", Toast.LENGTH_SHORT).show();
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
        finish();
        startActivity(switchActivityIntent);

    }

    private void changePassword(final String email, final String old_pass, final String new_pass) {
        // Tag used to cancel the request
        String tag_string_req = "req_reset_pass";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.RESET_PASS_URL, response -> {
                    Log.d(TAG, "Reset Password Response: " + response);
                    hideDialog();
    
                    try {
                        JSONObject jObj = new JSONObject(response);

                        Toast.makeText(HomeActivity.this, jObj.getString("message"), Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
    
                }, error -> {
            Log.e(TAG, "Reset Password Error: " + error.getMessage());
            Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            hideDialog();
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                params.put("tag", "change_pass");
                params.put("email", email);
                params.put("old_password", old_pass);
                params.put("password", new_pass);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

        };

        // Adding request to volley request queue
        strReq.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        Functions.showProgressDialog(HomeActivity.this, "Please wait...");
    }

    private void hideDialog() {
        Functions.hideProgressDialog(HomeActivity.this);
    }

}