package dev.pamparampam.myapplication.radiowezel;


import android.graphics.Canvas;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract mAdapter;

    public ItemMoveCallback(ItemTouchHelperContract adapter) {
        this.mAdapter = adapter;

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (viewHolder instanceof SongListAdapter.MyViewHolder) {
            SongListAdapter.MyViewHolder myViewHolder = (SongListAdapter.MyViewHolder) viewHolder;
            mAdapter.onRowDraw(c, recyclerView, myViewHolder, dX, dY, actionState, isCurrentlyActive);

        }


    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (viewHolder instanceof SongListAdapter.MyViewHolder) {
            SongListAdapter.MyViewHolder myViewHolder = (SongListAdapter.MyViewHolder) viewHolder;
            mAdapter.onRowDrawOver(c, recyclerView, myViewHolder, dX, dY, actionState, isCurrentlyActive);


        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }


    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        mAdapter.onRowSwiped(viewHolder.getAdapterPosition());

    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        super.onSelectedChanged(viewHolder, actionState);

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof SongListAdapter.MyViewHolder) {
                SongListAdapter.MyViewHolder myViewHolder = (SongListAdapter.MyViewHolder) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof SongListAdapter.MyViewHolder) {
            SongListAdapter.MyViewHolder myViewHolder = (SongListAdapter.MyViewHolder) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }
        Toast.makeText(recyclerView.getContext(), "Item dropped on position: " + viewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();

    }

    public interface ItemTouchHelperContract {
        void onRowMoved(int fromPosition, int toPosition);

        void onRowSelected(SongListAdapter.MyViewHolder myViewHolder);

        void onRowClear(SongListAdapter.MyViewHolder myViewHolder);

        void onRowSwiped(int position);

        void onRowDraw(Canvas c, RecyclerView recyclerView, SongListAdapter.MyViewHolder myViewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive);

        void onRowDrawOver(Canvas c, RecyclerView recyclerView, SongListAdapter.MyViewHolder myViewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive);

    }

}
