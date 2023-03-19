package dev.pamparampam.myapplication.radiowezel.network;

import static dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions.hideProgressDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
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

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.LoginActivity;
import dev.pamparampam.myapplication.radiowezel.MyApplication;
import dev.pamparampam.myapplication.radiowezel.RegisterActivity;
import dev.pamparampam.myapplication.radiowezel.VolleyCallback;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.CookieBar;


public class NetworkManager extends AppCompatActivity{

    // Main URL
    public static final String MAIN_URL = "https://pamparampam.dev/";
    // Auth URL
    public static final String AUTH_URL = "auth/";
    // Login URL
    public static final String LOGIN_URL = MAIN_URL + AUTH_URL + "token/";
    // Register URL
    public static final String REGISTER_URL = MAIN_URL + AUTH_URL + "register/";
    // Resend email verification
    public static final String RESEND_VERIFY_MAIL = MAIN_URL + AUTH_URL + "mail/resend/";

    public static final String LOGOUT_URL = MAIN_URL + AUTH_URL + "token/blacklist/";

    public static final String REFRESH_URL = MAIN_URL + AUTH_URL + "token/refresh/";


    // Resend email verification
    public static final String CHANGE_PASSWORD = MAIN_URL + AUTH_URL + "user/change/password/";

    public static final String CHANGE_INFO = MAIN_URL + AUTH_URL + "user/change/info/";

    public static final String CHANGE_EMAIL = MAIN_URL + AUTH_URL + "user/change/email/";
    // Forgot Password
    public static final String RESET = MAIN_URL + AUTH_URL + "reset/";


    public static void makeRequest(final VolleyCallback callback, String URL, Map<String, String> params, boolean isTokenNeeded, Context ct) {

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
                    if (!(activity instanceof RegisterActivity || activity instanceof LoginActivity)) {
                        refreshToken(callback, URL, params, isTokenNeeded, ct);

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
                            .setCookiePosition(CookieBar.TOP)
                            .show();
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                if (isTokenNeeded) {
                    String token = MyApplication.getInstance().getSP().getString("token", "token");
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


    public static void refreshToken(final VolleyCallback callback, String URL, Map<String, String> params, boolean isTokenNeeded, Context ct) {
        Toast.makeText(ct, "Refreshing...", Toast.LENGTH_SHORT).show();
        StringRequest request = new StringRequest(Request.Method.POST, REFRESH_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String token = obj.optString("access");

                    MyApplication.getInstance().getSP().edit().putString("token", token).apply();
                    makeRequest(callback, URL, params, isTokenNeeded, ct);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logoutUser((Activity) ct);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                String refresh = MyApplication.getInstance().getSP().getString("refresh", "refresh");
                params.put("refresh", refresh);


                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(request);
    }

    public static void refresh(VolleyCallback callback, Context ct) {
        Toast.makeText(ct, "Refreshing...", Toast.LENGTH_SHORT).show();

        StringRequest request = new StringRequest(Request.Method.POST, REFRESH_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    String token = obj.optString("access");
                    MyApplication.getInstance().getSP().edit().putString("token", token).apply();
                    callback.onSuccess(obj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logoutUser((Activity) ct);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                String refresh = MyApplication.getInstance().getSP().getString("refresh", "refresh");
                params.put("refresh", refresh);

                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(request);
    }


    public static void logoutUser(Activity activity) {
        try {
            WebSocket.getInstance(activity).close();

        }
        catch (NullPointerException ignored) {
            //who cares LOL get ratioed
        }
        Map<String, String> params = new HashMap<>();
        SharedPreferences sp = MyApplication.getInstance().getSP();
        params.put("refresh", sp.getString("refresh", "refresh"));
        NetworkManager.makeRequest(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                sp.edit().clear().apply();
                Intent switchActivityIntent = new Intent(activity, LoginActivity.class);
                switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                activity.finish();
                activity.startActivity(switchActivityIntent);
            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {
                CookieBar.build(activity)
                        .setTitle("Problem logging out...")
                        .setDuration(3000)
                        .setBackgroundColor(R.color.actionErrorShine)
                        .setCookiePosition(CookieBar.TOP)
                        .show();
                sp.edit().clear().apply();
                Intent switchActivityIntent = new Intent(activity, LoginActivity.class);
                switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                activity.finish();
                activity.startActivity(switchActivityIntent);

            }
        }, LOGOUT_URL, params, false, activity);



    }


}
