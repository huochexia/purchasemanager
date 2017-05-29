package com.hbsx.purordermanage.AgainCheck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hbsx.purordermanage.BaseActivity;
import com.hbsx.purordermanage.AgainCheck.adapter.CheckRequestNoteDetailAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.bean.RequestNote;
import com.hbsx.purordermanage.bean.User;
import com.hbsx.purordermanage.utils.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class CheckRequestNoteActivity extends BaseActivity {
    Toolbar toolbar;
    String mCategory;
    Button checkBtn;
    RelativeLayout purchaseNumLayout;

    RecyclerView summarySheetView;
    CheckRequestNoteDetailAdapter mAdapter;
    List<RequestNote> allList = new ArrayList<>();//所有数据状态为未审核的数据
    List<RequestNote> summarySheet = new ArrayList<>();//汇总去重后的报货单
    List<BmobObject> purchaseOrders = new ArrayList<>();//最后形成的订货单

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_check_requestnote);
        Intent intent = getIntent();
        mCategory = intent.getStringExtra("category");

        initView();
        //获取报货单
        getAllSheet();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mCategory);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        purchaseNumLayout = (RelativeLayout) findViewById(R.id.commodity_item_header_dingliang);
        purchaseNumLayout.setVisibility(View.VISIBLE);
        summarySheetView = (RecyclerView) findViewById(R.id.summary_request_note_sheet);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        summarySheetView.setLayoutManager(llm);
        //审核后，将参加汇总的报货单状态改为2。
        checkBtn = (Button) findViewById(R.id.check_request_note_btn);
        checkBtn.setEnabled(false);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //生成订单
                purchaseOrders = createPurchaseOrders(summarySheet);
                //保存订单
                savePurchaseOrder(purchaseOrders);
                //修改报货单状态为2，已审核
                changeRequestNoteState(allList, 2);
                checkBtn.setEnabled(false);
            }
        });
    }

    /**
     * 将报货单转换成订单
     */
    private List<BmobObject> createPurchaseOrders(List<RequestNote> list) {
        List<BmobObject> objects = new ArrayList<>();
        //获取当前用户
        User myUser = BmobUser.getCurrentUser(User.class);
        for (RequestNote good : list) {
            PurchaseOrder po = new PurchaseOrder();
            po.setOrderUserName(myUser.getUsername());
            po.setCategory(good.getCategory());
            po.setCommodityName(good.getCommodityName());
            po.setCommodityUnit(good.getUnit());
            po.setPurchaseNum(good.getPurchaseNum());
            po.setOrderState(0);
            objects.add(po);
        }
        return objects;
    }

    /**
     * 将审核后的订单保存入数据库
     */
    private void savePurchaseOrder(List<BmobObject> orders) {
        //如果订单数据小于50，则使用批处理保存，否则使用单个保存
        if (orders.size() < 50) {
            new BmobBatch().insertBatch(orders).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e != null) {
                        Toast.makeText(CheckRequestNoteActivity.this, "订单生成错误" + e.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        summarySheet.clear();
                        mAdapter.notifyDataSetChanged();
                        finish();
                    }
                }
            });
        } else {
            for (BmobObject po : orders) {
                po.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e != null) {
                            Toast.makeText(CheckRequestNoteActivity.this, "订单生成错误" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }

    }

    /**
     * 审核后状报货单状态改为已审核，值为2
     */
    private void changeRequestNoteState(List<RequestNote> list, int state) {
        List<BmobObject> bmobObjects = new ArrayList<>();
        for (RequestNote item : list) {
            item.setOrderState(state);
            bmobObjects.add(item);
        }
        //如果报货单数量小于50，则采用批处理方式变量其状态；如果大于50条，则逐条修改。
        if (bmobObjects.size() < 50) {
            new BmobBatch().updateBatch(bmobObjects).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e != null) {
                        Toast.makeText(CheckRequestNoteActivity.this, "报货单变更错误" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            for (BmobObject item : bmobObjects) {
                item.update(item.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e != null) {
                            Toast.makeText(CheckRequestNoteActivity.this, "报货单变更错误" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }


    /**
     * 生成报货单总表,当天所有状态为0，1的报货单参加汇总
     */
    public void getAllSheet() {
        List<BmobQuery<RequestNote>> and = new ArrayList<>();
        //第一个查询条件状态为0或者1的
        BmobQuery<RequestNote> queryState = new BmobQuery<>();
        queryState.addWhereLessThanOrEqualTo("orderState", 1);
        and.add(queryState);

        //第二个查询条件开始时间
        String start = Utility.getCurrentDateString() + " 00:00:00";
        BmobQuery<RequestNote> queryStart = new BmobQuery<>();
        queryStart.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(start)));
        and.add(queryStart);

        //第三个查询条件，结束时间
        String end = Utility.getCurrentDateString() + " 23:59:59";
        BmobQuery<RequestNote> queryEnd = new BmobQuery<>();
        queryEnd.addWhereLessThanOrEqualTo("createdAt", new BmobDate(Utility.getDate(end)));
        and.add(queryEnd);
        //第四个查询条件，商品类别
        BmobQuery<RequestNote> queryCate = new BmobQuery<>();
        queryCate.addWhereEqualTo("category",mCategory);
        and.add(queryCate);
        //汇总查询
        BmobQuery<RequestNote> querySum = new BmobQuery<>();
        querySum.and(and);

        querySum.findObjects(new FindListener<RequestNote>() {
            @Override
            public void done(final List<RequestNote> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        checkBtn.setEnabled(true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = SUMMARY_SHEET;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("summarysheet", (Serializable) list);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }).start();
                    } else {
                        Toast.makeText(CheckRequestNoteActivity.this, "此类商品今日没有报货单！", Toast.LENGTH_SHORT).show();
                        checkBtn.setEnabled(false);
                    }

                } else {
                    Toast.makeText(CheckRequestNoteActivity.this,
                            "查询失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public static final int SUMMARY_SHEET = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUMMARY_SHEET:
                    allList = (List<RequestNote>) msg.getData().getSerializable("summarysheet");
                    getSummarySheet(allList);
                    mAdapter = new CheckRequestNoteDetailAdapter(summarySheet);
                    summarySheetView.setAdapter(mAdapter);
                    toolbar.setSubtitle(summarySheet.size()+"种");
            }
        }
    };

    /**
     * 汇总报货单，合并同类数量
     */
    private void getSummarySheet(List<RequestNote> list) {
        List<RequestNote> list1 = new ArrayList<>();
        //进行深拷贝
        for (RequestNote note : list) {
            list1.add((RequestNote) note.clone());
        }
        Iterator it = list1.iterator();
        Map map = new HashMap();
        while (it.hasNext()) {
            RequestNote psi = (RequestNote) it.next();
            String key = psi.getCommodityName();//获取商品的名称做为key
            if (map.get(key) == null) {//如果没有
                map.put(key, psi);//加入map，key为商品名，value为报货单商品对象
            } else {//如果存在同名商品
                RequestNote psi1 = (RequestNote) map.get(key);//从map中找到重名商品
                psi1.setPurchaseNum(psi1.getPurchaseNum() + psi.getPurchaseNum());
                map.put(key, psi1);
            }

        }

        //历map，存入List中
        for (Object obj : map.keySet()) {
            RequestNote item = (RequestNote) map.get(obj);
            if (item.getPurchaseNum() != 0) {
                summarySheet.add(item);
            }
        }
    }
    /**
     * 启动本活动，传入参数
     */
    public static void actionStart(Context mContext,String category){
        Intent intent = new Intent(mContext,CheckRequestNoteActivity.class);
        intent.putExtra("category",category);
        mContext.startActivity(intent);

    }
}
