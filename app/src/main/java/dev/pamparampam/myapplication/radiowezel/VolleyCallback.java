package dev.pamparampam.myapplication.radiowezel;

import com.android.volley.NetworkResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public interface VolleyCallback {
    void onSuccess(JSONObject result);
    void onError(int code, Map<String, ArrayList<String>> message);
}
