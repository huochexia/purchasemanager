package com.hbsx.purordermanage.FirstCheck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.hbsx.purordermanage.BaseActivity;
import com.hbsx.purordermanage.Examine.Adapter.ProviderListAdapter;
import com.hbsx.purordermanage.FirstCheck.Adapter.EmployeeListAdapter;
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

public class EmployeeListActivity extends BaseActivity {
    Toolbar toolbar;

    List<User> mEmployeeList;
    RecyclerView mEmployeeView;
    EmployeeListAdapter mAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("选择报货员");
        setSupportActionBar(toolbar);


        mEmployeeList = new ArrayList<>();
        mEmployeeView = (RecyclerView) findViewById(R.id.search_list);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mEmployeeView.setLayoutManager(lm);

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
                        if (user.getRole().getRoleName().contains("报货")) {
                            mEmployeeList.add(user);
                        }
                    }
                    mAdapter = new EmployeeListAdapter(mEmployeeList);
                    mEmployeeView.setAdapter(mAdapter);
                    break;
            }

        }
    };

    /**
     * 启动本活动,
     *
     * @param mContext,
     */
    public static void actionStart(Context mContext) {
        Intent intent = new Intent(mContext, EmployeeListActivity.class);
        mContext.startActivity(intent);

    }
}
