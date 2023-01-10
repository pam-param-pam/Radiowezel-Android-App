package dev.pamparampam.myapplication.radiowezel.helper;

import static dev.pamparampam.myapplication.radiowezel.helper.Functions.logoutUser;

import android.app.Activity;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
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
    //private static List<EventListener> listeners = new ArrayList<>();
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

    private void timeoutError() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                CookieBar.build(activity)

                        .setMessage("Timeout error, server is not responding")
                        .setDuration(5000)
                        .setIcon(R.drawable.ic_error_shine)
                        .setBackgroundColor(R.color.actionErrorShine)
                        .setCookiePosition(CookieBar.TOP)
                        .show();
            }
        });

    }



    public void removeListener(EventListener toRemove) {
        listeners.remove(toRemove);
    }

    public WebSocket(SharedPreferences sp , Activity activity, String url){
        System.out.println("wanting to create");
        this.activity = activity;
        if (webSocketClient == null) {
            System.out.println("actually creating");
            webSocket = this;
            URI uri;
            try {
                uri = new URI(url);
                webSocketClient = new WebSocketClient(uri) {
                    @Override
                    public void onOpen() {
                        System.out.println("OPENEEED");
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
                        e.printStackTrace();
                    }

                    @Override
                    public void onCloseReceived(int reason, String description) {
                        System.out.println("ws closed");
                        if (reason==3001) {
                            scheduler.shutdownNow();

                            activity.runOnUiThread(new Runnable() {
                                public void run() {

                                    CookieBar.build(activity)
                                            .setMessage("Session expired. Please login again.")
                                            .setDuration(3000)
                                            .setBackgroundColor(R.color.infoShine)
                                            .setCookiePosition(CookieBar.TOP)
                                            .show();
                                }
                            });

                            scheduler = Executors.newScheduledThreadPool(1);


                            ScheduledFuture<?> idk = scheduler.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {

                                            logoutUser(sp, activity);
                                        }
                                    });
                                }
                            }, 8, TimeUnit.SECONDS);
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
