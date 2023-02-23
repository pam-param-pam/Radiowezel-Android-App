package dev.pamparampam.myapplication.radiowezel.helper;

import static dev.pamparampam.myapplication.radiowezel.helper.Functions.logoutUser;

import android.app.Activity;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.CookieBar;
import dev.pamparampam.myapplication.radiowezel.websocketclient.WebSocketClient;

interface EventListener {
    void receive(String message) throws JsonProcessingException, JSONException;
}

public class WebSocket{
    private static WebSocket webSocket;

    private static WebSocketClient webSocketClient;
    private static Map<Integer, EventListener> listeners = new HashMap<>();
    private static Map<Integer, ScheduledFuture<?>> shedulers = new HashMap<>();

    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static  WebSocket getInstance() {
        return webSocket;
    }
    private Activity activity;
    public void addListener(EventListener toAdd, int taskId) {

        ScheduledFuture<?> countdown = scheduler.schedule(new Runnable() {
            @Override
            public void run() {

                timeoutError();

            }}, 5, TimeUnit.SECONDS);

        shedulers.put(taskId, countdown);
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
    private void timeoutError() {

    }

    public void removeListener(EventListener toRemove) {
        try {
            listeners.remove(toRemove);
        }
        catch(NullPointerException ignored) { //this operation should be safe

        }


    }

    public WebSocket(SharedPreferences sp , Activity activity, String url){
        this.activity = activity;
        if (webSocketClient == null) {
            webSocket = this;
            URI uri;
            try {
                uri = new URI(url);
                webSocketClient = new WebSocketClient(uri) {
                    @Override
                    public void onOpen() {
                        fetchQueue();
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
                            for (Map.Entry<Integer, ScheduledFuture<?>> set :
                                    shedulers.entrySet()) {
                                if (set.getKey().toString().equals(taskId)) {
                                    set.getValue().cancel(false);
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
                        if (reason==3001) {
                            scheduler.shutdownNow();
                            activity.runOnUiThread(() -> logoutUser(sp, activity));

                        }
                    }
                };

                webSocketClient.setConnectTimeout(10000);
                webSocketClient.setReadTimeout(60000);
                webSocketClient.enableAutomaticReconnection(1000);
                webSocketClient.addHeader("Origin", "https://pamparampam.dev");
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
        webSocketClient.close(0, 1000, "Normal Closure");
        webSocketClient=null;
    }


}
