package com.hbsx.purordermanage.Request.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.hbsx.purordermanage.Request.ShoppingCartActivity;
import com.hbsx.purordermanage.Request.TotalEvent;
import com.hbsx.purordermanage.bean.Commodity;
import com.hbsx.purordermanage.bean.ShoppingCart;
import com.hbsx.purordermanage.utils.onMoveAndSwipedListener;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobObject;

/**
 * 通用适配器，用于购物车，供货，验货，录入等功能
 * Created by Administrator on 2017/2/3 0003.
 */

public class ShoppingCartListAdapter extends RecyclerView.Adapter<ShoppingCartListAdapter.ViewHolder>
        implements onMoveAndSwipedListener {

    List<ShoppingCart> mGoodsList;
    Context mContext;
    Map<Integer, Commodity> index = new HashMap<>();
    TotalEvent mEvent;


    public ShoppingCartListAdapter(List<ShoppingCart> list) {
        mGoodsList = list;
        mEvent = new TotalEvent();
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (position % 2 == 0) {
            holder.mContentLayout.setBackgroundColor(Color.parseColor("#FF60C1F4"));
        }

        holder.mSerialNumber.setText(String.valueOf(position+1));
        final ShoppingCart commodityItem =  mGoodsList.get(position);
        holder.mName.setText(commodityItem.getCommodityName());
        holder.mUnit.setText(commodityItem.getUnit().getUnitName());
        holder.mPurchasePrice.setText(commodityItem.getPurchasePrice()+"");
        holder.mPurchaseNum.setTag(commodityItem);
        holder.mPurchaseNum.setText(commodityItem.getPurchaseNum().toString());
        holder.mPurchaseNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ShoppingCart bean = (ShoppingCart) holder.mPurchaseNum.getTag();
                if (TextUtils.isEmpty(s)) {//如果s为空，则Float.pareseFloat(s+"")会产生异常
                    holder.mPurchaseNum.setText("0.0");
                } else {
                    //将内容赋值给当前item，因为当该item滑出屏幕后再滑回时系统会重置，所以为了
                    //防止该item被重置为改变前状态，将其临时保存
                    bean.setPurchaseNum(Float.parseFloat(s + ""));
                    //同时将变化后的bean保存在本地数据库中
//                    ShoppingCart good = new ShoppingCart();
//                    good.setPurchaseNum(bean.getPurchaseNum());
                    String objectId = bean.getObjectId();
                    bean.updateAll("objectId = ?", objectId);
                    //通过EventBus方式通知购物车主界面修改商品总价值
                    mEvent.total=computeTotal();
                    EventBus.getDefault().post(mEvent);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return mGoodsList.size();
    }

    /**
     * 这里处理item的移动功能
     *
     * @param fromPosition
     * @param toPosition
     * @return
     */
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    /**
     * 定义一个接口，用来修改主界面的工具栏子标题
     */
    public interface setToolbarSubTitleListener{
        void setToolbarSubtitle();
    }

    @Override
    public void onItemDismiss(int position) {
        //从本地数据库中删除
        String objectId = mGoodsList.get(position).getObjectId();
        DataSupport.deleteAll(ShoppingCart.class, "objectId = ?", objectId);
        //从当前列表中删除
        mGoodsList.remove(position);
        notifyItemRemoved(position);

    }

    /**
     * 计算购物车中所有商品总价值
     */
    public Float computeTotal() {
        Float total = 0.0f;
        for (ShoppingCart sc : mGoodsList) {
            total =total+ sc.getPurchaseNum()*sc.getPurchasePrice();
        }
        return total;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mUnit,mSerialNumber;
        RelativeLayout mPurchaseNumLayout;
        EditText mPurchaseNum;
        LinearLayout mContentLayout;
        RelativeLayout mPurchasePriceLayout;
        EditText mPurchasePrice;
        public ViewHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        /**
         * 初始化视图控件，根据不同的功能，创建不同的控件对象
         *
         * @param view
         */
        private void initView(View view) {
            mContentLayout = (LinearLayout) view.findViewById(R.id.commodity_item_content);
            mSerialNumber = (TextView) view.findViewById(R.id.commodity_item_content_serial_number);
            mName = (TextView) view.findViewById(R.id.commodity_item_content_name);
            mUnit = (TextView) view.findViewById(R.id.commodity_item_content_unit);
            mPurchaseNumLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_content_dingliang);
            mPurchaseNumLayout.setVisibility(View.VISIBLE);
            mPurchaseNum = (EditText) view.findViewById(R.id.commodity_item_content_dingliang_tv);
            mPurchaseNum.setEnabled(true);
            mPurchasePriceLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_content_price);
            mPurchasePriceLayout.setVisibility(View.VISIBLE);
            mPurchasePrice = (EditText) view.findViewById(R.id.commodity_item_content_price_tv);
            mPurchasePrice.setEnabled(false);
            mPurchasePrice.setBackground(null);
        }
    }

}
