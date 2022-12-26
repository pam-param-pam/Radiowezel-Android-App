package dev.pamparampam.myapplication.radiowezel.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.MyApplication;
import dev.pamparampam.myapplication.radiowezel.SettingsActivity;
import dev.pamparampam.myapplication.radiowezel.VolleyCallback;
import dev.pamparampam.myapplication.radiowezel.widget.ProgressBarDialog;




public class Functions extends AppCompatActivity{

    // Main URL
    public static final String MAIN_URL = "http://192.168.1.14:8000/";
    // Auth URL
    public static final String AUTH_URL = "auth/";
    // Login URL
    public static final String LOGIN_URL = MAIN_URL + AUTH_URL + "token/";
    // Register URL
    public static final String REGISTER_URL = MAIN_URL + AUTH_URL + "register/";
    // OTP Verification
    public static final String OTP_VERIFY_URL = MAIN_URL + AUTH_URL + "mail/verify/";

    public static final String RESEND_VERIFY_MAIL = MAIN_URL + AUTH_URL + "mail/resend/";

    public static final String RESET_START_URL = MAIN_URL + AUTH_URL + "user/reset/start/";

    public static final String RESET_CHECK_URL = MAIN_URL + AUTH_URL + "user/reset/check/";

    public static final String CHANGE_PASSWORD = MAIN_URL + AUTH_URL + "user/change/password/";

    public static final String CHANGE_INFO = MAIN_URL + AUTH_URL + "user/change/info/";

    public static final String CHANGE_EMAIL = MAIN_URL + AUTH_URL + "user/change/email/";

    // Forgot Password
    public static final String RESET_FINISH_URL = MAIN_URL + "auth/user/reset/finish/";



    public static void refreshSetting() {

        SettingsActivity settingsActivity = SettingsActivity.getInstance();
        TextView email = settingsActivity.findViewById(R.id.AS_email);
        TextView username = settingsActivity.findViewById(R.id.AS_username);
        TextView firstName = settingsActivity.findViewById(R.id.settings_first_name);
        TextView lastName = settingsActivity.findViewById(R.id.AS_last_name);

        SharedPreferences sp = settingsActivity.getSharedPreferences("login", MODE_PRIVATE);
        String usernameSp = sp.getString("username", "Username N/A");
        username.setText(usernameSp);
        String emailSp = sp.getString("email", "Email N/A");
        email.setText(emailSp);
        String firstNameSp = sp.getString("first_name", "First Name N/A");
        firstName.setText(firstNameSp);
        String lastNameSp = sp.getString("last_name", "Last Name N/A");
        System.out.println(lastNameSp);

        lastName.setText(lastNameSp);


    }


    public static void makeRequest(final VolleyCallback callback, String URL, Map<String, String> params, boolean isTokenNeeded, Context ct, SharedPreferences sp) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                hideProgressDialog(ct);
                try {
                    JSONObject obj = new JSONObject(response);
                    callback.onSuccess(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog(ct);
                int statusCode = 0;
                Map<String, ArrayList<String>> map = null;
                if (error.networkResponse != null) {

                    byte[] htmlBodyBytes = error.networkResponse.data;

                    statusCode = error.networkResponse.statusCode;
                    try {
                        String JSON=new JSONObject(new String(htmlBodyBytes)).toString();
                        final ObjectMapper mapper = new ObjectMapper()
                                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                        map = mapper.readValue(JSON,new TypeReference<HashMap<String, ArrayList<String>>>(){});
                        System.out.println("ERROR123 " + map);
                        callback.onError(statusCode, map);

                    } catch (JSONException | JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }

                if (statusCode == 500) {
                    Toast.makeText(ct, "WHOOPSIE POOPSIE UWU WE MADE A FUCKY WUCKY THE CODE MONKEYS AT OUR HEADQUARTERS ARE WORKING VEVY HARD TO FIX THIS >-<", Toast.LENGTH_LONG).show();

                }
                if (statusCode == 429) {




                    Toast.makeText(ct, "TO MANY REQUESTS", Toast.LENGTH_LONG).show();
                }
                if (error instanceof NetworkError) {
                    Toast.makeText(ct, "NETWORK ERROR", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ct, "UNAUTHORIZED!", Toast.LENGTH_LONG).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(ct, "Parse Error " + statusCode, Toast.LENGTH_LONG).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ct, "Server doesn't respond... ", Toast.LENGTH_LONG).show();

                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                if (isTokenNeeded) {
                    String token = sp.getString("token", "token");
                    headers.put("Authorization", "Bearer " + token);

                }
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(request);

    }

    public static int randInt() {
        return ThreadLocalRandom.current().nextInt(1000, 9999);
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
