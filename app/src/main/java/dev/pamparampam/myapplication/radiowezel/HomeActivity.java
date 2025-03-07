package dev.pamparampam.myapplication.radiowezel;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.CookieBar;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions;
import dev.pamparampam.myapplication.radiowezel.network.Responder;
import dev.pamparampam.myapplication.radiowezel.network.WebSocket;


public class HomeActivity extends AppCompatActivity implements StartDragListener {

    private FloatingActionButton settingsBtn, searchBtn;
    private ImageButton microphoneBtn, playBtn, nextBtn, pauseBtn;
    private SongListAdapter mAdapter;
    private ItemTouchHelper touchHelper;

    private RecyclerView recyclerView;
    private List<Item> list;
    private SharedPreferences sp;
    private SeekBar positionBar;

    private WebSocket ws;
    private TextView title, lengthTxt, positionTxt;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // below line is to get our inflater
        MenuInflater inflater = getMenuInflater();

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_menu, menu);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ws.fetchQueue();
                return true;
            }
        });
        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }

        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NoInternetDialogPendulum.Builder builder = new NoInternetDialogPendulum.Builder(
                this,
                getLifecycle()
        );
        builder.build();

        microphoneBtn = findViewById(R.id.AH_microphone_btn);
        playBtn = findViewById(R.id.AH_play_btn);
        pauseBtn = findViewById(R.id.AH_pause_btn);
        nextBtn = findViewById(R.id.AH_next_btn);
        positionBar = findViewById(R.id.AH_position_seekbar);
        settingsBtn = findViewById(R.id.AH_settings_btn);
        searchBtn = findViewById(R.id.AH_search_btn);
        title = findViewById(R.id.AH_title_text);
        positionTxt = findViewById(R.id.AH_position_text);
        lengthTxt = findViewById(R.id.AH_length_text);
        recyclerView = findViewById(R.id.AH_songs_recycler_view);

        positionBar.setMax(10000);
        positionTxt.setPaintFlags(0);
        lengthTxt.setPaintFlags(0);
        title .setPaintFlags(0);
        sp = MyApplication.getInstance().getSP();
        ws = WebSocket.getInstance(HomeActivity.this);
        connect();
        listeners();
        buildRecyclerView();

    }
    @Override
    public void onBackPressed() {

    }

    private void buildRecyclerView() {

        ws.fetchQueue();

        if(list == null){

            list = new ArrayList<>();

            Item item = new Item("https://img.freepik.com/premium-vector/system-software-update-upgrade-concept-loading-process-screen-vector-illustration_175838-2182.jpg?w=2000", "Loading...","ID", "CEO OF SLOW INTERNET", "LENGTH");
            list.add(item);

            mAdapter = new SongListAdapter(this, this, list, HomeActivity.this);
            ItemTouchHelper.Callback callback = new ItemMoveCallback(mAdapter);

            touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);

            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }



    }
    Responder responder = new Responder() {
        @Override
        public void receive(String message) throws JsonProcessingException, JSONException {
            CookieBar.dismiss(HomeActivity.this);

            super.receive(message);
            System.out.println(message);

            JSONObject obj = new JSONObject(message);
            CookieBar.Builder cookieBar = CookieBar.build(HomeActivity.this).setDuration(1500).setCookiePosition(CookieBar.TOP).setTitle(obj.get("info").toString());
            switch(obj.get("status").toString()) {
                case "success":
                    cookieBar.setBackgroundColor(R.color.successShine);
                    cookieBar.setIcon(R.drawable.ic_success_shine);
                    break;
                case "warning":
                    cookieBar.setBackgroundColor(R.color.warningShine);
                    cookieBar.setIcon(R.drawable.ic_warning_shine);

                    break;
                case "info":
                    cookieBar.setBackgroundColor(R.color.infoShine);
                    cookieBar.setIcon(R.drawable.ic_info_shine);

                    break;
                case "error":
                    cookieBar.setBackgroundColor(R.color.errorShine);
                    cookieBar.setIcon(R.drawable.ic_error_shine);

                    break;
                default:
                    cookieBar.setBackgroundColor(R.color.actionErrorShine);
                    cookieBar.setIcon(R.drawable.ic_error_retro);

                    cookieBar.setMessage("Unexpected, report this.");
            }

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    cookieBar.show();

                }
            });

        }
    };

    private void listeners() {
        settingsBtn.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

        });
        searchBtn.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        });
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int taskId = Functions.randInt();

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject()
                            .put("worker", "player")
                            .put("action", "seek").put("extras", new JSONObject()
                                    .put("seconds", seekBar.getProgress()))
                            .put("taskId", taskId);
                    ws.addListener(responder, taskId);

                    ws.send(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        playBtn.setOnClickListener(v -> {
            int taskId = Functions.randInt();

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject()
                        .put("worker", "player")
                        .put("action", "play")
                        .put("taskId", taskId);
                ws.addListener(responder, taskId);

                ws.send(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        pauseBtn.setOnClickListener(v -> {
            int taskId = Functions.randInt();
            boolean smoothPause = sp.getBoolean("smoothPause", false);
            if (smoothPause) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject()
                            .put("worker", "player")
                            .put("action", "smooth_pause")
                            .put("taskId", taskId);
                    ws.addListener(responder, taskId);
                    ws.send(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject()
                            .put("worker", "player")
                            .put("action", "pause")
                            .put("taskId", taskId);
                    ws.addListener(responder, taskId);
                    ws.send(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }




        });

        nextBtn.setOnClickListener(v -> {
            int taskId = Functions.randInt();

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject()
                        .put("worker", "player")
                        .put("action", "next")
                        .put("taskId", taskId);
                ws.addListener(responder, taskId);
                ws.send(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }



    private void connect() {

        /* global listener */
        ws.addListener(new Responder() {
            @Override
            public void receive(String message) throws JsonProcessingException {
                try {
                    JSONObject obj = new JSONObject(message);
                    String pos = obj.getString("pos");
                    String length = obj.getString("length");
                    String seconds = obj.getString("seconds");
                    String titleText = obj.getString("title");

                    runOnUiThread(() -> {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            positionBar.setProgress(Integer.parseInt(pos), true);
                        }
                        title.setText(titleText);
                        positionTxt.setText(seconds);
                        lengthTxt.setText(length);

                    });
                }
                catch(JSONException se) {
                    try {
                        JSONObject obj = new JSONObject(message);

                        final ObjectMapper objectMapper = new ObjectMapper();
                        List<Item> songList = objectMapper.readValue(obj.get("queue").toString(), new TypeReference<List<Item>>() {
                        });

                        runOnUiThread(() -> mAdapter.setmList(songList));
                    } catch (JSONException ignored) {

                    }
                }
            }
        }, 100_000);

    }


    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Item> filteredList = new ArrayList<>();

        // running a for loop to prefiltered elements.
        for (Item item : mAdapter.getmList()) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getTitle().toLowerCase(Locale.ROOT).contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No data found...", Toast.LENGTH_SHORT).show();

        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            mAdapter.filterList(filteredList);

        }
    }


    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }
    @Override
    public void onResume() {
        super.onResume();
        ws.fetchPosition();
        ws.fetchQueue();
    }


}
