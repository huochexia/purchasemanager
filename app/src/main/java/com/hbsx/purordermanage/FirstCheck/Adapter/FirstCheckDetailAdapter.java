package com.hbsx.purordermanage.FirstCheck.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbsx.purordermanage.POManageApplication;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.RequestNote;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/2/13 0013.
 */

public class FirstCheckDetailAdapter extends RecyclerView.Adapter<FirstCheckDetailAdapter.ViewHolder> {
    Context mContext;
    List<RequestNote> mRequestNoteList;

    public FirstCheckDetailAdapter(List<RequestNote> list) {
        mRequestNoteList = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mContentLayout;
        TextView serialNumber,commodityName, commodityUnit;
        EditText purchaseNum;
        RelativeLayout dingliangLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentLayout = (LinearLayout) itemView.findViewById(R.id.commodity_item_content);
            dingliangLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_dingliang);
            dingliangLayout.setVisibility(View.VISIBLE);
            serialNumber = (TextView) itemView.findViewById(R.id.commodity_item_content_serial_number);
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
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final RequestNote item = mRequestNoteList.get(position);
        holder.serialNumber.setText(String.valueOf(position+1));
        holder.commodityName.setText(item.getCommodityName());
        holder.commodityUnit.setText(item.getUnit());
        holder.purchaseNum.setTag(item);
        holder.purchaseNum.setEnabled(true);
        holder.purchaseNum.setText(item.getPurchaseNum() + "");
        if(item.getOrderState() == 1){
            holder.purchaseNum.setTextColor(Color.RED);
        }
        holder.purchaseNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                RequestNote bean = (RequestNote) holder.purchaseNum.getTag();
                if (TextUtils.isEmpty(s)) {//如果s为空，则Float.pareseFloat(s+"")会产生异常
                    holder.purchaseNum.setText("0.0");
                } else {
                    //将内容赋值给当前item，因为当该item滑出屏幕后再滑回时系统会重置，所以为了
                    //防止该item被重置为改变前状态，将其临时保存
                    bean.setPurchaseNum(Float.parseFloat(s + ""));
                    //同时将变化后的bean保存在本地数据库中
                    // 存入数据库
                    final String id = bean.getObjectId();
                    bean.update(id, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e != null) {
                                Toast.makeText(POManageApplication.getContext(), "录入数量失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (position % 2 == 0) {
            holder.mContentLayout.setBackgroundColor(Color.parseColor("#FF60C1F4"));
        }
    }


    @Override
    public int getItemCount() {
        return mRequestNoteList.size();
    }
}
