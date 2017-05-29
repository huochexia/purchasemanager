package com.hbsx.purordermanage.Examine.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbsx.purordermanage.Examine.ProviderOrderActivity;
import com.hbsx.purordermanage.Examine.ProviderOrderFragment;
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
        //增加角标，显示订货单数量（未验货数量）
        final BadgeView badgeView = new BadgeView(mContext, holder.provider);
        holder.provider.setTag(badgeView);
        badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);// 设置在右上角
        badgeView.setTextSize(14);// 设置文本大小
        badgeView.setTextColor(Color.GREEN);
        BmobQuery<PurchaseOrder> query = new BmobQuery<>();
        String bql = "select count(*) from PurchaseOrder where providername = ? and orderDate = ? and orderState = 1";
        query.setSQL(bql);
        query.setPreparedParams(new Object[]{provider, orderDate});
        query.doSQLQuery(new SQLQueryListener<PurchaseOrder>() {
            @Override
            public void done(BmobQueryResult<PurchaseOrder> bmobQueryResult, BmobException e) {
                if (e == null) {
                    if (bmobQueryResult.getCount() != 0) {
                        badgeView.setText(bmobQueryResult.getCount() + "");
                        badgeView.show();
                    }
                }
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
