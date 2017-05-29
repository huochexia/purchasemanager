package com.hbsx.purordermanage.Purchase.adapter;

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
import android.widget.Toast;

import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.utils.onMoveAndSwipedListener;
import com.hbsx.purordermanage.utils.onStateChangedListener;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 商品明细表适配器，实现自定义的onMoveAndSwipedListener接口，在实现的方法中具体处理item侧滑删除功能
 * Created by Administrator on 2017/1/14 0014.
 */

public class PurchaseDetailAdapter extends RecyclerView.Adapter<PurchaseDetailAdapter.ViewHolder>
        implements onMoveAndSwipedListener {

    private Context mContext;
    private List<PurchaseOrder> mPurchaseOrders;

    /**
     * 通过构造方法获取数据源
     */

    public PurchaseDetailAdapter(List<PurchaseOrder> list) {
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
        holder.mSerialNumber.setText(String.valueOf(position+1));
        holder.mName.setText(purchaseOrder.getCommodityName());
        holder.mUnit.setText(purchaseOrder.getCommodityUnit());
        holder.mPurchaseNum.setText(purchaseOrder.getPurchaseNum().toString());
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
     * 这里处理item的删除功能
     *
     * @param position
     */
    @Override
    public void onItemDismiss(final int position) {
        if (mPurchaseOrders.get(position).getOrderState() < 2) {//只要订单没有验货或录入都可以重新分配
            PurchaseOrder c = mPurchaseOrders.get(position);
            String id = c.getObjectId();
            c.setOrderState(0);
            c.update(id, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e!=null){
                        Toast.makeText(mContext,"数据库异常"+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
            mPurchaseOrders.remove(position);
            notifyItemRemoved(position);
        }else {
            Toast.makeText(mContext,"该项已完成验货，不能再分配！",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 通过内部类ViewHolder获得视图控件对象,实现onStateChangeListener 接口，用于改变item被选中时的状态
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements onStateChangedListener {
        LinearLayout mContentLayout;
        TextView mSerialNumber,mName, mUnit;
        RelativeLayout mPurchaseLayout;
        EditText mPurchaseNum;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentLayout = (LinearLayout) itemView.findViewById(R.id.commodity_item_content);
            mSerialNumber = (TextView) itemView.findViewById(R.id.commodity_item_content_serial_number);
            mName = (TextView) itemView.findViewById(R.id.commodity_item_content_name);
            mUnit = (TextView) itemView.findViewById(R.id.commodity_item_content_unit);
            mPurchaseLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_dingliang);
            mPurchaseLayout.setVisibility(View.VISIBLE);
            mPurchaseNum = (EditText) itemView.findViewById(R.id.commodity_item_content_dingliang_tv);
            mPurchaseNum.setEnabled(false);
            mPurchaseNum.setBackground(null);
        }

        @Override
        public void onItemSelected() {
            //设置item的背景颜色为浅灰色
            mName.setBackgroundColor(Color.RED);
            mUnit.setBackgroundColor(Color.RED);

        }

        public void onItemClear() {
            mName.setBackgroundColor(0);
            mUnit.setBackgroundColor(0);

        }
    }
}
