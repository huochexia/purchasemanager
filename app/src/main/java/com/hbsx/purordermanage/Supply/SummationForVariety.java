package com.hbsx.purordermanage.Supply;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


import com.hbsx.purordermanage.BaseActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Supply.adapter.SummationForVarietylAdapter;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.bean.User;
import com.hbsx.purordermanage.utils.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 按商品的品种进行汇总求和
 * Created by Administrator on 2017/7/6 0006.
 */

public class SummationForVariety extends BaseActivity {
    Toolbar toolbar;
    TextView mTotal;

    RecyclerView mVarietyList;
    SummationForVarietylAdapter mAdapter;
    List<PurchaseOrder>  mList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summation_varitey_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("按商品名称汇总");
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String start = intent.getStringExtra("start_date");
        String end = intent.getStringExtra("end_date");
        getTotal(start,end);
        mTotal = (TextView) findViewById(R.id.summation_variety_total);
        mVarietyList = (RecyclerView) findViewById(R.id.summation_variety_detail_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mVarietyList.setLayoutManager(llm);
    }
    /**
     * 计算两个日期间商品金额合计
     *
     * @param
     * @return
     */
    public void getTotal(String start, String end) {
        List<BmobQuery<PurchaseOrder>> and = new ArrayList<>();

        User myUser = BmobUser.getCurrentUser(User.class);
        BmobQuery<PurchaseOrder> queryUser = new BmobQuery<>();
        queryUser.addWhereEqualTo("providername", myUser.getUsername());
        and.add(queryUser);

        BmobQuery<PurchaseOrder> startQ = new BmobQuery<>();
        startQ.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(start + " 00:00:00")));
        and.add(startQ);

        BmobQuery<PurchaseOrder> endQ = new BmobQuery<>();
        endQ.addWhereLessThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(end + " 23:59:59")));
        and.add(endQ);
        BmobQuery<PurchaseOrder> stateQ = new BmobQuery<>();
        stateQ.addWhereEqualTo("orderState", 4);//已确认的订单
        and.add(stateQ);
        //组合查询条件
        BmobQuery<PurchaseOrder> query = new BmobQuery<>();
        query.and(and);
        query.setLimit(999);
        query.findObjects(new FindListener<PurchaseOrder>() {
            @Override
            public void done(final List<PurchaseOrder> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = SUM;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("total", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
    }

    public static final int SUM = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUM:
                    Float sumprice = 0.0f;
                    List<PurchaseOrder> list = (List<PurchaseOrder>) msg.getData().getSerializable("total");
                    Iterator it = list.iterator();
                    Map map = new HashMap();
                    while(it.hasNext()){
                        PurchaseOrder psi = (PurchaseOrder) it.next();
                        psi.setSum(psi.getActualNum()*psi.getPrice());
                        String key = psi.getCommodityName();//获取商品的名称做为key
                        if(map.get(key) == null){//如果没有
                            map.put(key,psi);//加入map，key为商品名，value为订货单商品对象
                        }else {//如果存在同名商品
                            PurchaseOrder psi1 = (PurchaseOrder) map.get(key);//从map中找到重名商品
                            psi1.setActualNum(psi1.getActualNum() + psi.getActualNum());
                            psi1.setSum(psi1.getSum()+psi.getSum());
                            map.put(key,psi1);
                        }
                    }
                    list.clear();//最后清空了
                    //历map，存入List中
                    for(Object obj:map.keySet()){
                        PurchaseOrder item = (PurchaseOrder) map.get(obj);
                        list.add(item);
                    }
                    mAdapter = new SummationForVarietylAdapter(list);
                    mVarietyList.setAdapter(mAdapter);
                    for (PurchaseOrder order : list) {
                        sumprice = sumprice + order.getSum();
                    }
                    Float sum = (float) (Math.round(sumprice * 100)) / 100;
                    mTotal.setText(sum + "");
                    break;
            }

        }
    };
}
