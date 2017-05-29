package com.hbsx.purordermanage.Purchase.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbsx.purordermanage.Purchase.PurchaseDetailActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.User;

import java.util.List;

/**
 * Created by Administrator on 2017/2/11 0011.
 */

public class ProviderSearchAdapter extends RecyclerView.Adapter<ProviderSearchAdapter.ViewHolder> {
    Context mContext;
    List<User> mProviderList;
    int periodflag;//查询期间标志

    public ProviderSearchAdapter(List<User> list,int period){
        mProviderList = list;
        periodflag = period;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User provider = mProviderList.get(position);
        holder.provider.setText(provider.getUsername());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseDetailActivity.actionStart(mContext,provider.getUsername(),periodflag);
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
