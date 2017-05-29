package com.hbsx.purordermanage.Request;

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
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Request.adapter.RequestNoteSearchAdapter;
import com.hbsx.purordermanage.bean.RequestNote;
import com.hbsx.purordermanage.bean.User;
import com.hbsx.purordermanage.utils.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 查询：显示出所有订单编号，然后点击编号获取详细内容
 * Created by Administrator on 2017/2/11 0011.
 */

public class RequestNoteNumSearchActivity extends BaseActivity {
    Toolbar toolbar;

    List<String> mOrderNumList;
    RecyclerView mOrdersView;
    RequestNoteSearchAdapter adapter;
    //查询方向
    int diraction;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        diraction = intent.getIntExtra("searchdate", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch (diraction) {
            case RequestNoteMainActivity.SEARCH_TODAY:
                toolbar.setTitle("今天报货单");
                break;
            case RequestNoteMainActivity.SEARCH_WEEK:
                toolbar.setTitle("近三天报货单");
                break;
        }
        toolbar.setNavigationIcon(R.mipmap.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestNoteNumSearchActivity.this.finish();
            }
        });
        setSupportActionBar(toolbar);

        mOrderNumList = new ArrayList<>();
        mOrdersView = (RecyclerView) findViewById(R.id.search_list);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mOrdersView.setLayoutManager(lm);

        getRequestNoteList(diraction);

    }

    /**
     * 根据查询条件，利用从云数据库中查询报货单
     */

    private void getRequestNoteList(int diraction) {
        BmobQuery<RequestNote> query = new BmobQuery<>();
        //获取当前用户
        User myUser = BmobUser.getCurrentUser(User.class);

        String start = Utility.getCurrentDateString() + " 00:00:00";
        String end = Utility.getCurrentDateString() + " 23:59:59";
        switch (diraction) {
            case RequestNoteMainActivity.SEARCH_TODAY://查询当天
                BmobQuery<RequestNote> query1 = new BmobQuery<>();
                query1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(start)));
                List<BmobQuery<RequestNote>> and = new ArrayList<>();
                and.add(query1);

                BmobQuery<RequestNote> query2 = new BmobQuery<>();
                query2.addWhereLessThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(end)));
                and.add(query2);

                BmobQuery<RequestNote> query3 = new BmobQuery<>();
                query3.addWhereEqualTo("person",myUser.getUsername());
                and.add(query3);

                query.and(and);
                break;
            case RequestNoteMainActivity.SEARCH_WEEK://查询一周内
                BmobQuery<RequestNote> queryPerson = new BmobQuery<>();
                queryPerson.addWhereEqualTo("person",myUser.getUsername());
                List<BmobQuery<RequestNote>> and2 = new ArrayList<>();
                and2.add(queryPerson);

                BmobQuery<RequestNote> queryDate = new BmobQuery<>();
                queryDate.addWhereGreaterThanOrEqualTo("createdAt",new BmobDate(Utility.getForwardDate(2)));
                and2.add(queryDate);

                query.and(and2);
                break;
        }
        query.order("createdAt");
        query.findObjects(new FindListener<RequestNote>() {
            @Override
            public void done(final List<RequestNote> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = REQUESTNOTE_LIST;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("requestNotelist", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                } else {
                    toast("查询失败：" + e.getMessage());
                }
            }
        });

    }



    /**
     * 利用handler得到数据
     */
    public static final int REQUESTNOTE_LIST = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUESTNOTE_LIST:
                    List<RequestNote> mRequestNoteList = (List<RequestNote>) msg.getData().getSerializable("requestNotelist");

                    for (RequestNote item : mRequestNoteList) {
                        mOrderNumList.add(item.getOrderNumber());
                    }
                    Utility.removeDuplicate(mOrderNumList);//去重复数据
                    //利用数据源生成配器
                    adapter = new RequestNoteSearchAdapter(mOrderNumList);
                    mOrdersView.setAdapter(adapter);
                    break;
            }

        }
    };

    /**
     * 启动本活动,传入查询日期方向（当天，当月）标志
     *
     * @param mContext,searchdate查询时间
     */
    public static void actionStart(Context mContext, int searchdate) {
        Intent intent = new Intent(mContext, RequestNoteNumSearchActivity.class);
        intent.putExtra("searchdate", searchdate);
        mContext.startActivity(intent);

    }
}
