package com.hbsx.purordermanage.Supply.adapter;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 商品明细表适配器，实现自定义的onMoveAndSwipedListener接口，在实现的方法中具体处理item侧滑删除功能
 * Created by Administrator on 2017/1/14 0014.
 */

public class SupplyPriceDetailAdapter extends RecyclerView.Adapter<SupplyPriceDetailAdapter.ViewHolder> {

    private Context mContext;
    private List<PurchaseOrder> mPurchaseOrders;
    private int state;
    private SharedPreferences mPreferences;
//    private SharedPreferences.Editor mEditor;

    /**
     * 通过构造方法获取数据源
     */

    public SupplyPriceDetailAdapter(List<PurchaseOrder> list, int state) {
        this.mPurchaseOrders = list;
        this.state = state;

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
        mPreferences = mContext.getSharedPreferences("HistoricalPrice", Context.MODE_PRIVATE);
//        mEditor = mPreferences.edit();
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
        holder.mPrice.setTag(purchaseOrder);

        holder.mSerialNumber.setText(String.valueOf(position + 1));
        holder.mName.setText(purchaseOrder.getCommodityName());
        holder.mUnit.setText(purchaseOrder.getCommodityUnit());
        holder.mPriceLayout.setVisibility(View.VISIBLE);
        holder.mPrice.setEnabled(true);
        Float price =purchaseOrder.getPrice();
        if (price == 0) {//如果从数据库中的商品价格为0，则从本地获取历史价格
            price=getHistoricalPrice(purchaseOrder.getCommodityName());
        }
        holder.mPrice.setText(price.toString());
        holder.mPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //得到对应的item
                PurchaseOrder order = (PurchaseOrder) holder.mPrice.getTag();
                Float price =getHistoricalPrice(order.getCommodityName());
                //如果历史价格发生变化，用蓝色字提示用户这个价格在改变。
                if (Float.valueOf(s.toString()) != price) {
                    holder.mPrice.setTextColor(Color.BLUE);
                }
                if (TextUtils.isEmpty(s)) {
                    holder.mPrice.setText("0.0");
                } else {
                    //为了防止item被系统还原，所以将变化也要保存在原始item中
                    order.setPrice(Float.parseFloat(s + ""));
                    //将变化保存在数据库中,因为网络的不稳定，所以只要变化就保存到数据库中。
                    final String id = order.getObjectId();
                    order.setPrice(Float.parseFloat(order.getPrice().toString()));
                    order.update(id, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }
                    });
                }
                if (price != 0) {
                    //新价格增涨30%以上，或者小于10%，说明价格变化大，有可是录入错误，要提醒用户注意
                    boolean greater =Float.valueOf(s.toString())>price*1.3 ;
                    boolean less = Float.valueOf(s.toString())*10<=price ;
                    if (greater || less) {
                        Toast.makeText(mContext,"价格变化较大，请认真核对！",Toast.LENGTH_SHORT).show();
                        holder.mPrice.setTextColor(Color.RED);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        switch (state) {
            case 1://已分配
                holder.mPurchaseNumLayout.setVisibility(View.VISIBLE);
                holder.mPurchaseNum.setText(purchaseOrder.getPurchaseNum().toString());
                holder.mPurchaseNum.setEnabled(false);
                holder.mPurchaseNum.setBackground(null);
                break;
            case 2:
            case 3:
                holder.mActualNumLayout.setVisibility(View.VISIBLE);
                holder.mActualNum.setText(purchaseOrder.getActualNum().toString());
                holder.mActualNum.setEnabled(false);
                holder.mActualNum.setBackground(null);
                holder.mPrice.setEnabled(false);
                holder.mPrice.setBackground(null);
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
     * 从本地获取历史价格数据
     */
    private Float getHistoricalPrice(String commodityName) {
        Float price = mPreferences.getFloat(commodityName, 0.0f);
        return price;
    }

    /**
     * 通过内部类ViewHolder获得视图控件对象,实现onStateChangeListener 接口，用于改变item被选中时的状态
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mContentLayout;
        TextView mSerialNumber, mName, mUnit;
        RelativeLayout mActualNumLayout, mPurchaseNumLayout, mPriceLayout;
        EditText mActualNum, mPurchaseNum, mPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentLayout = (LinearLayout) itemView.findViewById(R.id.commodity_item_content);
            mSerialNumber = (TextView) itemView.findViewById(R.id.commodity_item_content_serial_number);
            mName = (TextView) itemView.findViewById(R.id.commodity_item_content_name);
            mUnit = (TextView) itemView.findViewById(R.id.commodity_item_content_unit);

            mPriceLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_price);
            mPrice = (EditText) itemView.findViewById(R.id.commodity_item_content_price_tv);

            switch (state) {
                case 1:
                    mPurchaseNumLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_dingliang);
                    mPurchaseNum = (EditText) itemView.findViewById(R.id.commodity_item_content_dingliang_tv);
                    break;
                case 2:
                case 3:
                    mActualNumLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_actualnum);
                    mActualNum = (EditText) itemView.findViewById(R.id.commodity_item_content_actualnum_tv);
                    break;
            }
        }

    }
}
