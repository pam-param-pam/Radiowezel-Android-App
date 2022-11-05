package dev.pamparampam.myapplication.radiowezel.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dev.pamparampam.myapplication.radiowezel.EmailVerify;
import dev.pamparampam.myapplication.radiowezel.HomeActivity;
import dev.pamparampam.myapplication.radiowezel.LoginActivity;
import dev.pamparampam.myapplication.radiowezel.MyApplication;
import dev.pamparampam.myapplication.radiowezel.VolleyCallback;
import dev.pamparampam.myapplication.radiowezel.widget.ProgressBarDialog;


public class Functions {

    //Main URL
    private static final String MAIN_URL = "http://178.42.189.210:8000/";

    // Login URL
    private static final String LOGIN_URL = MAIN_URL + "auth/token/";

    // Register URL
    private static final String REGISTER_URL = MAIN_URL + "auth/register/";

    // OTP Verification
    private static final String OTP_VERIFY_URL = MAIN_URL + "auth/mail/verify/";

    private static final String RESEND_VERIFY_MAIL = MAIN_URL + "auth/mail/resend/";

    private static final String RESET_URL = MAIN_URL + "auth/user/reset/";

    // Forgot Password
    private static final String RESET_PASSWORD_URL = MAIN_URL + "auth/user/reset/verify/";

    public static void isValidVerifyCode(SharedPreferences sp, Context ct, String code) {
        showProgressDialog(ct, "Verifying...");
        Map<String, String> params = new HashMap<>();

        params.put("code", code);

        makeRequest(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject result) {
                hideProgressDialog(ct);
                Intent switchActivityIntent = new Intent(ct, HomeActivity.class);

                switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ((Activity) ct).finish();

                ct.startActivity(switchActivityIntent);

            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {

            }


        }, ct, OTP_VERIFY_URL, params, true, sp);


    }
    public static void reset(SharedPreferences sp, Context ct, String email) {
        showProgressDialog(ct, "Sending email...");
        Map<String, String> params = new HashMap<>();

        params.put("email", email);


        makeRequest(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject result) {

            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {
            if (message.containsKey("email")) {
                Toast.makeText(ct, message.get("email").get(0), Toast.LENGTH_SHORT).show();
            }
            }


        }, ct, RESET_URL, params, false, sp);


    }
    public static void resetPassword(SharedPreferences sp, Context ct, String email, String code, String password) {
        showProgressDialog(ct, "Reseting...");
        Map<String, String> params = new HashMap<>();

        params.put("email", email);
        params.put("code", code);
        params.put("password", password);

        makeRequest(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject result) {

            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {

            }


        }, ct, RESET_PASSWORD_URL, params, false, sp);

    }

    public static void resendEmailVerifyCode(SharedPreferences sp, Context ct) {
        Map<String, String> params = new HashMap<>();

        makeRequest(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject result) {
                Toast.makeText(ct, "Resend!", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {
                Toast.makeText(ct, "Error!", Toast.LENGTH_SHORT).show();
                hideProgressDialog(ct);


            }


        }, ct, RESEND_VERIFY_MAIL, params, true, sp);
    }



    public static void register(SharedPreferences sp, Context ct, String usernameText, String firstNameText, String lastNameText, String emailText, String passwordText, String repeatPasswordText, TextInputLayout username, TextInputLayout first_name, TextInputLayout last_name, TextInputLayout email, TextInputLayout password, TextInputLayout repeatPassword) {
        showProgressDialog(ct, "Registering...");
        Map<String, String> params = new HashMap<>();

        params.put("username", usernameText);
        params.put("first_name", firstNameText);
        params.put("last_name", lastNameText);
        params.put("email", emailText);
        params.put("password", passwordText);
        params.put("repeatPassword", repeatPasswordText);
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

                hideProgressDialog(ct);

                Intent switchActivityIntent = new Intent(ct, EmailVerify.class);

                switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ((Activity) ct).finish();

                ct.startActivity(switchActivityIntent);

            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {
                username.setErrorEnabled(false);
                first_name.setErrorEnabled(false);
                last_name.setErrorEnabled(false);
                email.setErrorEnabled(false);
                password.setErrorEnabled(false);
                repeatPassword.setErrorEnabled(false);

                System.out.println("MESSSAAAAGE " + message);
                if (message.containsKey("username")) {
                    username.setError(String.join("\n", Objects.requireNonNull(message.get("username"))));
                }
                if (message.containsKey("email")) {
                    email.setError(String.join("\n", Objects.requireNonNull(message.get("email"))));
                }
                if (message.containsKey("first_name")) {
                    first_name.setError(String.join("\n", Objects.requireNonNull(message.get("first_name"))));
                }
                if (message.containsKey("last_name")) {
                    last_name.setError(String.join("\n", Objects.requireNonNull(message.get("last_name"))));
                }
                if (message.containsKey("password")) {
                    password.setError(String.join("\n", Objects.requireNonNull(message.get("password"))));
                }
                if (message.containsKey("repeatPassword")) {
                    repeatPassword.setError(String.join("\n", Objects.requireNonNull(message.get("repeatPassword"))));
                }

                hideProgressDialog(ct);


            }


        }, ct, REGISTER_URL, params, false, sp);

    }


    public static void login(SharedPreferences sp, Context ct, String email, String password) {
        showProgressDialog(ct, "Logging in ...");
        Map<String, String> params = new HashMap<>();
        params.put("username", email);
        params.put("new_password", password);

        makeRequest(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject result) {
                String token = result.optString("token");

                sp.edit().putString("token", token).apply();

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


                Intent switchActivityIntent = new Intent(ct, HomeActivity.class);

                switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ((Activity) ct).finish();

                ct.startActivity(switchActivityIntent);


            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {
            }


        }, ct, LOGIN_URL, params, false, sp);
    }

    public static void makeRequest(final VolleyCallback callback, Context ct, String URL, Map<String, String> params, boolean isTokenNeeded, SharedPreferences sp) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog(ct);
                try {
                    JSONObject obj = new JSONObject(response);
                    callback.onSuccess(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(0, null);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog(ct);
                int statusCode = 0;

                System.out.println(error.getLocalizedMessage());
                if (error.networkResponse != null) {
                    byte[] htmlBodyBytes = error.networkResponse.data;

                    statusCode = error.networkResponse.statusCode;

                    ObjectMapper om = new ObjectMapper();
                    TypeReference<Map<String, ArrayList<String>>> tr = new TypeReference<Map<String, ArrayList<String>>>() {
                    };
                    try {
                        Map<String, ArrayList<String>> val = om.readValue(htmlBodyBytes, tr);
                        System.out.println(val);
                        callback.onError(statusCode, val);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                if (error instanceof NetworkError) {
                    Toast.makeText(ct, "NETWORK ERROR", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ct, "UNAUTHORIZED!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(ct, "Parse Error " + statusCode, Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ct, "Server doesn't respond... " + statusCode, Toast.LENGTH_SHORT).show();

                }
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                if (isTokenNeeded) {
                    String token = sp.getString("token", "token");
                    headers.put("Authorization", "Bearer " + token);

                }
                return headers;
            }

            //Pass Your Parameters here
            @Override
            protected Map<String, String> getParams() {

                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(request);

    }




    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     */
    public void logoutUser(Context context) {

    }

    /**
     * Email Address Validation
     */
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * Hide Keyboard
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();

        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }

    }

    public static void showProgressDialog(Context context, String title) {
        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        DialogFragment newFragment = ProgressBarDialog.newInstance(title);
        newFragment.show(fm, "dialog");
    }

    public static void hideProgressDialog(Context context) {
        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }
}
