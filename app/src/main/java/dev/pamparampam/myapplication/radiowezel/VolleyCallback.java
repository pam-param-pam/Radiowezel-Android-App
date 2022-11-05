package dev.pamparampam.myapplication.radiowezel;

import com.android.volley.NetworkResponse;

import org.json.JSONObject;

public interface VolleyCallback {
    void onSuccess(JSONObject result);
    void onError(int code, JSONObject message);
}
