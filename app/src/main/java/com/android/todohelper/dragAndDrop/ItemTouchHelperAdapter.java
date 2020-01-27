package com.android.todohelper.dragAndDrop;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onSwipeRight(int position);

    void onSwipeLeft(int position);

}