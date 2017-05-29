package com.hbsx.purordermanage.InputData;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.hbsx.purordermanage.InputData.adapter.InputOrderAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.bean.User;

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
 * 供货清单Fragment，一种是订货单，一种是验货单
 * Created by Administrator on 2017/2/24 0024.
 */

public class LookAndInputOrderFragment extends Fragment {
    public static final String ORDER_STATE = "state";
    public static final String ORDER_DATE = "date";
    public static final List<PurchaseOrder> mSelectedOrders = new ArrayList<>();

    private TextView mItemNumHeader;
    //未录入
    private Button mConfirmBtn;//用于未录入时确定已录入选项
    private static TextView mSelectedItemNums;//用于显示将要录入的已选择录入项目数
    private static TextView mSelectedTotal;//用于显示将要录入的已选择项金额
    //已录入
//    private Button mRefreshBtn;//用于在刷新已录入
    private TextView mItemNums;//用于显示已录入项目数
    private TextView mPriceTotal;//用于计算已录入订单总计

    private RecyclerView mRecyclerView;
    private List<PurchaseOrder> mPurchaseOrderList;
    private InputOrderAdapter mAdapter;

    //查询日期,如果是当天，则为提交功能，仅显示价格表头，显示提交按钮，屏敝小计；
    // 如果不是当天则显示实数和价格表头，以及小计屏敝提交按钮
    private int mOrderState;
    private String mOrderDate;
    private String provider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mOrderState = bundle.getInt(ORDER_STATE);
            mOrderDate = bundle.getString(ORDER_DATE);
            provider = bundle.getString("provider");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_order, container, false);
        initView(view);
        getPurchaseOrderList(mOrderState);
        return view;
    }

    private void initView(View view) {
        RelativeLayout actulNumLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_header_actualnum);
        actulNumLayout.setVisibility(View.VISIBLE);
        RelativeLayout priceLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_header_price);
        priceLayout.setVisibility(View.VISIBLE);
        //状态为2，表示未录入；状态为3，表示已录入完毕
        switch (mOrderState) {
            case 3:
                // 表头选择项
                RelativeLayout selected = (RelativeLayout) view.findViewById(R.id.commodity_item_header_select);
                selected.setVisibility(View.VISIBLE);
                //确认按钮
                mConfirmBtn = (Button) view.findViewById(R.id.input_confirm_btn);
                mConfirmBtn.setVisibility(View.VISIBLE);
                mConfirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<BmobObject> objects = new ArrayList<BmobObject>();
                        for (PurchaseOrder order : mSelectedOrders) {
                            order.setOrderState(4);
                            objects.add(order);
                        }
                        mPurchaseOrderList.removeAll(mSelectedOrders);
                        mAdapter.notifyDataSetChanged();
                        mAdapter.RefreshMap();
                        clearSelectedOrderList();
                        mSelectedItemNums.setText("0");
                        mSelectedTotal.setText("0");
                        new BmobBatch().updateBatch(objects).doBatch(new QueryListListener<BatchResult>() {
                            @Override
                            public void done(List<BatchResult> list, BmobException e) {
                                if (e != null) {
                                    Toast.makeText(getContext(), "失败！" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                //项目数目显示
                mItemNumHeader = (TextView) view.findViewById(R.id.input_item_num_header);
                mItemNumHeader.setVisibility(View.VISIBLE);
                mSelectedItemNums = (TextView) view.findViewById(R.id.input_item_num_text);
                mSelectedItemNums.setVisibility(View.VISIBLE);
                mSelectedTotal = (TextView) view.findViewById(R.id.input_price_total);
                break;
            case 4:
                //项目数目显示
                mItemNumHeader = (TextView) view.findViewById(R.id.input_item_num_header);
                mItemNumHeader.setVisibility(View.VISIBLE);
                mItemNums = (TextView) view.findViewById(R.id.input_item_num_text);
                mItemNums.setVisibility(View.VISIBLE);
                mPriceTotal = (TextView) view.findViewById(R.id.input_price_total);
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
    public static LookAndInputOrderFragment newInstance(int state, String orderDate, String provider) {
        Bundle bundle = new Bundle();
        bundle.putInt(ORDER_STATE, state);
        bundle.putString(ORDER_DATE, orderDate);
        bundle.putString("provider", provider);
        LookAndInputOrderFragment fragment = new LookAndInputOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 每次滑动都会调用setUserVisibleHint()这个方法，进行预加载后
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(mOrderState == 4){
            getPurchaseOrderList(4);
        }
    }

    /**
     * 获取订单列表
     */
    private void getPurchaseOrderList(int state) {
        List<BmobQuery<PurchaseOrder>> and = new ArrayList<>();
        //当前用户

        BmobQuery<PurchaseOrder> queryUser = new BmobQuery<>();
        queryUser.addWhereEqualTo("providername", provider);
        and.add(queryUser);
        //订单日期
        BmobQuery<PurchaseOrder> queryDate = new BmobQuery<>();
        queryDate.addWhereEqualTo("orderDate", mOrderDate);
        and.add(queryDate);
        //订单状态
        BmobQuery<PurchaseOrder> queryState = new BmobQuery<>();
        queryState.addWhereEqualTo("orderState", state);
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
    }

    public static final int SUPPLY_ORDER = 0;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUPPLY_ORDER:
                    mPurchaseOrderList = (List<PurchaseOrder>) msg.getData().getSerializable("orders");
                    mAdapter = new InputOrderAdapter(mPurchaseOrderList, mOrderState);
                    mRecyclerView.setAdapter(mAdapter);

                    if (mOrderState == 4) {//如果是已录入，则计算总额
                        mItemNums.setText(mPurchaseOrderList.size()+"");
                        Float mTotal = getOrdersPriceSum(mPurchaseOrderList);
                        mPriceTotal.setText(mTotal + " ");
                    }

                    break;
            }
        }
    };

    /**
     * 增加选择订单，同时计算已选择项目数及金额
     *
     * @param order
     */
    public static void addSelectedOrders(PurchaseOrder order) {
        if(!mSelectedOrders.contains(order)){
            mSelectedOrders.add(order);
        }
        Float total = getOrdersPriceSum(mSelectedOrders);
        mSelectedTotal.setText(total + "");
        mSelectedItemNums.setText(mSelectedOrders.size() + "");
    }

    /**
     * 减少选择订单，同时计算已选择项目数及金额
     *
     * @param order
     */
    public static void deleteSelectedOrders(PurchaseOrder order) {
        mSelectedOrders.remove(order);
        mSelectedItemNums.setText(mSelectedOrders.size() + "");
        mSelectedTotal.setText(getOrdersPriceSum(mSelectedOrders) + "");
    }

    /**
     * 清空已选择列表
     */
    public static void clearSelectedOrderList() {
        mSelectedOrders.clear();
    }

    /**
     * 计算订单金额小计
     */
    public static Float getOrdersPriceSum(List<PurchaseOrder> list) {
        Float sum = 0.0f;
        for (PurchaseOrder order : list) {
            sum = sum + order.getActualNum() * order.getPrice();
        }
        return  (float)(Math.round(sum*100))/100;
    }
}
