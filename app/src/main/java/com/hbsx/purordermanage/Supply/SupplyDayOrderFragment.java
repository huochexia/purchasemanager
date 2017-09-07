package com.hbsx.purordermanage.Supply;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Supply.adapter.SupplyPriceDetailAdapter;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.bean.User;
import com.hbsx.purordermanage.utils.Utility;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * 供货清单Fragment，一种是订货单，一种是验货单
 * Created by Administrator on 2017/2/24 0024.
 */

public class SupplyDayOrderFragment extends Fragment {
    public static final String ORDER_STATE = "state";
    public static final String ORDER_DATE = "date";

    private Button mCommitPrice;
    private TextView mPriceSum;
    private Float mSum;
    private RelativeLayout mPriceLayout;

    private RecyclerView mRecyclerView;
    private List<PurchaseOrder> mPurchaseOrderList = new ArrayList<>();
    private SupplyPriceDetailAdapter mAdapter;

    //查询日期,如果是当天，则为提交功能，仅显示价格表头，显示提交按钮，屏敝小计；
    // 如果不是当天则显示实数和价格表头，以及小计屏敝提交按钮
    private int mOrderState;
    private String mOrderDate;
    private String bql;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mOrderState = bundle.getInt(ORDER_STATE);
            mOrderDate = bundle.getString(ORDER_DATE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supply_num_price, container, false);
        try {
            initView(view);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        getPurchaseOrderList(mOrderState);
        return view;
    }

    private void initView(View view) throws ParseException {
        mPriceLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_header_price);
        mPriceLayout.setVisibility(View.VISIBLE);
        mPriceSum = (TextView) view.findViewById(R.id.supply_price_sum1);
        mCommitPrice = (Button) view.findViewById(R.id.supply_commit_price_btn1);
//        mCommitPrice.setEnabled(Utility.compareTime(" 10:30:00"));//大于当天10点半后，不允许确认。
        mCommitPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOrderState == 2) {
                    List<BmobObject> objects = new ArrayList<>();
                    for (PurchaseOrder order : mPurchaseOrderList) {
                        order.setOrderState(3);//修改订单状态为3，表示已确认
                        objects.add(order);
                    }
                    new BmobBatch().updateBatch(objects).doBatch(new QueryListListener<BatchResult>() {
                        @Override
                        public void done(List<BatchResult> list, BmobException e) {

                        }
                    });
                    //因为通过BQL基本语句查询得到的结果是一个不可修改列表，所以不能使用removeAll等方法
                    //进行列表清空处理，这里重新创建了一个空列表，用于取代原查询结果列表，达到清屏的目的。
                    List<PurchaseOrder> list = new ArrayList<>();
                    mAdapter = new SupplyPriceDetailAdapter(list, mOrderState);
                    mRecyclerView.setAdapter(mAdapter);
                }

            }
        });
        switch (mOrderState) {
            case 1:
                RelativeLayout purchaseNumLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_header_dingliang);
                purchaseNumLayout.setVisibility(View.VISIBLE);
                mPriceSum.setVisibility(View.GONE);
                mCommitPrice.setVisibility(View.GONE);
                break;
            case 2:
                RelativeLayout actulNumLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_header_actualnum);
                actulNumLayout.setVisibility(View.VISIBLE);
                mPriceSum.setVisibility(View.GONE);
                mCommitPrice.setText("确 认");
                break;
            case 3:
                RelativeLayout actulNumLayout1 = (RelativeLayout) view.findViewById(R.id.commodity_item_header_actualnum);
                actulNumLayout1.setVisibility(View.VISIBLE);
                mCommitPrice.setVisibility(View.GONE);
                break;

        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.supply_number_price_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);


    }

    /**
     * 构造一个Fragment,同时传入相应值
     *
     * @param state
     * @return
     */
    public static SupplyDayOrderFragment newInstance(int state, String orderDate) {
        Bundle bundle = new Bundle();
        bundle.putInt(ORDER_STATE, state);
        bundle.putString(ORDER_DATE, orderDate);
        SupplyDayOrderFragment fragment = new SupplyDayOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 获取订单列表
     */
    private void getPurchaseOrderList(int state) {
        //当前用户
        User myUser = BmobUser.getCurrentUser(User.class);
          /*
          基本语句查询
           */
        BmobQuery<PurchaseOrder> query = new BmobQuery<>();
        if (mOrderState == 3) {
            bql = "select * from PurchaseOrder where providername = ? and orderDate = ? and orderState >= ?";
        } else {
            bql = "select * from PurchaseOrder where providername = ? and orderDate = ? and orderState = ?";
        }
        query.setSQL(bql);
        query.setPreparedParams(new Object[]{myUser.getUsername(), mOrderDate, mOrderState});
        query.doSQLQuery(new SQLQueryListener<PurchaseOrder>() {
            @Override
            public void done(final BmobQueryResult<PurchaseOrder> bmobQueryResult, BmobException e) {
                if (e == null) {
                    //getResults返回一个不变的集合，Collections.unmodifiableList（）。
                    final List<PurchaseOrder> list = bmobQueryResult.getResults();
                    if (list != null && list.size() > 0) {
                        mPurchaseOrderList = list;
                        mAdapter = new SupplyPriceDetailAdapter(mPurchaseOrderList, mOrderState);
                        mRecyclerView.setAdapter(mAdapter);
                        mSum = getOrdersPriceSum(mPurchaseOrderList);
                        mPriceSum.setText("小计：" + mSum + " ");
                    }else{
                        mPriceSum.setText("没有商品被确认！");
                    }

                }
            }
        });
//        /*
//        组合查询
//         */
//        List<BmobQuery<PurchaseOrder>> and = new ArrayList<>();
//        BmobQuery<PurchaseOrder> queryUser = new BmobQuery<>();
//        queryUser.addWhereEqualTo("provider", myUser);
//        and.add(queryUser);
//        //订单日期
//        BmobQuery<PurchaseOrder> queryDate = new BmobQuery<>();
//        queryDate.addWhereEqualTo("orderDate", mOrderDate);
//        and.add(queryDate);
//        //订单状态
//        BmobQuery<PurchaseOrder> queryState = new BmobQuery<>();
//        if (state ==1 || state ==2) {//1为未验货状态，2为验货
//            queryState.addWhereEqualTo("orderState", state);
//        } else {//订单如果是3为供货商已确认，是4时为已录入。
//            queryState.addWhereGreaterThanOrEqualTo("orderState", state);
//        }
//        and.add(queryState);
//
//
//        BmobQuery<PurchaseOrder> query = new BmobQuery<>();
//        query.and(and);
//        query.order("category");
//        query.findObjects(new FindListener<PurchaseOrder>() {
//            @Override
//            public void done(final List<PurchaseOrder> list, BmobException e) {
//                if (e == null) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Message msg = new Message();
//                            msg.what = SUPPLY_ORDER;
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("orders", (Serializable) list);
//                            msg.setData(bundle);
//                            handler.sendMessage(msg);
//                        }
//                    }).start();
//                }
//            }
//        });
    }
//
//    public static final int SUPPLY_ORDER = 0;
//    public Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case SUPPLY_ORDER:
//                    mPurchaseOrderList = (List<PurchaseOrder>) msg.getData().getSerializable("orders");
//                    mAdapter = new SupplyPriceDetailAdapter(mPurchaseOrderList, mOrderState);
//                    mRecyclerView.setAdapter(mAdapter);
//                    mSum = getOrdersPriceSum(mPurchaseOrderList);
//                    mPriceSum.setText("小计：" + mSum + " ");
//                    break;
//            }
//        }
//    };

    /**
     * 计算订单金额小计
     */
    public Float getOrdersPriceSum(List<PurchaseOrder> list) {
        Float sum = 0.0f;

            for (PurchaseOrder order : list) {
                sum = sum + order.getActualNum() * order.getPrice();
            }
            return (float) (Math.round(sum * 100)) / 100;

    }

    /**
     * 每次滑动都会调用setUserVisibleHint()这个方法，进行预加载后
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mOrderState == 1) {
            getPurchaseOrderList(1);
        } else {
            if (mOrderState == 2) {
                getPurchaseOrderList(2);
            } else {
                getPurchaseOrderList(3);
            }

        }


    }
}
