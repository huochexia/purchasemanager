package com.hbsx.purordermanage.InputData.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbsx.purordermanage.InputData.LookAndInputOrderFragment;
import com.hbsx.purordermanage.POManageApplication;
import com.hbsx.purordermanage.Purchase.SendToProviderActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 商品明细表适配器，实现自定义的onMoveAndSwipedListener接口，在实现的方法中具体处理item侧滑删除功能
 * Created by Administrator on 2017/1/14 0014.
 */

public class InputOrderAdapter extends RecyclerView.Adapter<InputOrderAdapter.ViewHolder> {

    private Context mContext;
    private List<PurchaseOrder> mPurchaseOrders;
    private int state;
    private SharedPreferences shared;
    Map<Integer, Boolean> select = new HashMap<>();


    /**
     * 通过构造方法获取数据源
     */

    public InputOrderAdapter(List<PurchaseOrder> list, int state, Context context) {
        this.mPurchaseOrders = list;
        this.state = state;
        mContext = context;
        shared = mContext.getSharedPreferences("oldPrice", Context.MODE_PRIVATE);
        RefreshMap();
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.commodity_item_content, parent, false);
        return new ViewHolder(view);
    }


    /**
     * 根据item位置创建商品实例，将数据源内容赋值给对应视图控件属性
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PurchaseOrder purchaseOrder = mPurchaseOrders.get(position);
        if (position % 2 == 0) {
            holder.mContentLayout.setBackgroundColor(Color.parseColor("#FF60C1F4"));
        }
        holder.mSerialNumber.setText(String.valueOf(position + 1));
        holder.mName.setText(purchaseOrder.getCommodityName());
        holder.mUnit.setText(purchaseOrder.getCommodityUnit());
        holder.mActualNumLayout.setVisibility(View.VISIBLE);
        holder.mActualNum.setEnabled(false);
        holder.mActualNum.setBackground(null);
        holder.mActualNum.setText(purchaseOrder.getActualAgain().toString());
        holder.mPriceLayout.setVisibility(View.VISIBLE);
        holder.mPrice.setEnabled(false);
        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "历史价格："+shared.getFloat(purchaseOrder.getCommodityName(), 0.0f)
                        , Toast.LENGTH_SHORT).show();
            }
        });
        holder.mPrice.setBackground(null);
        holder.mPrice.setText(purchaseOrder.getPrice().toString());
        float compate =shared.getFloat(purchaseOrder.getCommodityName(), 0.0f) - purchaseOrder.getPrice();
        if ( compate< 0) {
            holder.mPrice.setTextColor(Color.RED);
        } else if(compate>0){
            holder.mPrice.setTextColor(Color.BLUE);
        }
        switch (state) {
            case 3:
                holder.mSelectedLayout.setVisibility(View.VISIBLE);
                holder.mSelect.setTag(position);
                holder.mSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        select.put(position, isChecked);
                        if (isChecked) {

                            LookAndInputOrderFragment.addSelectedOrders(purchaseOrder);
                        } else {
                            LookAndInputOrderFragment.deleteSelectedOrders(purchaseOrder);
                        }
                    }
                });
                if (select.get(position) == null) {
                    select.put(position, false);
                }
                holder.mSelect.setChecked(select.get(position));
                break;
        }
    }

    /**
     * 返回数据源数据的数量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mPurchaseOrders.size();
    }

    /**
     * 还原选择
     *
     * @return
     */
    public void RefreshMap() {
        for (int i = 0; i < mPurchaseOrders.size(); i++) {
            select.put(i, false);
        }
    }

    /**
     * 通过内部类ViewHolder获得视图控件对象,实现onStateChangeListener 接口，用于改变item被选中时的状态
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mContentLayout;
        TextView mSerialNumber, mName, mUnit;
        RelativeLayout mActualNumLayout, mSelectedLayout, mPriceLayout;
        EditText mActualNum, mPrice;
        CheckBox mSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentLayout = (LinearLayout) itemView.findViewById(R.id.commodity_item_content);
            mSerialNumber = (TextView) itemView.findViewById(R.id.commodity_item_content_serial_number);
            mName = (TextView) itemView.findViewById(R.id.commodity_item_content_name);
            mUnit = (TextView) itemView.findViewById(R.id.commodity_item_content_unit);
            mActualNumLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_actualnum);
            mActualNum = (EditText) itemView.findViewById(R.id.commodity_item_content_actualnum_tv);
            mPriceLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_price);
            mPrice = (EditText) itemView.findViewById(R.id.commodity_item_content_price_tv);

            switch (state) {
                case 3:
                    mSelectedLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_select);

                    mSelect = (CheckBox) itemView.findViewById(R.id.commodity_item_content_select_tv);
                    break;

            }
        }

    }
}
