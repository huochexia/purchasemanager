package com.hbsx.purordermanage.Manager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.Commodity;
import com.hbsx.purordermanage.utils.onMoveAndSwipedListener;
import com.hbsx.purordermanage.utils.onStateChangedListener;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 商品明细表适配器，实现自定义的onMoveAndSwipedListener接口，在实现的方法中具体处理item侧滑删除功能
 * Created by Administrator on 2017/1/14 0014.
 */

public class CommodityDetailAdapter extends RecyclerView.Adapter<CommodityDetailAdapter.ViewHolder>
        implements onMoveAndSwipedListener {

    private Context mContext;
    private List<Commodity> mCommodities;

    /**
     * 通过构造方法获取数据源
     */

    public CommodityDetailAdapter(List<Commodity> list) {
        this.mCommodities = list;

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
        final Commodity commodity = mCommodities.get(position);
            if (position % 2 == 0) {
                holder.mContentLayout.setBackgroundColor(Color.parseColor("#FF60C1F4"));
            }
            holder.mSerialNumber.setText(String.valueOf(position+1));
            holder.mName.setText(commodity.getCommName());
            holder.mUnit.setText(commodity.getUnit().getUnitName());
    }

    /**
     * 返回数据源数据的数量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mCommodities.size();
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
    public void onItemDismiss(int position) {

        String id = mCommodities.get(position).getObjectId();
        Commodity c = new Commodity();
        c.setObjectId(id);
        c.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "删除失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //删除模型中数据
        mCommodities.remove(position);
        //删除RecyclerView列表对应item
        notifyItemRemoved(position);

    }

    /**
     * 通过内部类ViewHolder获得视图控件对象,实现onStateChangeListener 接口，用于改变item被选中时的状态
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements onStateChangedListener {
        LinearLayout mContentLayout;
        TextView  mName, mUnit,mSerialNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentLayout = (LinearLayout) itemView.findViewById(R.id.commodity_item_content);
            mName = (TextView) itemView.findViewById(R.id.commodity_item_content_name);
            mUnit = (TextView) itemView.findViewById(R.id.commodity_item_content_unit);
            mSerialNumber= (TextView) itemView.findViewById(R.id.commodity_item_content_serial_number);
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
