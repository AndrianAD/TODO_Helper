package com.android.todohelper.dragAndDrop;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onSwipeRight(int position);

    void onSwipeLeft(int position, RecyclerView.ViewHolder viewHolder);

}