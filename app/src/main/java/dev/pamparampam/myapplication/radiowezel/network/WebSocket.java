package dev.pamparampam.myapplication.radiowezel.network;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dev.pamparampam.myapplication.radiowezel.Constants;
import dev.pamparampam.myapplication.radiowezel.MyApplication;
import dev.pamparampam.myapplication.radiowezel.VolleyCallback;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions;
import dev.pamparampam.myapplication.radiowezel.websocketclient.WebSocketClient;



public class WebSocket{
    private static WebSocket webSocket;

    private  WebSocketClient webSocketClient;
    private  Map<Integer, IEventListener> listeners = new HashMap<>();

    public static WebSocket getInstance(Context ct) {
        if (webSocket == null) {
            webSocket = new WebSocket(ct);
        }
        return webSocket;
    }


    private WebSocket(Context ct){

        NetworkManager.refresh(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                webSocketClient.setConnectTimeout(10000);
                webSocketClient.setReadTimeout(60000);
                webSocketClient.enableAutomaticReconnection(1000);
                webSocketClient.addHeader("Origin", "https://pamparampam.dev");
                String token = MyApplication.getInstance().getSP().getString("token", "token");

                webSocketClient.addHeader("token", token);
                webSocketClient.connect();
            }

            @Override
            public void onError(int code, Map<String, ArrayList<String>> message) {

            }
        },ct);


        webSocket = this;
        URI uri;
        try {
            uri = new URI(Constants.PLAYER_URL);
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen() {
                    fetchPosition();
                    fetchQueue();
                }

                @Override
                public void onTextReceived(String message) {

                    try {
                        JSONObject obj = new JSONObject(message);
                        String taskId = obj.getString("taskId");

                        for (Map.Entry<Integer, IEventListener> set :
                                listeners.entrySet()) {
                            if (set.getKey().toString().equals(taskId)) {
                                set.getValue().receive(message);
                            }
                        }

                    } catch (JSONException | JsonProcessingException exe) {
                        exe.printStackTrace();
                    }
                }

                @Override
                public void onBinaryReceived(byte[] data) {
                    System.out.println("onBinaryReceived");
                }

                @Override
                public void onPingReceived(byte[] data) {
                    System.out.println("onPingReceived");
                }

                @Override
                public void onPongReceived(byte[] data) {
                    System.out.println("onPongReceived");
                }

                @Override
                public void onException(Exception e) {
                    if (!(e instanceof UnknownHostException)) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onCloseReceived(int reason, String description) {
                    System.out.println("ws closed");

                }
            };



        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void addListener(IEventListener toAdd, int taskId) {

        listeners.put(taskId, toAdd);
    }
    public void fetchQueue() {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject()
                    .put("worker", "queue")
                    .put("action", "get").put("taskId", 100_000);

            webSocket.send(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchPosition() {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject()
                    .put("worker", "player")
                    .put("action", "get_pos").put("taskId", 100_000);

            webSocket.send(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void removeListener(IEventListener toRemove) {
        try {
            listeners.remove(toRemove);
        }
        catch(NullPointerException ignored) { //this operation should be safe

        }

    }
    public void send(JSONObject jsonObject) {
        webSocketClient.send(jsonObject.toString());
        Log.e("Websocket", jsonObject.toString());
    }

    public void send(String string) {
        webSocketClient.send(string);

    }
    public void close() {
        webSocketClient.close(0, 1000, "Normal Closure");
        webSocketClient=null;
        webSocket=null;
    }


}
