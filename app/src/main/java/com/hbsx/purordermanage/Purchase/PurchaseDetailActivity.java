package com.hbsx.purordermanage.Purchase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.Purchase.adapter.PurchaseDetailAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.utils.SimpleItemTouchHelperCallback;
import com.hbsx.purordermanage.utils.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2017/2/22 0022.
 */

public class PurchaseDetailActivity extends BaseActivity {
    private String provider;
    private int searchPeriod;

    private Toolbar toolbar;
    private RelativeLayout purchaseNumHeader;
    private RecyclerView mRecyclerView;
    private PurchaseDetailAdapter mAdapter;

    private List<PurchaseOrder> purchaseOrderList;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_detail);
        Intent intent = getIntent();
        provider =  intent.getStringExtra("provider");
        searchPeriod = intent.getIntExtra("searchperiod", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch (searchPeriod) {
            case PurchaseMainActivity.SEARCH_TODAY:
                toolbar.setTitle("当天订单");
                break;
            case PurchaseMainActivity.SEARCH_YESTERDAY:
                toolbar.setTitle("昨日订单");
                break;
        }

        toolbar.setNavigationIcon(R.mipmap.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseDetailActivity.this.finish();
            }
        });
        setSupportActionBar(toolbar);

        purchaseNumHeader = (RelativeLayout) findViewById(R.id.commodity_item_header_dingliang);
        purchaseNumHeader.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.purchase_detail_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        purchaseOrderList = new ArrayList<>();

        getPurchaseDetail();
    }

    /**
     * 获取订购商品列表
     */
    private void getPurchaseDetail() {
        BmobQuery<PurchaseOrder> query = new BmobQuery<>();
        String start = Utility.getCurrentDateString() + " 00:00:00";
        String end = Utility.getCurrentDateString() + " 23:59:59";
        switch (searchPeriod) {
            case PurchaseMainActivity.SEARCH_TODAY://查询当天
                BmobQuery<PurchaseOrder> query1 = new BmobQuery<>();
                query1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(start)));
                List<BmobQuery<PurchaseOrder>> and = new ArrayList<>();
                and.add(query1);

                BmobQuery<PurchaseOrder> query2 = new BmobQuery<>();
                query2.addWhereLessThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(end)));
                and.add(query2);

                BmobQuery<PurchaseOrder> query3 = new BmobQuery<>();
                query3.addWhereEqualTo("providername", provider);
                and.add(query3);

                BmobQuery<PurchaseOrder> query4 = new BmobQuery<>();
                query4.addWhereGreaterThanOrEqualTo("orderState", 1);
                and.add(query4);

                query.and(and);
                break;
            case PurchaseMainActivity.SEARCH_YESTERDAY://查询昨天
                List<BmobQuery<PurchaseOrder>> and2 = new ArrayList<>();

                BmobQuery<PurchaseOrder> queryPerson = new BmobQuery<>();
                queryPerson.addWhereEqualTo("providername", provider);
                and2.add(queryPerson);

                BmobQuery<PurchaseOrder> queryS = new BmobQuery<>();
                queryS.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(Utility.getForwardDate(1)));
                and2.add(queryS);

                BmobQuery<PurchaseOrder> queryE = new BmobQuery<>();
                queryE.addWhereLessThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(start)));
                and2.add(queryE);

                query.and(and2);
                break;
        }
        query.order("category");
        query.findObjects(new FindListener<PurchaseOrder>() {
            @Override
            public void done(final List<PurchaseOrder> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = PURCHASEORDER_LIST;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("purchaseorderlist", (Serializable) list);
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
    public static final int PURCHASEORDER_LIST = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PURCHASEORDER_LIST:
                    purchaseOrderList = (List<PurchaseOrder>) msg.getData().getSerializable("purchaseorderlist");
                    if (purchaseOrderList.size() <= 0) {
                        toast("此供货商无订单！",false);
                    } else {
                        mAdapter = new PurchaseDetailAdapter(purchaseOrderList);
                        mRecyclerView.setAdapter(mAdapter);
                        toolbar.setSubtitle("共"+purchaseOrderList.size()+"项商品");
                        //关联ItemTouchHelper和RecyclerView
                        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
                        mItemTouchHelper = new ItemTouchHelper(callback);
                        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

                    }

                    break;
            }
        }
    };

    /**
     * 启动活动
     */
    public static void actionStart(Context mContext, String provider, int periodflag) {
        Intent intent = new Intent(mContext, PurchaseDetailActivity.class);
        intent.putExtra("provider", provider);
        intent.putExtra("searchperiod", periodflag);
        mContext.startActivity(intent);
    }
}
