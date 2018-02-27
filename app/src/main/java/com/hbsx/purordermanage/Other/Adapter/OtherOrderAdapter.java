package com.hbsx.purordermanage.Other.Adapter;

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

import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.utils.onMoveAndSwipedListener;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 商品明细表适配器，实现自定义的onMoveAndSwipedListener接口，在实现的方法中具体处理item侧滑删除功能
 * Created by Administrator on 2017/1/14 0014.
 */

public class OtherOrderAdapter extends RecyclerView.Adapter<OtherOrderAdapter.ViewHolder>
        implements onMoveAndSwipedListener {

    private Context mContext;
    private List<PurchaseOrder> mPurchaseOrders;
    private Integer mOrderState;

    /**
     * 通过构造方法获取数据源
     */

    public OtherOrderAdapter(List<PurchaseOrder> list, Integer state) {
        this.mPurchaseOrders = list;
        this.mOrderState = state;

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
        holder.mActualNum.setTag(purchaseOrder);

        holder.mSerialNumber.setText(String.valueOf(position+1));
        holder.mName.setText(purchaseOrder.getCommodityName());
        holder.mUnit.setText(purchaseOrder.getCommodityUnit());
        //价格
        holder.mPriceLayout.setVisibility(View.VISIBLE);
        holder.mPrice.setEnabled(true);
        holder.mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PurchaseOrder order = (PurchaseOrder) holder.mActualNum.getTag();
                if (TextUtils.isEmpty(s)) {
                    holder.mPrice.setText("0.0");
                } else {
                    //为了防止因滑动将EditText中内容还原为初始值，将改变存入该item中
                    order.setPrice(Float.parseFloat(s + ""));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.mPrice.setBackground(null);
        holder.mPrice.setText(purchaseOrder.getPrice().toString());
        //实数
        holder.mActualNumLayout.setVisibility(View.VISIBLE);
        holder.mActualNum.setText(purchaseOrder.getActualAgain().toString());
        if (purchaseOrder.getOrderState() < 4) {
            holder.mActualNum.setEnabled(true);
        }else{
            holder.mActualNum.setEnabled(false);
            holder.mActualNum.setBackground(null);
        }

        holder.mActualNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PurchaseOrder order = (PurchaseOrder) holder.mActualNum.getTag();
                if (TextUtils.isEmpty(s)) {
                    holder.mActualNum.setText("0.0");
                } else {
                    //为了防止因滑动将EditText中内容还原为初始值，将改变存入该item中
                    order.setActualAgain(Float.parseFloat(s + ""));
//                        // 存入数据库
//                        final String id = order.getObjectId();
//                        order.update(id, new UpdateListener() {
//                            @Override
//                            public void done(BmobException e) {
//
//                            }
//                        });

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    /**
     * @param position
     */
    @Override
    public void onItemDismiss(final int position) {
        if (mPurchaseOrders.get(position).getOrderState() == 3) {//只有为验货状态时才可以重新验货，尚未录入
            PurchaseOrder c = mPurchaseOrders.get(position);
            String id = c.getObjectId();

            c.setOrderState(1);
            c.update(id, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {

                    }
                }
            });
            mPurchaseOrders.remove(position);
            notifyItemRemoved(position);
        } else {
            Toast.makeText(mContext, "该项录入，不能删除！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 通过内部类ViewHolder获得视图控件对象,实现onStateChangeListener 接口，用于改变item被选中时的状态
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mContentLayout;
        TextView mSerialNumber,mName, mUnit;
        RelativeLayout mActualNumLayout, mPriceLayout;
        EditText mActualNum, mPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentLayout = (LinearLayout) itemView.findViewById(R.id.commodity_item_content);
            mSerialNumber = (TextView) itemView.findViewById(R.id.commodity_item_content_serial_number);
            mName = (TextView) itemView.findViewById(R.id.commodity_item_content_name);
            mUnit = (TextView) itemView.findViewById(R.id.commodity_item_content_unit);

            mPriceLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_price);
            mPrice = (EditText) itemView.findViewById(R.id.commodity_item_content_price_tv);


            mActualNumLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_actualnum);
            mActualNum = (EditText) itemView.findViewById(R.id.commodity_item_content_actualnum_tv);
        }
    }
}

