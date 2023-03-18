package dev.pamparampam.myapplication.radiowezel;



import static dev.pamparampam.myapplication.radiowezel.cookiebar2.utils.Functions.randInt;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.pamparampam.myapplication.R;
import dev.pamparampam.myapplication.radiowezel.cookiebar2.CookieBar;
import dev.pamparampam.myapplication.radiowezel.network.NetworkManager;
import dev.pamparampam.myapplication.radiowezel.network.Responder;
import dev.pamparampam.myapplication.radiowezel.network.WebSocket;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    private StartDragListener mStartDragListener;
    private List<Item> mList;
    private WebSocket ws;
    private Activity activity;
    private Context context;
    public SongListAdapter(Context ct, StartDragListener startDragListener, List<Item> mList, Activity activity) {

        this.mStartDragListener = startDragListener;
        this.mList = mList;
        this.context = ct;
        this.activity = activity;

    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ah_my_row, parent, false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.musicTitle.setText(mList.get(position).getTitle());
        holder.musicDesc.setText(mList.get(position).getAuthor());
        Picasso.get().load(mList.get(position).getThumbnail()).resize(800,480).centerCrop().into(holder.musicThumbnail);

        holder.handImage.setOnLongClickListener(v -> {
            mStartDragListener.requestDrag(holder);
            return true;
        });

    }
    public void filterList(ArrayList<Item> filterList) {
        // below line is to add our filtered
        // list in our course array list.
        mList = filterList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        else return mList.size();
    }

    @Override
    public void onRowSwiped(int position) {
        // we will delete and also we want to undo
        String id = mList.get(position).getId();


        // backup of removed item for undo
        final Item deletedItem = mList.get(position);

        ws = WebSocket.getInstance(activity);

        int taskId = randInt();

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject()
                    .put("worker", "queue")
                    .put("action", "remove")
                    .put("taskId", taskId)
                    .put("extras", new JSONObject().put("videoId", id));

            Responder responder = new Responder(){
                @Override
                public void receive(String message) throws JSONException {
                    JSONObject obj = new JSONObject(message);
                    CookieBar.Builder cookieBar = CookieBar.build(activity).setDuration(3000).setCookiePosition(CookieBar.BOTTOM).setTitle(obj.get("info").toString());

                    switch(obj.get("status").toString()) {
                        case "success":
                            cookieBar.setBackgroundColor(R.color.successShine);
                            cookieBar.setIcon(R.drawable.ic_success_shine);
                            cookieBar.setAction("UNDO", () -> {
                                CookieBar.dismiss(activity);
                                restoreItem(deletedItem, position);
                            });
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
                    activity.runOnUiThread(cookieBar::show);
                }
            };
            ws.addListener(responder, taskId);
            ws.send(jsonObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRowDraw(Canvas c, RecyclerView recyclerView, MyViewHolder myViewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    }

    @Override
    public void onRowDrawOver(Canvas c, RecyclerView recyclerView, MyViewHolder myViewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    }

    public void removeItem(int position) {
        mList.remove(position);
        // this will update recyclerview means refresh it
        notifyItemRemoved(position);
    }

    public void restoreItem(Item item, int position) {
        ws = WebSocket.getInstance(activity);
        int taskId = randInt();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject()
                    .put("worker", "queue")
                    .put("action", "restore")
                    .put("taskId", taskId)
                    .put("extras", new JSONObject().put("videoId", item.getId()).put("position", position));
            Responder responder = new Responder() {
                @Override
                public void receive(String message) throws JSONException {
                    JSONObject obj = new JSONObject(message);
                    CookieBar.Builder cookieBar = CookieBar.build(activity).setDuration(1500).setCookiePosition(CookieBar.TOP).setTitle(obj.get("info").toString());
                    switch (obj.get("status").toString()) {
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
                    activity.runOnUiThread(cookieBar::show);
                }
            };
            ws.addListener(responder, taskId);
            ws.send(jsonObject);


        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public List<Item> getmList() {
        return mList;
    }

    public void setmList(List<Item> mList) {

        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {

                Collections.swap(mList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

        ws = WebSocket.getInstance(activity);

        int taskId = randInt();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject()
                    .put("worker", "queue")
                    .put("action", "move")
                    .put("taskId", taskId)
                    .put("extras", new JSONObject().put("starting_i", fromPosition).put("ending_i", toPosition));
            Responder responder = new Responder() {
                @Override
                public void receive(String message) throws JSONException {
                    JSONObject obj = new JSONObject(message);
                    CookieBar.Builder cookieBar = CookieBar.build(activity).setDuration(1500).setCookiePosition(CookieBar.TOP).setTitle(obj.get("info").toString());
                    switch (obj.get("status").toString()) {
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
                    activity.runOnUiThread(cookieBar::show);
                }
            };
            ws.addListener(responder, taskId);
            ws.send(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        myViewHolder.cardView.setCardBackgroundColor(Color.LTGRAY);

    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        myViewHolder.cardView.setCardBackgroundColor(Color.WHITE);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView musicTitle, musicDesc;
        ImageView musicThumbnail, handImage;
        View rowView;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rowView = itemView;
            cardView = itemView.findViewById(R.id.ah_MR_song_card_view);
            musicTitle = itemView.findViewById(R.id.ah_MR_music_titles);
            musicDesc = itemView.findViewById(R.id.ah_MR_music_desc);
            musicThumbnail = itemView.findViewById(R.id.ah_MR_music_thumbnail);
            handImage = itemView.findViewById(R.id.ah_MR_move_dots_image);

        }


    }
}

