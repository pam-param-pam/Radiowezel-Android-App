package dev.pamparampam.myapplication.login;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.pamparampam.myapplication.R;

import java.util.Arrays;
import java.util.Collections;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {


    StartDragListener mStartDragListener;
    String[] data1, data2;

    int[] img;
    ImageView handImage;
    Context context;


    public RecyclerViewAdapter(Context ct, StartDragListener startDragListener, String[] s1, String[] s2, int[] images) {

        mStartDragListener = startDragListener;
        data1 = s1;
        data2 = s2;
        context = ct;
        img = images;
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.musicTitle.setText(data1[position]);
        holder.musicDesc.setText(data2[position]);
        holder.musicThumbnail.setImageResource(img[position]);

        holder.handImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() ==
                        MotionEvent.ACTION_DOWN) {
                    mStartDragListener.requestDrag(holder);
                }
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return data1.length;
    }


    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(Arrays.asList(data1), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(Arrays.asList(data1), i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }






    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView musicTitle, musicDesc;
        ImageView musicThumbnail, handImage;
        View rowView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rowView = itemView;
            musicTitle = itemView.findViewById(R.id.music_titles);
            musicDesc = itemView.findViewById(R.id.music_desc);
            musicThumbnail = itemView.findViewById(R.id.music_thumbnail);
            handImage = itemView.findViewById(R.id.imageView);



        }
    }

}

