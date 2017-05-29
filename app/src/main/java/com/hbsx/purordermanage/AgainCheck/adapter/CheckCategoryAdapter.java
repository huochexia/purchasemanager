package com.hbsx.purordermanage.AgainCheck.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbsx.purordermanage.AgainCheck.CheckRequestNoteActivity;
import com.hbsx.purordermanage.Manager.CommodityDetailActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Request.RequestNoteSelectActivity;
import com.hbsx.purordermanage.bean.CommodityCategory;

import java.util.List;

/**
 * 商品分类列表适配器
 * Created by Administrator on 2017/1/14 0014.
 */

public class CheckCategoryAdapter extends RecyclerView.Adapter<CheckCategoryAdapter.ViewHolder> {
    public static final int COMMODITY_MANAGER = 1;
    public static final int COMMODITY_BAOHUO = 2;
    private Context mContext;
    private List<CommodityCategory> mCommodityCategories;
    //标志变量，如果是1，则是商品管理功能； 如果是2，则是选择菜品功能
    private  int mFlag;

    /**
     * 作用：定义并获取控件对象
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView mCommoCateName;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            mCommoCateName = (TextView) cardView.findViewById(R.id.commo_cate_name);
        }
    }

    /**
     * 构造方法：获取数据源
     *
     * @param commodityCategories
     */
    public CheckCategoryAdapter(List<CommodityCategory> commodityCategories) {
        this.mCommodityCategories = commodityCategories;

    }

    /**
     * 作用：获取项目布局文件，形成视图对象
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        //从item布局文件中加载视图对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.commo_cate_item_layout, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    /**
     * 作用：将构造器获取的数据源与上述方法获取的视图对象进行对应绑定
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //从数据源中获取一个对象
        final CommodityCategory commoCate = mCommodityCategories.get(position);
        //将对象中的数据填写入视图
//        holder.mCommoCateImg.setImageResource(commoCate.getImageId());
        holder.mCommoCateName.setText(commoCate.getCategoryName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckRequestNoteActivity.actionStart(mContext,commoCate.getCategoryName());
            }
        });

    }

    /**
     * 作用：获取数据源中数据的个数
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mCommodityCategories.size();
    }


}
