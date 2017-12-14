package com.hbsx.purordermanage.Examine;

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
public class ProviderOrderActivity extends BaseActivity {
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
        provider = intent.getStringExtra("provider");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(provider+" 订单");
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
        viewPagerAdapter.addFragment(ProviderOrderFragment.newInstance(1,mOrderDate,provider),"未验货");
        viewPagerAdapter.addFragment(ProviderOrderFragment.newInstance(3,mOrderDate,provider),"已检验");
        mViewPager.setAdapter(viewPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.supply_day_tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText("未验货"));
        mTabLayout.addTab(mTabLayout.newTab().setTag("已检验"));
        mTabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * 启动本活动
     * @param mContext
     * @param orderdate  传入订单日期
     */
    public static void actionStart(Context mContext,String orderdate,String provider){
        Intent intent = new Intent(mContext,ProviderOrderActivity.class);
        intent.putExtra("orderdate",orderdate);
        intent.putExtra("provider",provider);
        mContext.startActivity(intent);
    }
}
