package com.hbsx.purordermanage.Other.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbsx.purordermanage.Other.ProviderOrderActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.bean.User;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * Created by Administrator on 2017/2/11 0011.
 */

public class ProviderListAdapter extends RecyclerView.Adapter<ProviderListAdapter.ViewHolder> {
    Context mContext;
    List<User> mProviderList;
    String orderDate;//查询日期

    public ProviderListAdapter(List<User> list, String orderDate){
        mProviderList = list;
        this.orderDate = orderDate;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext=parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_search_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String provider = mProviderList.get(position).getUsername();
        holder.provider.setText(provider);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProviderOrderActivity.actionStart(mContext,orderDate,provider);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mProviderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView provider;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            provider = (TextView) cardView.findViewById(R.id.order_search_number);
        }
    }

}
