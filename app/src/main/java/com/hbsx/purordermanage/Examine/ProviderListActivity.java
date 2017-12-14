package com.hbsx.purordermanage.Examine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.Examine.Adapter.ProviderListAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 查询：根据用户列表，以及查询日期
 * Created by Administrator on 2017/2/11 0011.
 */

public class ProviderListActivity extends BaseActivity {
    Toolbar toolbar;

    List<User> mProviderList;
    RecyclerView mProviderView;
    ProviderListAdapter mAdapter;
    //查询日期间
    String orderDate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        orderDate = intent.getStringExtra("orderDate");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("选择供货商");
        toolbar.setSubtitle(orderDate);
        toolbar.setNavigationIcon(R.mipmap.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setSupportActionBar(toolbar);

        mProviderList = new ArrayList<>();
        mProviderView = (RecyclerView) findViewById(R.id.search_list);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mProviderView.setLayoutManager(lm);

//        getProviderList();

    }

    /**
     * 为了达到刷新角标提示内容的目的，将数据加载过程放入onStart（）函数中
     */
    @Override
    protected void onStart() {
        super.onStart();
        mProviderList.clear();
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
                    mAdapter = new ProviderListAdapter(mProviderList, orderDate);
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
    public static void actionStart(Context mContext, String orderDate) {
        Intent intent = new Intent(mContext, ProviderListActivity.class);
        intent.putExtra("orderDate", orderDate);
        mContext.startActivity(intent);

    }
}
