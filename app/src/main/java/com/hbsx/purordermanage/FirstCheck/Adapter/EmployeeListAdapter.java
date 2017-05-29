package com.hbsx.purordermanage.FirstCheck.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbsx.purordermanage.FirstCheck.FirstCheckDetailActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.bean.User;
import com.hbsx.purordermanage.utils.Utility;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * Created by Administrator on 2017/2/11 0011.
 */

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.ViewHolder> {
    Context mContext;
    List<User> mEmployeeList;

    public EmployeeListAdapter(List<User> list){
        mEmployeeList = list;

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
        final User provider = mEmployeeList.get(position);
        holder.employee.setText(provider.getUsername());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstCheckDetailActivity.actionStart(mContext,provider.getUsername());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mEmployeeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView employee;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            employee = (TextView) cardView.findViewById(R.id.order_search_number);
        }
    }

}
