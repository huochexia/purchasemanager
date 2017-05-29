package com.hbsx.purordermanage.utils;

/**
 * 接口：当在Item上进行上下移动，或左右滑动时调用的方法
 * Created by Administrator on 2017/1/19 0019.
 */

public interface onMoveAndSwipedListener {

    boolean onItemMove(int fromPosition , int toPosition);
    void onItemDismiss(int position);
}
