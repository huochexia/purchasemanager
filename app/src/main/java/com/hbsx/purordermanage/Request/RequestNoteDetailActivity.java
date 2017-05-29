package com.hbsx.purordermanage.Request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.hbsx.purordermanage.BaseActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Request.adapter.RequestNoteDetailAdapter;
import com.hbsx.purordermanage.bean.RequestNote;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * Created by Administrator on 2017/2/13 0013.
 */

public class RequestNoteDetailActivity extends BaseActivity {

    Toolbar toolbar;
    FloatingActionButton flb;
    String ordernumber;
    RelativeLayout mPurchaseNumLayout;

    List<RequestNote>  mRequestNoteList;
    RecyclerView mRequestNoteView;
    RequestNoteDetailAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_commodity_detail);

        flb = (FloatingActionButton) findViewById(R.id.btn_add_commodity);
        flb.setVisibility(View.GONE);

        mPurchaseNumLayout = (RelativeLayout) findViewById(R.id.commodity_item_header_dingliang);
        mPurchaseNumLayout.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        ordernumber = intent.getStringExtra("ordernumber");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(ordernumber);
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

        getRequestNote(ordernumber);

    }
    public static void actionStart(Context mContext, String ordernumber){
        Intent intent = new Intent(mContext,RequestNoteDetailActivity.class);
        intent.putExtra("ordernumber",ordernumber);
        mContext.startActivity(intent);
    }

    /**
     * 根据查询条件，利用从云数据库中查询报货单
     */

    private void getRequestNote(String ordernumber) {
        BmobQuery<RequestNote> query = new BmobQuery<>();
        /**
         *BSQl基本查询方式
         */
        String bsql = "select * from RequestNote where orderNumber = ? ";
        query.setSQL(bsql);
        query.setPreparedParams(new Object[]{ordernumber});
        query.doSQLQuery(new SQLQueryListener<RequestNote>() {
            @Override
            public void done(BmobQueryResult<RequestNote> bmobQueryResult, BmobException e) {
                if(e ==null){
                    List<RequestNote> mRequestNoteList =bmobQueryResult.getResults();
                    if(mRequestNoteList!=null && mRequestNoteList.size()>0){
                        mAdapter = new RequestNoteDetailAdapter(mRequestNoteList);
                        mRequestNoteView.setAdapter(mAdapter);
                        toolbar.setSubtitle("共" + mRequestNoteList.size() + "项");
                    }

                }else{
                    toast("查询失败"+e.getMessage());
                }
            }
        });
        /**
         * Bmob组合查询方式
         */
//        query.addWhereEqualTo("orderNumber",ordernumber);
//        query.findObjects(new FindListener<RequestNote>() {
//            @Override
//            public void done(final List<RequestNote> list, BmobException e) {
//                if (e == null) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Message msg = new Message();
//                            msg.what = REQUESTNOTE_ITEM;
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("requestNotelist", (Serializable) list);
//                            msg.setData(bundle);
//                            handler.sendMessage(msg);
//                        }
//                    }).start();
//                } else {
//                    toast("查询失败：" + e.getMessage());
//                }
//            }
//        });

    }
//    /**
//     * 利用handler得到数据
//     */
//    public static final int REQUESTNOTE_ITEM = 1;
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case REQUESTNOTE_ITEM:
//                    List<RequestNote> mRequestNoteList = (List<RequestNote>) msg.getData().getSerializable("requestNotelist");
//                    mAdapter = new RequestNoteDetailAdapter(mRequestNoteList);
//                    mRequestNoteView.setAdapter(mAdapter);
//                    toolbar.setSubtitle("共"+mRequestNoteList.size()+"项");
//                    break;
//            }
//
//        }
//    };
}
