package com.hbsx.purordermanage.Supply;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.utils.MyViewPagerAdapter;

/**
 * 每日订单详情，每日订单有两种，一种是验货单，一种是订货单
 * Created by Administrator on 2017/2/24 0024.
 */
public class SupplyDayOrderActivity extends BaseActivity {
    private Toolbar  toolbar;
    private String mOrderDate;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_day_layout);
        Intent intent = getIntent();
        mOrderDate = intent.getStringExtra("orderdate");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mOrderDate+" 订单");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(SupplyDayOrderFragment.newInstance(1,mOrderDate),"订货单");
        viewPagerAdapter.addFragment(SupplyDayOrderFragment.newInstance(2,mOrderDate),"验货单");
        viewPagerAdapter.addFragment(SupplyDayOrderFragment.newInstance(3,mOrderDate),"确认单");
        mViewPager.setAdapter(viewPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.supply_day_tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText("订货单"));
        mTabLayout.addTab(mTabLayout.newTab().setTag("验货单"));
        mTabLayout.addTab(mTabLayout.newTab().setTag("确认单"));
        mTabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * 启动本活动
     * @param mContext
     * @param orderdate  传入订单日期
     */
    public static void actionStart(Context mContext,String orderdate){
        Intent intent = new Intent(mContext,SupplyDayOrderActivity.class);
        intent.putExtra("orderdate",orderdate);
        mContext.startActivity(intent);
    }
}
