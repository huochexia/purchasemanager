package com.hbsx.purordermanage.Manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.CommodityCategory;

/**
 * Created by Administrator on 2017/1/14 0014.
 */

public class CommodityDetailActivity extends BaseActivity {
    private CommodityDetailFragment cdf;

    CommodityCategory mCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_detail);

        Intent intent =getIntent();
        Bundle bundle = intent.getExtras();
        mCategory = (CommodityCategory) bundle.getSerializable("category");

        FragmentManager fm = getSupportFragmentManager();
        cdf = new CommodityDetailFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.commodity_detail_fragment_container,cdf);
        ft.commit();
    }

    /**
     * 静态方法，启动此活动时，传入一个数据
     * @param context
     * @param
     */
    public static void actionStart(Context context, CommodityCategory category){
        Intent intent = new Intent(context,CommodityDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("category",category);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
