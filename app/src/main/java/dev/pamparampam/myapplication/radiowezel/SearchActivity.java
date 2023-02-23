package dev.pamparampam.myapplication.radiowezel;


import static android.widget.Toast.LENGTH_LONG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum;

import java.util.List;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.CookieBar;
import dev.pamparampam.myapplication.radiowezel.helper.Functions;
import dev.pamparampam.myapplication.radiowezel.helper.WebSocket;


public class SearchActivity extends AppCompatActivity {

    //EditText for input search keywords
    private EditText searchInput;
    private WebSocket ws;
    //YoutubeAdapter class that serves as a adapter for filling the 
    //RecyclerView by the CardView (video_item.xml) that is created in layout folder
    private YoutubeAdapter youtubeAdapter;

    //RecyclerView manages a long list by recycling the portion of view
    //that is currently visible on screen
    private RecyclerView mRecyclerView;

    //ProgressDialog can be shown while downloading data from the internet
    //which indicates that the query is being processed

    //Handler to run a thread which could fill the list after downloading data
    //from the internet and inflating the images, title and description
    private Handler handler;

    //results list of type VideoItem to store the results so that each item 
    //int the array list has id, title, description and thumbnail url
    private List<VideoItem> searchResults;


    //Overriding onCreate method(first method to be called) to create the activity 
    //and initialise all the variable to their respective views in layout file and 
    //adding listeners to required views
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //calling parent class to recall the app's last state
        super.onCreate(savedInstanceState);


        //method to fill the activity that is launched with  the activity_main.xml layout file
        setContentView(R.layout.activity_search);

        //initialising the objects with their respective view in activity_main.xml file
        searchInput = findViewById(R.id.AY_search_youtube_input);
        mRecyclerView = findViewById(R.id.AY_videos_recycler_view);

        //setting title and and style for progress dialog so that users can understand
        //what is happening currently

        NoInternetDialogPendulum.Builder builder = new NoInternetDialogPendulum.Builder(
                this,
                getLifecycle()
        );
        builder.build();
        //Fixing the size of recycler view which means that the size of the view
        //should not change if adapter or children size changes
        mRecyclerView.setHasFixedSize(true);
        //give RecyclerView a layout manager to set its orientation to vertical
        //by default it is vertical
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        handler = new Handler();

        //add listener to the EditText view which listens to changes that occurs when 
        //users changes the text or deletes the text
        //passing object of Textview EditorActionListener to this method
        //onEditorAction method called when user clicks ok button or any custom
        //button set on the bottom right of keyboard
        searchInput.setOnEditorActionListener((v, actionId, event) -> {

            //actionId of the respective action is returned as integer which can
            //be checked with our set custom search button in keyboard
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                //setting progress message so that users can understand what is happening
                showDialog("Finding videos for " + v.getText().toString());


                //calling our search method created below with input keyword entered by user
                //by getText method which returns Editable type, get string by toString method
                searchOnYoutube(v.getText().toString());

                //getting instance of the keyboard or any other input from which user types
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //hiding the keyboard once search button is clicked
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);

                return false;
            }
            return true;
        });

    }

    //custom search method which takes argument as the keyword for which videos is to be searched
    private void searchOnYoutube(final String keywords) {

        //A thread that will execute the searching and inflating the RecyclerView as and when
        //results are found
        new Thread() {

            //implementing run method
            public void run() {

                //create our YoutubeConnector class's object with Activity context as argument
                YoutubeConnector yc = new YoutubeConnector(SearchActivity.this);

                //calling the YoutubeConnector's search method by entered keyword 
                //and saving the results in list of type VideoItem class
                searchResults = yc.search(keywords);

                if (searchResults == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            CookieBar.build(SearchActivity.this)
                                    .setTitle("NETWORK ERROR")
                                    .setMessage("Looks like you don't have internet")
                                    .setDuration(5000)
                                    .setBackgroundColor(R.color.errorShine)
                                    .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                    .show();
                            hideDialog();
                        }
                    });

                } else {


                    //handler's method used for doing changes in the UI
                    //implementing run method of Runnable
                    handler.post(() -> {
                        if (searchResults.isEmpty()) {

                            CookieBar.build(SearchActivity.this)

                                    .setMessage("Couldn't find anything sorry")
                                    .setDuration(5000)
                                    .setIcon(R.drawable.ic_error_shine)
                                    .setBackgroundColor(R.color.errorShine)
                                    .setCookiePosition(CookieBar.TOP)
                                    .show();


                        }

                        //call method to create Adapter for RecyclerView and filling the list
                        //with thumbnail, title, id and description
                        fillYoutubeVideos();

                        //after the above has been done hiding the ProgressDialog
                        hideDialog();
                    });
                }
            }
            //starting the thread
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, LENGTH_LONG).show();

    }

    //method for creating adapter and setting it to recycler view
    private void fillYoutubeVideos() {
        ConstraintLayout layout = findViewById(R.id.CLM);
        //object of YoutubeAdapter which will fill the RecyclerView
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);

        youtubeAdapter = new YoutubeAdapter(layout, getApplicationContext(), searchResults, sp, SearchActivity.this);

        //setAdapter to RecyclerView
        mRecyclerView.setAdapter(youtubeAdapter);

        //notify the Adapter that the data has been downloaded so that list can be updated
        youtubeAdapter.notifyDataSetChanged();
    }

    private void showDialog(String dialog) {
        Functions.showProgressDialog(SearchActivity.this, dialog);
    }

    private void hideDialog() {
        Functions.hideProgressDialog(SearchActivity.this);
    }
}