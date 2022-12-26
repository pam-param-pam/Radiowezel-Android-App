package dev.pamparampam.myapplication.radiowezel.helper;

import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.gustavoavila.websocketclient.WebSocketClient;

interface EventListener {
    void receive(String message) throws JsonProcessingException, JSONException;
}

public class WebSocket{

    private static WebSocketClient webSocketClient;
    //private static List<EventListener> listeners = new ArrayList<>();
    private static Map<Integer, EventListener> listeners = new HashMap<>();

    public void addListener(EventListener toAdd, int taskId) {
        listeners.put(taskId, toAdd);
    }
    public void removeListener(EventListener toRemove) {
        listeners.remove(toRemove);
    }
    public WebSocket(SharedPreferences sp , String url){
        if (webSocketClient == null) {

            URI uri;
            try {
                uri = new URI(url);
                webSocketClient = new WebSocketClient(uri) {
                    @Override
                    public void onOpen() {
                        System.out.println("onOpen");
                        webSocketClient.send("Hello, World!");
                    }

                    @Override
                    public void onTextReceived(String message) {

                        try {
                            JSONObject obj = new JSONObject(message);
                            String taskId = obj.getString("taskId");
                            for (Map.Entry<Integer, EventListener> set :
                                    listeners.entrySet()) {
                                if (set.getKey().toString().equals(taskId)) {
                                    set.getValue().receive(message);
                                }
                            }
                        } catch (JSONException | JsonProcessingException e) {
                            e.printStackTrace();
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
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onCloseReceived() {
                        System.out.println("onCloseReceived");
                    }
                };

                webSocketClient.setConnectTimeout(10000);
                webSocketClient.setReadTimeout(60000);
                webSocketClient.enableAutomaticReconnection(1000);
                webSocketClient.addHeader("Origin", "http://developer.example.com");
                String token = sp.getString("token", "token");

                webSocketClient.addHeader("token", token);
                webSocketClient.connect();

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(JSONObject jsonObject) {


        webSocketClient.send(jsonObject.toString());


    }

    public void send(String string) {


        webSocketClient.send(string);


    }
    public void close() {
        webSocketClient.close();

    }


}
