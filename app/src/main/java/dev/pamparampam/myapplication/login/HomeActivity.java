package dev.pamparampam.myapplication.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.login.helper.Functions;


public class HomeActivity extends AppCompatActivity implements StartDragListener {
    private static final String TAG = HomeActivity.class.getSimpleName();

    private FloatingActionButton settingsBtn;
    private RecyclerViewAdapter mAdapter;
    private ItemTouchHelper touchHelper;
    private HashMap<String, String> user = new HashMap<>();
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        settingsBtn = findViewById(R.id.floating_settings_btn);

        RelativeLayout layout = findViewById(R.id.RLM);
        recyclerView = findViewById(R.id.recycler_view);


        String[] titles = getResources().getStringArray(R.array.music_titles);
        String[] descriptions = getResources().getStringArray(R.array.music_desc);


        int[] image = new int[]{R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            list.add(new Item(image[i], titles[i], descriptions[i]));
        }
        mAdapter = new RecyclerViewAdapter(this, this, layout, list);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(mAdapter);

        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }


    private void init() {


        settingsBtn.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(i);
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void showDialog() {
        Functions.showProgressDialog(HomeActivity.this, "Please wait...");
    }

    private void hideDialog() {
        Functions.hideProgressDialog(HomeActivity.this);
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }
}