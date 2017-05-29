package com.hbsx.purordermanage.Request.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Request.RequestNoteDetailActivity;

import java.util.List;

/**
 * Created by Administrator on 2017/2/11 0011.
 */

public class RequestNoteSearchAdapter extends RecyclerView.Adapter<RequestNoteSearchAdapter.ViewHolder> {
    Context mContext;
    List<String> orderNumList;

    public RequestNoteSearchAdapter(List<String> list){
        orderNumList = list;
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
        final String orderNumber = orderNumList.get(position);
        holder.orderNumber.setText(orderNumber);
        holder.orderNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestNoteDetailActivity.actionStart(mContext,orderNumber);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderNumList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView orderNumber;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            orderNumber = (TextView) cardView.findViewById(R.id.order_search_number);
        }
    }

}
