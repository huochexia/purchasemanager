package com.hbsx.purordermanage.Other;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbsx.purordermanage.Other.Adapter.OtherOrderAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

/**
 * 验货清单Fragment，一种是未验货，一种是已验货，已验货的在未录入之前可删除变成未验货
 * Created by Administrator on 2017/2/24 0024.
 */

public class OrderFragment extends Fragment {
    public static final String ORDER_STATE = "state";
    public static final String ORDER_DATE = "date";
    private Button mJisuanButton,mSaveButton;
    private TextView itemSum;

    private RelativeLayout mPriceLayout, mActualNumLayout, mPurchaseNumLayout;

    private RecyclerView mRecyclerView;
    private List<PurchaseOrder> mPurchaseOrderList;
    private OtherOrderAdapter mAdapter;

    //查询日期,如果是当天，则为提交功能，仅显示价格表头，显示提交按钮，屏敝小计；
    // 如果不是当天则显示实数和价格表头，以及小计屏敝提交按钮
    private String mProvider;
    private String mOrderDate;
    private int mOrderState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mOrderState = bundle.getInt(ORDER_STATE);
            mOrderDate = bundle.getString(ORDER_DATE);
            mProvider = bundle.getString("provider");
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_actual_num, container, false);
        initView(view);
        getPurchaseOrderList(mOrderState);

        return view;
    }

    /**
     * 初始化视图
     */
    private void initView(View view) {
        mJisuanButton = (Button) view.findViewById(R.id.jisuan_btn);
        mJisuanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemSum.setText(getOrdersPriceSum(mPurchaseOrderList)+"");
            }
        });
        itemSum = (TextView) view.findViewById(R.id.item_total);

        mSaveButton = (Button) view.findViewById(R.id.save_btn);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BmobBatch().updateBatch(BeanToBmobObject(mPurchaseOrderList))
                        .doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> list, BmobException e) {
                            Toast toast = Toast.makeText(getContext(), "", Toast.LENGTH_LONG);// 显示时间也可以是数字
                            toast.setGravity(Gravity.CENTER, 0, 0);// 最上方显示
                            LinearLayout toastLayout = (LinearLayout) toast.getView();
                            ImageView imageView = new ImageView(getContext());
                        if (e != null) {
                            toast.setText("提交失败");
                            imageView.setImageResource(R.drawable.error);
                            toastLayout.addView(imageView, 0);// 0 图片在文字的上方 ， 1 图片在文字的下方
                            toast.show();
                        } else {
                            toast.setText("提交成功");
                            imageView.setImageResource(R.drawable.sucess);
                            toastLayout.addView(imageView, 0);// 0 图片在文字的上方 ， 1 图片在文字的下方
                            toast.show();
                        }


                    }
                });

            }
        });

        mActualNumLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_header_actualnum);
        mActualNumLayout.setVisibility(View.VISIBLE);

        mPriceLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_header_price);
        mPriceLayout.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.examine_actual_number_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
    }

    /**
     * 获取订单列表
     */
    private void getPurchaseOrderList(Integer state) {

        /**
         * Bmob组合查询方法
         */
        List<BmobQuery<PurchaseOrder>> and = new ArrayList<>();
        //用户
        BmobQuery<PurchaseOrder> queryUser = new BmobQuery<>();
        queryUser.addWhereEqualTo("providername", mProvider);
        and.add(queryUser);
        //订单日期
        BmobQuery<PurchaseOrder> queryDate = new BmobQuery<>();
        queryDate.addWhereEqualTo("orderDate", mOrderDate);
        and.add(queryDate);
        //订单状态
        BmobQuery<PurchaseOrder> queryState = new BmobQuery<>();
        queryState.addWhereGreaterThanOrEqualTo("orderState", state);
        and.add(queryState);

        BmobQuery<PurchaseOrder> query = new BmobQuery<>();
        query.and(and);
        query.order("category");
        query.findObjects(new FindListener<PurchaseOrder>() {
            @Override
            public void done(final List<PurchaseOrder> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = SUPPLY_ORDER;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("orders", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
//        /**
//         * 基本语句查询方法
//         */
//        BmobQuery<PurchaseOrder> querySql = new BmobQuery<>();
//        String bsql = "";
//        if (mOrderState == 1) {
//            bsql = "select * from PurchaseOrder where providername = ? and orderDate = ? and orderState = ?";
//        } else {
//            bsql = "select * from PurchaseOrder where providername = ? and orderDate = ? and orderState >= ?";
//        }
//        querySql.setSQL(bsql);
//        querySql.setPreparedParams(new Object[]{mProvider.getUsername(), mOrderDate, mOrderState});
//        querySql.doSQLQuery(new SQLQueryListener<PurchaseOrder>() {
//            @Override
//            public void done(BmobQueryResult<PurchaseOrder> bmobQueryResult, BmobException e) {
//                if (e == null) {
//                    mPurchaseOrderList = bmobQueryResult.getResults();
//                    if (mPurchaseOrderList != null && mPurchaseOrderList.size() > 0) {
//                        mAdapter = new ProviderOrderAdapter(mPurchaseOrderList, mOrderState);
//                        mRecyclerView.setAdapter(mAdapter);
//                        itemNumber.setText(mPurchaseOrderList.size() + "项");
//                        if (mOrderState == 2) {
//                            //关联ItemTouchHelper和RecyclerView
//                            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
//                            mItemTouchHelper = new ItemTouchHelper(callback);
//                            mItemTouchHelper.attachToRecyclerView(mRecyclerView);
//                        }
//                    }
//                }
//            }
//        });
    }

    public static final int SUPPLY_ORDER = 0;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUPPLY_ORDER:
                    mPurchaseOrderList = (List<PurchaseOrder>) msg.getData().getSerializable("orders");
                    mAdapter = new OtherOrderAdapter(mPurchaseOrderList, mOrderState);
                    mRecyclerView.setAdapter(mAdapter);
                    break;
            }
        }
    };
    /**
     * 计算订单金额小计
     */
    public Float getOrdersPriceSum(List<PurchaseOrder> list) {
        Float sum = 0.0f;

        for (PurchaseOrder order : list) {
            sum = sum + order.getActualAgain() * order.getPrice();
        }
        return (float) (Math.round(sum * 100)) / 100;

    }

    /**
     * 构造一个Fragment,同时传入相应值
     *
     * @param state
     * @return
     */
    public static OrderFragment newInstance(int state, String orderDate, String provider) {
        Bundle bundle = new Bundle();
        bundle.putInt(ORDER_STATE, state);
        bundle.putString(ORDER_DATE, orderDate);
        bundle.putString("provider", provider);
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public List<BmobObject> BeanToBmobObject(List<PurchaseOrder> orders) {
        List<BmobObject> bmobObjects = new ArrayList<>();
        for (PurchaseOrder order : orders) {
//            order.setOrderState(3);
            BmobObject object = (BmobObject)order;
            bmobObjects.add(object);
        }
        return  bmobObjects;
    }

}
