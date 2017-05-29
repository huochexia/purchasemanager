package com.hbsx.purordermanage.Request.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.RequestNote;

import java.util.List;

/**
 * Created by Administrator on 2017/2/13 0013.
 */

public class RequestNoteDetailAdapter extends RecyclerView.Adapter<RequestNoteDetailAdapter.ViewHolder> {
    Context mContext;
    List<RequestNote> mRequestNoteList;

    public RequestNoteDetailAdapter(List<RequestNote> list) {
        mRequestNoteList = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mContentLayout;
        TextView commodityName, commodityUnit,mSerialNumber;
        EditText purchaseNum;
        RelativeLayout dingliangLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentLayout = (LinearLayout) itemView.findViewById(R.id.commodity_item_content);
            dingliangLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_dingliang);
            dingliangLayout.setVisibility(View.VISIBLE);

            mSerialNumber = (TextView) itemView.findViewById(R.id.commodity_item_content_serial_number);
            commodityName = (TextView) itemView.findViewById(R.id.commodity_item_content_name);
            commodityUnit = (TextView) itemView.findViewById(R.id.commodity_item_content_unit);
            purchaseNum = (EditText) itemView.findViewById(R.id.commodity_item_content_dingliang_tv);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.commodity_item_content, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        RequestNote item = mRequestNoteList.get(position);
        holder.mSerialNumber.setText(String.valueOf(position+1));
        holder.commodityName.setText(item.getCommodityName());
        holder.commodityUnit.setText(item.getUnit());
        holder.purchaseNum.setText(item.getPurchaseNum() + "");
        holder.purchaseNum.setEnabled(false);
        holder.purchaseNum.setBackground(null);
        if (position % 2 == 0) {
            holder.mContentLayout.setBackgroundColor(Color.parseColor("#FF60C1F4"));
        }
    }


    @Override
    public int getItemCount() {
        return mRequestNoteList.size();
    }
}
