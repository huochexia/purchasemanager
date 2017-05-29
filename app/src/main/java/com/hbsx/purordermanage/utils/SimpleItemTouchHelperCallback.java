package com.hbsx.purordermanage.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/1/19 0019.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private onMoveAndSwipedListener swipedListener;
    public SimpleItemTouchHelperCallback(onMoveAndSwipedListener listener){
        swipedListener = listener;

    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //如果是ListView样式的RecyclerView
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            //不支持拖拽
            final int dragFlags = 0;
            //设置侧滑方向为从左到右和从右到左都可以
            final int swipeFlags = ItemTouchHelper.START;
            //将方向参数设置进去
            return makeMovementFlags(dragFlags,swipeFlags);
        }else{//如果是GridView样式的RecyclerView
            //设置拖拽方向为上下左右
            final int dragFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN|
                    ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
            //不支持侧滑
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags,swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        swipedListener.onItemDismiss(viewHolder.getAdapterPosition());
    }

    /**
     * 当用户拖拽完或者侧滑一个item时回调此方法
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        //当前状态不是idel（空闲）状态时，说明当前正在拖拽或者侧滑
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE){
            if(viewHolder instanceof onStateChangedListener){
                ((onStateChangedListener) viewHolder).onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 当用户拖拽完或者侧滑完一个item时回调此方法，用来清除施加在item上的一些状态
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if(viewHolder instanceof onStateChangedListener){
            ((onStateChangedListener) viewHolder).onItemClear();
        }

    }
}
