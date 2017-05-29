package com.hbsx.purordermanage.Purchase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hbsx.purordermanage.BaseActivity;
import com.hbsx.purordermanage.Purchase.adapter.ProviderSearchAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 查询：根据用户列表，以及查询日期区间
 * Created by Administrator on 2017/2/11 0011.
 */

public class ProviderOrderSearchActivity extends BaseActivity {
    Toolbar toolbar;

    List<User> mProviderList;
    RecyclerView mProviderView;
    ProviderSearchAdapter mAdapter;
    //查询期间
    int period;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        period = intent.getIntExtra("searchperiod", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch (period) {
            case PurchaseMainActivity.SEARCH_TODAY:
                toolbar.setTitle("今天订单");
                break;
            case PurchaseMainActivity.SEARCH_YESTERDAY:
                toolbar.setTitle("昨天订单");
                break;
        }
        toolbar.setNavigationIcon(R.mipmap.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProviderOrderSearchActivity.this.finish();
            }
        });
        setSupportActionBar(toolbar);

        mProviderList = new ArrayList<>();
        mProviderView = (RecyclerView) findViewById(R.id.search_list);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mProviderView.setLayoutManager(lm);

        getProviderList();

    }

    /**
     * 查询供货商列表
     */
    private void getProviderList() {
        BmobQuery<User> query = new BmobQuery<>();
        query.include("role");
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(final List<User> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = USER_REQUEST;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });


    }

    /**
     * 利用handler得到数据
     */

    public static final int USER_REQUEST = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case USER_REQUEST:
                    List<User> users = (List<User>) msg.getData().getSerializable("user");
                    for (User user : users) {
                        if (user.getRole().getRoleName().equals("供货")) {
                            mProviderList.add(user);
                        }
                    }
                    mAdapter = new ProviderSearchAdapter(mProviderList,period);
                    mProviderView.setAdapter(mAdapter);

                    break;
            }

        }
    };

    /**
     * 启动本活动,传入查询日期方向（当天，当月）标志
     *
     * @param mContext,searchdate查询时间
     */
    public static void actionStart(Context mContext,int period) {
        Intent intent = new Intent(mContext, ProviderOrderSearchActivity.class);
        intent.putExtra("searchperiod",period);
        mContext.startActivity(intent);

    }
}
