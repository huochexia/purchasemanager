package com.hbsx.purordermanage.Purchase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.Purchase.adapter.ProviderSpinnerAdapter;
import com.hbsx.purordermanage.Purchase.adapter.SendToOrderAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.bean.User;
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
 * Created by Administrator on 2017/2/15 0015.
 */

public class SendToProviderActivity extends BaseActivity {
    //临时存储已选择商品列表
    private static List<PurchaseOrder> mSelectedOrders;

    Toolbar toolbar;
    RelativeLayout purchaseNumLayout;
    RelativeLayout selectedLayout;

    RecyclerView purchaseOrderView;
    List<PurchaseOrder> purchaseOrders = new ArrayList<>();
    SendToOrderAdapter adapter;

    AppCompatSpinner providerSpinner;
    List<User> mProviderList = new ArrayList<>();
    ProviderSpinnerAdapter spinnerAdapter;
    User provider;

    Button sendToBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_provider);
        mSelectedOrders = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("向供货商发送商品订单");
        toolbar.setNavigationIcon(R.mipmap.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);

        purchaseNumLayout = (RelativeLayout) findViewById(R.id.commodity_item_header_dingliang);
        purchaseNumLayout.setVisibility(View.VISIBLE);
        selectedLayout = (RelativeLayout) findViewById(R.id.commodity_item_header_select);
        selectedLayout.setVisibility(View.VISIBLE);

        purchaseOrderView = (RecyclerView) findViewById(R.id.send_to_purchase_order_select_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        purchaseOrderView.setLayoutManager(llm);
        getPurchaseOrderList();

        //加载供货商
        providerSpinner = (AppCompatSpinner) findViewById(R.id.provider_selected_spinner);
        providerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                provider = mProviderList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getProviderList();

        sendToBtn = (Button) findViewById(R.id.send_to_btn);
        sendToBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BmobObject> Orders = new ArrayList<>();
                for (PurchaseOrder order : mSelectedOrders) {
//                    order.setProvider(provider);//修改订货单的供货商
                    order.setProvidername(provider.getUsername());
                    order.setOrderDate(Utility.getCurrentDateString());
                    order.setOrderState(1);//修改订货单状态为1
                    Orders.add(order);
                }
                purchaseOrders.removeAll(mSelectedOrders);//从显示列表中删除已选择项
                adapter.notifyDataSetChanged();
                adapter.RefreshMap();
                clearSelectedOrderList();//清空已选择列表
                new BmobBatch().updateBatch(Orders).doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> list, BmobException e) {
                    }
                });

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getPurchaseOrderList();
    }

    /**
     * 向已选择列表中增加订单
     *
     * @param order
     */
    public static void addSelectedToOrderList(PurchaseOrder order) {
        mSelectedOrders.add(order);
    }

    /**
     * 从已选择列表中删除订单
     */
    public static void deleteSelectedToOrderList(PurchaseOrder order) {
        mSelectedOrders.remove(order);
    }

    /**
     * 清空已选择列表
     */
    public static void clearSelectedOrderList() {
        mSelectedOrders.clear();
    }

    /**
     * 获得全局变量，商品列表
     */
    public static List<PurchaseOrder> getSelectedCommodityList() {
        return mSelectedOrders;
    }

    /**
     * 启动本活动
     */
    public static void actionStart(Context mContext) {
        Intent intent = new Intent(mContext, SendToProviderActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 从云数据库中获取所有尚未分配的订单表
     */
    public void getPurchaseOrderList() {
        List<BmobQuery<PurchaseOrder>> and = new ArrayList<>();
        //查询状态为0，即尚处理的订单
        BmobQuery<PurchaseOrder> queryState = new BmobQuery<>();
        queryState.addWhereEqualTo("orderState", 0);
        and.add(queryState);
        //查询当日的
        String current = Utility.getCurrentDateString();
        String start = current + " 00:00:00";
        String end = current + " 23:59:59";
        BmobQuery<PurchaseOrder> queryStart = new BmobQuery<>();
        queryStart.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(start)));
        and.add(queryStart);
        BmobQuery<PurchaseOrder> queryEnd = new BmobQuery<>();
        queryEnd.addWhereLessThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(end)));
        and.add(queryEnd);

        BmobQuery<PurchaseOrder> query = new BmobQuery<>();
        query.and(and);
        query.order("category");
        query.setCachePolicy(BmobQuery.CachePolicy.IGNORE_CACHE);
        query.findObjects(new FindListener<PurchaseOrder>() {
            @Override
            public void done(final List<PurchaseOrder> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = PURCHASE_OREDR_REQUEST;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("purchaseorder", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                } else {
                    Toast.makeText(SendToProviderActivity.this, "查询失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 获得供货商列表
     */
    public void getProviderList() {
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

    public static final int PURCHASE_OREDR_REQUEST = 1;
    public static final int USER_REQUEST = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case PURCHASE_OREDR_REQUEST:
                    purchaseOrders.clear();
                    List<PurchaseOrder> orders = (List<PurchaseOrder>) msg.getData().getSerializable("purchaseorder");
                    for (PurchaseOrder order : orders) {
                        purchaseOrders.add(order);
                    }
                    adapter = new SendToOrderAdapter(purchaseOrders);
                    purchaseOrderView.setAdapter(adapter);
                    break;
                case USER_REQUEST:
                    List<User> users = (List<User>) msg.getData().getSerializable("user");
                    for (User user : users) {
                        if (user.getRole().getRoleName().equals("供货")) {
                            mProviderList.add(user);
                        }
                    }
                    spinnerAdapter = new ProviderSpinnerAdapter(SendToProviderActivity.this, mProviderList);
                    providerSpinner.setAdapter(spinnerAdapter);

            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.purchase_menu,menu);
        return true;
    }
    public static final int SEARCH_TODAY = 0;
    public static final int SEARCH_YESTERDAY = 1;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.today_purchase_order:
                ProviderOrderSearchActivity.actionStart(this,SEARCH_TODAY);
                break;
            case R.id.yesterday_purchase_order:
                ProviderOrderSearchActivity.actionStart(this,SEARCH_YESTERDAY);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
