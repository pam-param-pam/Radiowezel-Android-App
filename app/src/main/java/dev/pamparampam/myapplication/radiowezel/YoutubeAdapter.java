package dev.pamparampam.myapplication.radiowezel;

import android.content.Context;
import android.content.Intent;


import android.graphics.Canvas;
import android.graphics.Color;
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

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import dev.pamparampam.myapplication.R;


//Adapter class for RecyclerView of videos
public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract{

    private Context mContext;
    private List<VideoItem> mVideoList;
    private ConstraintLayout layout;


    //Parameterised Constructor to save the Activity context and video list
    //helps in initializing a object for this class
    public YoutubeAdapter(ConstraintLayout layout, Context mContext, List<VideoItem> mVideoList) {
        this.mContext = mContext;
        this.mVideoList = mVideoList;
        this.layout = layout;
    }


    @Override
    public void onRowMoved(int fromPosition, int toPosition) {

    }

    @Override
    public void onRowSelected(RecyclerViewAdapter.MyViewHolder myViewHolder) {
        myViewHolder.cardView.setCardBackgroundColor(Color.LTGRAY);

    }

    @Override
    public void onRowClear(RecyclerViewAdapter.MyViewHolder myViewHolder) {
        myViewHolder.cardView.setCardBackgroundColor(Color.WHITE);

    }

    @Override
    public void onRowSwiped(int position) {

        String name = mVideoList.get(position).getTitle();

        // backup of removed item for undo
        final VideoItem deletedItem = mVideoList.get(position);

        // remove the item from recyclerview
        removeItem(position);
        // showing snack-bar for undo
        Snackbar snackbar = Snackbar.make(layout, name + " Removed!", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", v -> restoreItem(deletedItem, position));
        snackbar.setActionTextColor(Color.GREEN);
        snackbar.show();
    }

    @Override
    public void onRowDraw(Canvas c, RecyclerView recyclerView, RecyclerViewAdapter.MyViewHolder myViewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

    }

    @Override
    public void onRowDrawOver(Canvas c, RecyclerView recyclerView, RecyclerViewAdapter.MyViewHolder myViewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

    }
    public void removeItem(int position) {
        mVideoList.remove(position);
        // this will update recyclerview means refresh it
        notifyItemRemoved(position);
    }

    public void restoreItem(VideoItem videoItem, int position) {
        mVideoList.add(position, videoItem);
        notifyItemInserted(position);
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder



    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);

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
        holder.video_id.setText("Video ID : "+singleVideo.getId()+" ");
        holder.video_title.setText(singleVideo.getTitle());
        holder.video_description.setText(singleVideo.getDescription());

        holder.add_new_video_btn.setOnClickListener(v -> {
            // showing snack-bar for undo
             String title = StringUtils.abbreviate(singleVideo.getTitle(), 40) ;


            Snackbar snackbar = Snackbar.make(layout, title + " Added!", Snackbar.LENGTH_LONG);


            snackbar.show();
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

            //Flags define hot the activity should behave when launched
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
            add_new_video_btn = (ImageButton) view.findViewById(R.id.add_new_video_btn);
            thumbnail = (ImageView) view.findViewById(R.id.video_thumbnail);
            video_title = (TextView) view.findViewById(R.id.video_title);
            video_id = (TextView) view.findViewById(R.id.video_id);
            video_description = (TextView) view.findViewById(R.id.video_description);
            video_view = (RelativeLayout) view.findViewById(R.id.video_view);
        }
    }

}