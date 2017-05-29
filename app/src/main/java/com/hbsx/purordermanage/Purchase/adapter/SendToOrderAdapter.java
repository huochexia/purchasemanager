package com.hbsx.purordermanage.Purchase.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbsx.purordermanage.Purchase.SendToProviderActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/3 0003.
 */

public class SendToOrderAdapter extends RecyclerView.Adapter<SendToOrderAdapter.ViewHolder> {

    final List<PurchaseOrder> commodityList;
    Context mContext;
    Map<Integer,Boolean> select = new HashMap<>();

    public SendToOrderAdapter(List<PurchaseOrder> list) {
        commodityList = list;
        RefreshMap();
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position % 2 == 0) {
            holder.mContentLayout.setBackgroundColor(Color.parseColor("#FF60C1F4"));
        }
        final PurchaseOrder purchaseOrder = commodityList.get(position);
        holder.dingliangLayout.setVisibility(View.VISIBLE);
        holder.selectedLayout.setVisibility(View.VISIBLE);
        holder.mSerialNumber.setText(String.valueOf(position+1));
        holder.mName.setText(purchaseOrder.getCommodityName());
        holder.mUnit.setText(purchaseOrder.getCommodityUnit());
        holder.mPurchaseNum.setText(purchaseOrder.getPurchaseNum() + "");
        holder.mPurchaseNum.setBackground(null);
        holder.mSelect.setTag(position);
        holder.mSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               select.put(position,isChecked);
                if (isChecked) {
                    SendToProviderActivity.addSelectedToOrderList(purchaseOrder);
                } else {
                    SendToProviderActivity.deleteSelectedToOrderList(purchaseOrder);
                }
            }
        });
        if(select.get(position)==null){
            select.put(position,false);
        }
        holder.mSelect.setChecked(select.get(position));
    }

    @Override
    public int getItemCount() {
        return commodityList.size();
    }
    /**
     * 还原选择
     * @return
     */
    public void RefreshMap(){
        for (int i = 0 ;i<commodityList.size();i++){
            select.put(i,false);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout dingliangLayout;
        RelativeLayout selectedLayout;
        TextView mSerialNumber,mName;
        TextView mUnit;
        EditText mPurchaseNum;
        CheckBox mSelect;
        LinearLayout mContentLayout;

        public ViewHolder(View view) {
            super(view);
            dingliangLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_content_dingliang);
            selectedLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_content_select);
            mContentLayout = (LinearLayout) view.findViewById(R.id.commodity_item_content);
            mSerialNumber = (TextView) view.findViewById(R.id.commodity_item_content_serial_number);
            mName = (TextView) view.findViewById(R.id.commodity_item_content_name);
            mUnit = (TextView) view.findViewById(R.id.commodity_item_content_unit);
            mPurchaseNum = (EditText) view.findViewById(R.id.commodity_item_content_dingliang_tv);
            mPurchaseNum.setEnabled(false);
            mSelect = (CheckBox) view.findViewById(R.id.commodity_item_content_select_tv);

        }
    }

}
