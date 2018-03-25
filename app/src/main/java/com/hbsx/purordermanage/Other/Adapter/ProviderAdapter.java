package com.hbsx.purordermanage.Other.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hbsx.purordermanage.bean.User;

import java.util.List;

/**
 * 供货商下拉列表
 * Created by Administrator on 2018/3/25 0025.
 */

public class ProviderAdapter extends BaseAdapter {
    private Context mContext;
    private List<User> list;
    public ProviderAdapter(Context context, List<User> list){
        mContext = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LinearLayout ll = new LinearLayout( mContext );
        ll.setOrientation(LinearLayout. HORIZONTAL );
        ll.setGravity(Gravity. CENTER_HORIZONTAL );
        TextView tv = new TextView( mContext );
        tv.setText(list.get(position).getUsername());
        tv.setTextSize(18);
        tv.setTextColor(Color.BLACK );
        ll.addView(tv);
        return ll;
    }
}
