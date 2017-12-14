package com.hbsx.purordermanage.FirstCheck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.FirstCheck.Adapter.FirstCheckDetailAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.RequestNote;
import com.hbsx.purordermanage.utils.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

/**
 * Created by Administrator on 2017/2/13 0013.
 */

public class FirstCheckDetailActivity extends BaseActivity {

    Toolbar toolbar;
    FloatingActionButton flb;
    String employeeName;
    RelativeLayout mPurchaseNumLayout;

    List<RequestNote>  mRequestNoteList;
    RecyclerView mRequestNoteView;
    FirstCheckDetailAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_commodity_detail);

        flb = (FloatingActionButton) findViewById(R.id.btn_add_commodity);
        flb.setVisibility(View.GONE);

        mPurchaseNumLayout = (RelativeLayout) findViewById(R.id.commodity_item_header_dingliang);
        mPurchaseNumLayout.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        employeeName = intent.getStringExtra("employee");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(employeeName+"今日未审报货单");
        toolbar.setNavigationIcon(R.mipmap.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        setSupportActionBar(toolbar);

        mRequestNoteView = (RecyclerView) findViewById(R.id.commodity_detail_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRequestNoteView.setLayoutManager(llm);

        getRequestNote(employeeName);

    }

    /**
     * 启动活动
     * @param mContext
     * @param employeeName
     */
    public static void actionStart(Context mContext, String employeeName){
        Intent intent = new Intent(mContext,FirstCheckDetailActivity.class);
        intent.putExtra("employee",employeeName);
        mContext.startActivity(intent);
    }
    /**
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commit_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.commit_shopping_btn:
                List<BmobObject> objects = new ArrayList<BmobObject>();
                for(RequestNote note : mRequestNoteList){
                    note.setOrderState(1);
                    objects.add(note);
                }
                new BmobBatch().updateBatch(objects).doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> list, BmobException e) {
                        if(e == null){
                            Toast.makeText(FirstCheckDetailActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                getRequestNote(employeeName);
                mAdapter.notifyDataSetChanged();
                finish();
                break;
        }
        return true;
    }

    /**
     * 根据查询条件，利用从云数据库中查询报货单
     */

    private void getRequestNote(String employeeName) {
        String start = Utility.getCurrentDateString() + " 00:00:00";
        String end = Utility.getCurrentDateString() + " 23:59:59";

        BmobQuery<RequestNote> query1 = new BmobQuery<>();
        query1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(start)));
        List<BmobQuery<RequestNote>> and = new ArrayList<>();
        and.add(query1);

        BmobQuery<RequestNote> query2 = new BmobQuery<>();
        query2.addWhereLessThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(end)));
        and.add(query2);

        BmobQuery<RequestNote> query3 = new BmobQuery<>();
        query3.addWhereEqualTo("person",employeeName);
        and.add(query3);

        BmobQuery<RequestNote> query4 = new BmobQuery<>();
        query4.addWhereLessThanOrEqualTo("orderState",1);
        and.add(query4);

        BmobQuery<RequestNote> query = new BmobQuery<>();
        query.and(and);

        query.findObjects(new FindListener<RequestNote>() {
            @Override
            public void done(final List<RequestNote> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = REQUESTNOTE_ITEM;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("requestNotelist", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                } else {
                    toast("查询失败：" + e.getMessage(),false);
                }
            }
        });

    }
    /**
     * 利用handler得到数据
     */
    public static final int REQUESTNOTE_ITEM = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUESTNOTE_ITEM:
                    mRequestNoteList = (List<RequestNote>) msg.getData().getSerializable("requestNotelist");
                    mAdapter = new FirstCheckDetailAdapter(mRequestNoteList);
                    mRequestNoteView.setAdapter(mAdapter);
                    break;
            }

        }
    };
}
