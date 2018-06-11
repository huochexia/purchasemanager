package com.hbsx.purordermanage.Request.adapter;

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
import android.widget.Toast;

import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.Commodity;
import com.hbsx.purordermanage.utils.onMoveAndSwipedListener;
import com.hbsx.purordermanage.utils.onStateChangedListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 报货明细表适配器，
 * Created by Administrator on 2017/1/14 0014.
 */

public class RequestNoteSelectAdapter extends RecyclerView.Adapter<RequestNoteSelectAdapter.ViewHolder>
        implements onMoveAndSwipedListener {

    private Context mContext;
    private List<Commodity> mCommodities;

    private Map<Integer, Boolean> map = new HashMap<>();


    /**
     * 通过构造方法获取数据源
     */

    public RequestNoteSelectAdapter(List<Commodity> list) {
        this.mCommodities = list;
        for (int i = 0; i < mCommodities.size(); i++) {
            map.put(i, false);
        }
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
        holder.mSerialNumber.setText(String.valueOf(position + 1));
        holder.mName.setText(commodity.getCommName());
        holder.mUnit.setText(commodity.getUnit().getUnitName());
        holder.mPriceet.setText(commodity.getPrice()+"");
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                map.put(position, isChecked);
            }
        });
        if (map.get(position) == null) {
            map.put(position, false);
        }
        holder.mCheckBox.setChecked(map.get(position));
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
     * 获得选择索引
     *
     * @return
     */
    public Map<Integer, Boolean> getMap() {
        return map;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

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
     * 通过内部类ViewHolder获得视图控件对象
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements onStateChangedListener {
        LinearLayout mContentLayout;
        TextView mSerialNumber,mName, mUnit;
        CheckBox mCheckBox;
        RelativeLayout mCheckBoxLayout;
        RelativeLayout mPriceLayout;
        EditText mPriceet;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentLayout = (LinearLayout) itemView.findViewById(R.id.commodity_item_content);
            mSerialNumber = (TextView) itemView.findViewById(R.id.commodity_item_content_serial_number);
            mName = (TextView) itemView.findViewById(R.id.commodity_item_content_name);
            mUnit = (TextView) itemView.findViewById(R.id.commodity_item_content_unit);

            mCheckBoxLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_select);
            mCheckBoxLayout.setVisibility(View.VISIBLE);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.commodity_item_content_select_tv);

            mPriceLayout = (RelativeLayout) itemView.findViewById(R.id.commodity_item_content_price);
            mPriceLayout.setVisibility(View.VISIBLE);
            mPriceet = (EditText) itemView.findViewById(R.id.commodity_item_content_price_tv);
            mPriceet.setEnabled(false);
            mPriceet.setBackground(null);
        }

        @Override
        public void onItemSelected() {
            //设置item的背景颜色为浅灰色
            mName.setBackgroundColor(Color.RED);
            mUnit.setBackgroundColor(Color.RED);
        }

        @Override
        public void onItemClear() {
            mName.setBackgroundColor(0);
            mUnit.setBackgroundColor(0);
        }
    }
}
