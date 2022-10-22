package dev.pamparampam.myapplication.radiowezel;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

import dev.pamparampam.myapplication.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {


    private StartDragListener mStartDragListener;

    private RelativeLayout layout;

    private List<Item> mList;

    private Context context;


    public RecyclerViewAdapter(Context ct, StartDragListener startDragListener, RelativeLayout layout, List<Item> mList) {

        this.mStartDragListener = startDragListener;
        this.mList = mList;
        this.context = ct;
        this.layout = layout;
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.musicTitle.setText(mList.get(position).getTitle());
        holder.musicDesc.setText(mList.get(position).getDescription());
        holder.musicThumbnail.setImageResource(mList.get(position).getThumbnail());

        holder.handImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mStartDragListener.requestDrag(holder);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        else return mList.size();
    }

    @Override
    public void onRowSwiped(int position) {
        // we will delete and also we want to undo
        String name = mList.get(position).getTitle();

        // backup of removed item for undo
        final Item deletedItem = mList.get(position);

        // remove the item from recyclerview
        removeItem(position);
        // showing snack-bar for undo
        Snackbar snackbar = Snackbar.make(layout, name + " Removed!", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreItem(deletedItem, position);
            }
        });
        snackbar.setActionTextColor(Color.GREEN);
        snackbar.show();

    }

    @Override
    public void onRowDraw(Canvas c, RecyclerView recyclerView, MyViewHolder myViewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        myViewHolder.rowView.setBackgroundResource(R.drawable.ic_launcher_background);
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
        mList.add(position, item);
        notifyItemInserted(position);
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
    }


    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        myViewHolder.cardView.setCardBackgroundColor(Color.LTGRAY);

    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        myViewHolder.cardView.setCardBackgroundColor(Color.WHITE);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView musicTitle, musicDesc;
        ImageView musicThumbnail, handImage;
        View rowView;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rowView = itemView;
            cardView = itemView.findViewById(R.id.card_view);
            musicTitle = itemView.findViewById(R.id.music_titles);
            musicDesc = itemView.findViewById(R.id.music_desc);
            musicThumbnail = itemView.findViewById(R.id.music_thumbnail);
            handImage = itemView.findViewById(R.id.move_dots_image);

        }


    }
}

