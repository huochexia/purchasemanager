package com.hbsx.purordermanage.Supply.adapter;

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

import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 商品品种汇总适配器
 *  Created by Administrator on 2017/1/14 0014.
 */

public class SummationForVarietylAdapter extends RecyclerView.Adapter<SummationForVarietylAdapter.ViewHolder> {

    private Context mContext;
    private List<PurchaseOrder> mPurchaseOrders;



    /**
     * 通过构造方法获取数据源
     */

    public SummationForVarietylAdapter(List<PurchaseOrder> list) {
        this.mPurchaseOrders = list;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.summation_variety_list_item, parent, false);
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

        holder.mSerialNumber.setText(String.valueOf(position+1));
        holder.mName.setText(purchaseOrder.getCommodityName());
        holder.mUnit.setText(purchaseOrder.getCommodityUnit());
        holder.mQuantity.setText(purchaseOrder.getActualNum().toString());
        holder.mSummation.setText(String.valueOf(purchaseOrder.getSum()));
        //计算平均单价
        Float price = purchaseOrder.getSum()/purchaseOrder.getActualNum();

        holder.mPrice.setText(price+"");

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
     * 通过内部类ViewHolder获得视图控件对象,
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mSerialNumber,mName, mUnit;
        TextView mQuantity,mPrice,mSummation;

        public ViewHolder(View itemView) {
            super(itemView);
            mSerialNumber = (TextView) itemView.findViewById(R.id.serial_number);
            mName = (TextView) itemView.findViewById(R.id.product_name);
            mUnit = (TextView) itemView.findViewById(R.id.product_unit);
            mQuantity = (TextView) itemView.findViewById(R.id.product_quantity);
            mPrice = (TextView) itemView.findViewById(R.id.product_price);
            mSummation = (TextView) itemView.findViewById(R.id.product_price_total);


        }

    }
}
