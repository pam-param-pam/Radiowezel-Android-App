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
import com.android.volley.ClientError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dev.pamparampam.myapplication.radiowezel.EmailVerify;
import dev.pamparampam.myapplication.radiowezel.HomeActivity;
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

    // Forgot Password
    private static final String RESET_PASS_URL = MAIN_URL + "auth/mail/resend/";

    public static boolean isValidVerifyCode(SharedPreferences sp, Context ct, String code) {
        showProgressDialog(ct,"Verifying...");
        Map<String, String> params = new HashMap<>();

        params.put("code", code);

        makeRequest(new VolleyCallback(){

            @Override
            public void onSuccess(JSONObject result){
                hideProgressDialog(ct);
                Intent switchActivityIntent = new Intent(ct, HomeActivity.class);

                switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ((Activity) ct).finish();

                ct.startActivity(switchActivityIntent);

            }

            @Override
            public void onError(int code, JSONObject message) {
                hideProgressDialog(ct);


            }


        }, ct, OTP_VERIFY_URL, params, true, sp);


        return false;
    }
    public static void resendResetCode(SharedPreferences sp, Context ct) {

    }
    public static void resendEmailVerifyCode(SharedPreferences sp, Context ct) {
    }
    public static void resetPassword(SharedPreferences sp, Context ct) {
        //TODO send code etc idk
    }
    public static void register(SharedPreferences sp, Context ct, String usernameText, String firstNameText, String lastNameText, String emailText, String passwordText, String repeatPasswordText, TextInputLayout username, TextInputLayout first_name, TextInputLayout last_name, TextInputLayout password, TextInputLayout email, TextInputLayout repeatPassword) {
        showProgressDialog(ct,"Registering...");
        Map<String, String> params = new HashMap<>();

        params.put("username", usernameText);
        params.put("first_name", firstNameText);
        params.put("last_name", lastNameText);
        params.put("email", emailText);
        params.put("password", passwordText);
        params.put("repeatPassword", repeatPasswordText);
        makeRequest(new VolleyCallback(){

            @Override
            public void onSuccess(JSONObject result){
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
            public void onError(int code, JSONObject message) {
                hideProgressDialog(ct);
                Gson gson = new Gson();
                StudentList list = gson.fromJson(response);
                Toast.makeText(ct, message, Toast.LENGTH_SHORT).show();

            }



        }, ct, REGISTER_URL, params, false, sp);

    }

    public static void resendCode() {

    }
    public static void login(SharedPreferences sp, Context ct, String email, String password) {
        showProgressDialog(ct,"Logging in ...");
        Map<String, String> params  = new HashMap<>();
        params.put("username", email);
        params.put("password", password);

        makeRequest(new VolleyCallback(){

            @Override
            public void onSuccess(JSONObject result){
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

                hideProgressDialog(ct);

                Intent switchActivityIntent = new Intent(ct, HomeActivity.class);

                switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ((Activity) ct).finish();

                ct.startActivity(switchActivityIntent);



            }

            @Override
            public void onError(int code, JSONObject message) {
                hideProgressDialog(ct);
            }





        }, ct, LOGIN_URL, params, false, sp);
    }
    public static void makeRequest(final VolleyCallback callback, Context ct, String URL, Map<String, String> params, boolean isTokenNeeded, SharedPreferences sp) {
        StringRequest request = new StringRequest(Request.Method.POST,URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                int statusCode;
                NetworkResponse response = null;
                String message = null;
                System.out.println(error.getLocalizedMessage());
                if (error.networkResponse != null) {
                    statusCode = error.networkResponse.statusCode;
                    byte[] htmlBodyBytes = error.networkResponse.data;
                    try {
                        JSONObject obj = new JSONObject(new String(htmlBodyBytes));
                        callback.onError(statusCode, obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    statusCode = 0;

                }



                if( error instanceof NetworkError) {
                    Toast.makeText(ct, "NETWORK ERROR", Toast.LENGTH_SHORT).show();
                }
                  else if( error instanceof AuthFailureError) {
                    Toast.makeText(ct, "UNAUTHORIZED!", Toast.LENGTH_SHORT).show();

                } else if( error instanceof ParseError) {
                    Toast.makeText(ct, "Parse Error " + statusCode, Toast.LENGTH_SHORT).show();

                } else if( error instanceof TimeoutError) {
                    Toast.makeText(ct, "Server doesn't respond... " + statusCode, Toast.LENGTH_SHORT).show();

                }
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers  = new HashMap<>();
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
     * */
    public void logoutUser(Context context){

    }

    /**
     *  Email Address Validation
     */
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     *  Hide Keyboard
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();

        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }

    }

    public static void showProgressDialog(Context context, String title) {
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        DialogFragment newFragment = ProgressBarDialog.newInstance(title);
        newFragment.show(fm, "dialog");
    }

    public static void hideProgressDialog(Context context) {
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }
}
