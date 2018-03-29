package com.example.shang.filemanager.music;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by yaojian on 2017/7/31.
 */

public class MyItemTouchHelper extends ItemTouchHelper.Callback {

    public interface CallBackListener{
        void remove(int adapterPosition);
    }

    private int position;
    private CallBackListener listener;

    public MyItemTouchHelper(int position){
        this.position = position;
    }

    public void setListener(CallBackListener listener) {
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int drawFlag = ItemTouchHelper.UP| ItemTouchHelper.DOWN;
        int swipeFlag ;
        if (position == 0){
           swipeFlag = ItemTouchHelper.RIGHT;
        }else{
            swipeFlag = ItemTouchHelper.LEFT;
        }
        return makeMovementFlags(drawFlag,swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener!=null){
            listener.remove(viewHolder.getAdapterPosition());
        }
    }
}
