package com.hbsx.purordermanage.InputData;

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
 * 每日订单详情，每日订单有两种，一种是未录入单，一种是已录入单
 * Created by Administrator on 2017/2/24 0024.
 */
public class LookAndInputOrderActivity extends BaseActivity {
    private Toolbar  toolbar;
    private String mOrderDate;
    private String provider;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_day_layout);
        Intent intent = getIntent();
        mOrderDate = intent.getStringExtra("orderdate");
        provider =  intent.getStringExtra("provider");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(provider+"的订单");
        toolbar.setSubtitle(mOrderDate);
        toolbar.setNavigationIcon(R.mipmap.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        //查询状态为3即已确认订单
        viewPagerAdapter.addFragment(LookAndInputOrderFragment.newInstance(3,mOrderDate,provider),"未录入");
        //查询状态为4即已录入订单
        viewPagerAdapter.addFragment(LookAndInputOrderFragment.newInstance(4,mOrderDate,provider),"已录入");
        mViewPager.setAdapter(viewPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.supply_day_tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText("未录入"));
        mTabLayout.addTab(mTabLayout.newTab().setTag("已录入"));
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LookAndInputOrderFragment.mSelectedOrders.clear();
    }

    /**
     * 启动本活动
     * @param mContext
     * @param orderdate  传入订单日期
     */
    public static void actionStart(Context mContext,String orderdate,String provider){
        Intent intent = new Intent(mContext,LookAndInputOrderActivity.class);
        intent.putExtra("orderdate",orderdate);
        intent.putExtra("provider",provider);
        mContext.startActivity(intent);
    }
}
