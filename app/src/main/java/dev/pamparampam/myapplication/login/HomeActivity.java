package dev.pamparampam.myapplication.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.login.helper.Functions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



public class HomeActivity extends AppCompatActivity implements StartDragListener{
    private static final String TAG = HomeActivity.class.getSimpleName();

    private FloatingActionButton settingsBtn;
    private RecyclerViewAdapter mAdapter;
    private ItemTouchHelper touchHelper;
    private HashMap<String,String> user = new HashMap<>();
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        settingsBtn = findViewById(R.id.floating_settings_btn);


        recyclerView = findViewById(R.id.recycler_view);


        String[] s1 = getResources().getStringArray(R.array.music_titles);
        String[] s2 = getResources().getStringArray(R.array.music_desc);
        int[] images = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};



        mAdapter = new RecyclerViewAdapter(this,this, s1, s2, images);

        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(mAdapter);
        touchHelper  = new ItemTouchHelper(callback);
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