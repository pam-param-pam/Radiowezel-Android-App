package dev.pamparampam.myapplication.radiowezel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.CookieBar;
import dev.pamparampam.myapplication.radiowezel.helper.Functions;
import dev.pamparampam.myapplication.radiowezel.helper.Responder;
import dev.pamparampam.myapplication.radiowezel.helper.WebSocket;


//Adapter class for RecyclerView of videos
public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.MyViewHolder>{

    private Context mContext;
    private List<VideoItem> mVideoList;
    private ConstraintLayout layout;
    private WebSocket ws;
    private SharedPreferences sp;
    private Activity activity;
    //Parameterised Constructor to save the Activity context and video list
    //helps in initializing a object for this class
    public YoutubeAdapter(ConstraintLayout layout, Context mContext, List<VideoItem> mVideoList, SharedPreferences sp, Activity activity) {
        this.mContext = mContext;
        this.mVideoList = mVideoList;
        this.layout = layout;
        this.sp = sp;
        this.activity = activity;
    }



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder



    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ay_video_item, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    //filling every item of view with respective text and image
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

         // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final VideoItem singleVideo = mVideoList.get(position);

        //replace the default text with id, title and description with setText method
        holder.video_id.setText("Video ID : "+singleVideo.getId());
        holder.video_title.setText(singleVideo.getTitle());
        holder.video_description.setText(singleVideo.getDescription());


        Responder responder = new Responder() {
            @Override
            public void receive(String message) throws JsonProcessingException, JSONException {

                JSONObject obj = new JSONObject(message);
                CookieBar.Builder cookieBar = CookieBar.build(activity).setDuration(1500).setCookiePosition(CookieBar.TOP).setTitle(obj.get("info").toString());
                switch(obj.get("status").toString()) {
                    case "success":
                        cookieBar.setBackgroundColor(R.color.successShine);
                        cookieBar.setIcon(R.drawable.ic_success_shine);
                        break;
                    case "warning":
                        cookieBar.setBackgroundColor(R.color.warningShine);
                        cookieBar.setIcon(R.drawable.ic_warning_shine);

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
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        cookieBar.show();
                    }
                });


        }};



        holder.add_new_video_btn.setOnClickListener(v -> {


            ws = new WebSocket(sp, activity,Constants.TEST_URL);
            int taskId = Functions.randInt();
            ws.addListener(responder, taskId);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject()
                        .put("worker", "queue")
                        .put("action", "add")
                        .put("taskId", taskId)
                        .put("extras", new JSONObject()
                                .put("videoId", singleVideo.getId()));

                ws.send(jsonObject);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        });


        Picasso.get().load(singleVideo.getThumbnailURL()).resize(480,270).centerCrop().into(holder.thumbnail);

        //setting on click listener for each video_item to launch clicked video in new activity
        holder.video_view.setOnClickListener(view -> {

            //creating a intent for PlayerActivity class from this Activity
            //arguments needed are package context and the new Activity class
            Intent intent = new Intent(mContext, PlayerActivity.class);

            //putExtra method helps to add extra/extended data to the intent
            //which can then be used by the new activity to get initial data from older activity
            //arguments is a name used to identify the data and other is the data itself
            intent.putExtra("VIDEO_ID", singleVideo.getId());
            intent.putExtra("VIDEO_TITLE",singleVideo.getTitle());
            intent.putExtra("VIDEO_DESC",singleVideo.getDescription());

            //Flags define how the activity should behave when launched
            //FLAG_ACTIVITY_NEW_TASK flag if set, the activity will become the start of a new task on this history stack.
            //adding flag as it is required for YoutubePlayerView Activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //launching the activity by startActivity method
            //use mContext as this class is not the original context
            mContext.startActivity(intent);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    //here the dataset is mVideoList
    @Override
    public int getItemCount() {

        return mVideoList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView thumbnail;
        public TextView video_title, video_id, video_description;
        public RelativeLayout video_view;
        public ImageButton add_new_video_btn;

        public MyViewHolder(View view) {

            super(view);

            //the video_item.xml file is now associated as view object
            //so the view can be called from view's object
            add_new_video_btn = view.findViewById(R.id.AP_add_new_video_btn);
            thumbnail = view.findViewById(R.id.AS_profile_thumbnail);
            video_title = view.findViewById(R.id.video_title);
            video_id = view.findViewById(R.id.video_id);
            video_description = view.findViewById(R.id.video_description);
            video_view = view.findViewById(R.id.video_view);
        }
    }

}