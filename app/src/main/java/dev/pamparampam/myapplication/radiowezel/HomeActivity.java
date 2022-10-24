package dev.pamparampam.myapplication.radiowezel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.helper.Functions;


public class HomeActivity extends AppCompatActivity implements StartDragListener {
    private static final String TAG = HomeActivity.class.getSimpleName();

    private FloatingActionButton settingsBtn, searchBtn;
    private ImageButton microphoneBtn, playBtn, nextBtn;
    private RecyclerViewAdapter mAdapter;
    private ItemTouchHelper touchHelper;
    private HashMap<String, String> user = new HashMap<>();
    private RecyclerView recyclerView;
    private MediaRecorder mediaRecorder;
    private List<Item> list;

    private AudioRecord audioRecord;
    private AudioTrack audioTrack;

    private int intBufferSize;
    private short[] shortAudioData;

    private int intGain;
    private boolean isRecording = false;


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
                filter("");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                filter("");
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

        microphoneBtn = findViewById(R.id.microphone);
        playBtn = findViewById(R.id.play);
        nextBtn = findViewById(R.id.next);


        settingsBtn = findViewById(R.id.settings_btn);
        searchBtn = findViewById(R.id.search_btn);



        recyclerView = findViewById(R.id.recycler_view);

        System.out.println("BUILDING RECYCLER VIEW AGAIN FROM SCRATCH!");
        buildRecyclerView();


        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();


    }
    @Override
    public void onBackPressed() {
    }

    private void buildRecyclerView() {


        RelativeLayout layout = findViewById(R.id.RLM);
        System.out.println(list);
        if(list == null){

            String[] titles = getResources().getStringArray(R.array.music_titles);
            String[] descriptions = getResources().getStringArray(R.array.music_desc);
            int[] image = new int[]{R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};

            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            list = new ArrayList<>();
            for (int i = 0; i < titles.length; i++) {
                list.add(new Item(image[i], titles[i], descriptions[i]));
            }
        }

        mAdapter = new RecyclerViewAdapter(this, this, layout, list);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(mAdapter);

        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @SuppressLint("MissingPermission")
    public void buttonStart(View view) {
        isRecording = true;
        int intRecordSampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);

        intBufferSize = AudioRecord.getMinBufferSize(intRecordSampleRate, AudioFormat.CHANNEL_IN_MONO
                , AudioFormat.ENCODING_PCM_16BIT);

        shortAudioData = new short[intBufferSize];

        if (isMicrophonePresent()) {
            getMicrophonePermission();
        }

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC
                , intRecordSampleRate
                , AudioFormat.CHANNEL_IN_STEREO
                , AudioFormat.ENCODING_PCM_16BIT
                , intBufferSize);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC
                , intRecordSampleRate
                , AudioFormat.CHANNEL_IN_STEREO
                , AudioFormat.ENCODING_PCM_16BIT
                , intBufferSize
                , AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackRate(intRecordSampleRate);
        audioRecord.startRecording();
        audioTrack.play();



        while (isRecording){
            audioRecord.read(shortAudioData, 0, shortAudioData.length);

            for (int i = 0; i< shortAudioData.length; i++){
                shortAudioData[i] = (short) Math.min (shortAudioData[i] * intGain, Short.MAX_VALUE);
            }
            audioTrack.write(shortAudioData, 0, shortAudioData.length);
        }
    }

    public void buttonStop(View view){

        isRecording = false;


    }
    private void init() {

        settingsBtn.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(i);
        });
        searchBtn.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(i);
        });

        microphoneBtn.setOnClickListener(v -> {
            if (isRecording) {
                buttonStop(microphoneBtn);

            }
            else {
                buttonStart(playBtn);

            }
        });


    }



    private void showDialog(String dialog) {
        Functions.showProgressDialog(HomeActivity.this, dialog);
    }

    private void hideDialog() {
        Functions.hideProgressDialog(HomeActivity.this);
    }




    private boolean isMicrophonePresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true;
        } else {
            return false;

        }
    }
    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Item> filteredList = new ArrayList<>();

        // running a for loop to prefiltered elements.
        for (Item item : list) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getTitle().toLowerCase(Locale.ROOT).contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            mAdapter.filterList(filteredList);

        }
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "test RecordingFile" + ".mp3");
        return file.getPath();
    }

    private void getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions (this, new String[]
                    {Manifest.permission.RECORD_AUDIO}, 200 );
        }
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }
}