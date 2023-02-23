package dev.pamparampam.myapplication.radiowezel.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import dev.pamparampam.myapplication.radiowezel.LoginActivity;
import dev.pamparampam.myapplication.radiowezel.MyApplication;
import dev.pamparampam.myapplication.radiowezel.RegisterActivity;
import dev.pamparampam.myapplication.radiowezel.SettingsActivity;
import dev.pamparampam.myapplication.radiowezel.VolleyCallback;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.CookieBar;
import dev.pamparampam.myapplication.radiowezel.widget.ProgressBarDialog;




public class Functions extends AppCompatActivity{

    // Main URL
    public static final String MAIN_URL = "https://pamparampam.dev/";
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
    public static final String RESET_FINISH_URL = MAIN_URL + AUTH_URL + "user/reset/finish/";

    private static Handler handler = new Handler();





    public static void makeRequest(final VolleyCallback callback, String URL, Map<String, String> params, boolean isTokenNeeded, Context ct, SharedPreferences sp) {
        if (!isInternetConnected(ct)) {
            Activity activity = (Activity) ct;
            activity.runOnUiThread(new Runnable() {
                public void run() {

                        CookieBar.build(activity)
                                .setMessage("No internet...")
                                .setDuration(3000)
                                .setIcon(R.drawable.ic_error_shine)
                                .setBackgroundColor(R.color.actionErrorShine)
                                .setCookiePosition(CookieBar.TOP)
                                .show();

                }
            });
        }
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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
                Map<String, ArrayList<String>> map;
                if (error.networkResponse != null) {

                    byte[] htmlBodyBytes = error.networkResponse.data;

                    statusCode = error.networkResponse.statusCode;
                    try {
                        String JSON=new JSONObject(new String(htmlBodyBytes)).toString();
                        final ObjectMapper mapper = new ObjectMapper()
                                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                        map = mapper.readValue(JSON,new TypeReference<HashMap<String, ArrayList<String>>>(){});
                        callback.onError(statusCode, map);

                    } catch (JSONException | JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
                Activity activity = (Activity) ct;
                if (statusCode == 401) {
                    if (!(activity instanceof LoginActivity) && !(activity instanceof RegisterActivity)) {
                        CookieBar.build(activity)
                                .setMessage("Session expired. Please login again.")
                                .setDuration(3000)
                                .setBackgroundColor(R.color.infoShine)
                                .setCookiePosition(CookieBar.TOP)
                                .show();
                        handler.postDelayed(() -> logoutUser(sp, activity), 5000);
                    }

                }
                if (statusCode == 500) {
                    CookieBar.build(activity)
                            .setMessage("WHOOPSIE POOPSIE UWU WE MADE A FUCKY WUCKY THE CODE MONKEYS AT OUR HEADQUARTERS ARE WORKING VEVY HARD TO FIX THIS >-<")
                            .setDuration(1000)
                            .setBackgroundColor(R.color.actionErrorShine)
                            .setCookiePosition(CookieBar.TOP)
                            .show();

                }
                if (statusCode == 429) {

                    CookieBar.build(activity)
                            .setTitle("TO MANY REQUESTS")
                            .setMessage("Please slow down!")
                            .setDuration(5000)
                            .setBackgroundColor(R.color.errorShine)
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();
                }
                if (error instanceof NetworkError) {
                    CookieBar.build(activity)
                            .setTitle("NETWORK ERROR")
                            .setMessage("Looks like you don't have internet")
                            .setDuration(5000)
                            .setBackgroundColor(R.color.errorShine)
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();                }
                else if (error instanceof ParseError) {
                    Toast.makeText(ct, "Parse Error " + statusCode, Toast.LENGTH_LONG).show();

                } else if (error instanceof TimeoutError) {
                    CookieBar.build(activity)
                            .setTitle("Server doesn't respond... ")
                            .setDuration(5000)
                            .setBackgroundColor(R.color.actionErrorShine)
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();

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

    public static void logoutUser(SharedPreferences sp, Activity activity) {
        if (WebSocket.getInstance() != null) {
            WebSocket.getInstance().close();
        }

        sp.edit().clear().apply();
        Intent switchActivityIntent = new Intent(activity, LoginActivity.class);
        switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        activity.finish();


        activity.startActivity(switchActivityIntent);



    }
    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

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
