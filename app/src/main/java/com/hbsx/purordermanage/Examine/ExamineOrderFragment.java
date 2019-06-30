package com.hbsx.purordermanage.Examine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import com.hbsx.purordermanage.Examine.Adapter.ExamineOrderAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.Commodity;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.utils.SimpleItemTouchHelperCallback;

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
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 验货清单Fragment，一种是未验货，一种是已验货，已验货的在未录入之前可删除变成未验货，重新验货
 * 获得商品清单后，根据供货商所报价格，更新数据库中商品单价
 * Created by Administrator on 2017/2/24 0024.
 */

public class ExamineOrderFragment extends Fragment {
    public static final String ORDER_STATE = "state";
    public static final String ORDER_DATE = "date";
    private Button mCommitActualNum;
    private TextView itemNumber;

    private RelativeLayout mPriceLayout, mActualNumLayout, mPurchaseNumLayout;

    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView mRecyclerView;
    private List<PurchaseOrder> mPurchaseOrderList;
    private ExamineOrderAdapter mAdapter;

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
        View view = inflater.inflate(R.layout.fragment_examine_actual_num, container, false);
        initView(view);
        getPurchaseOrderList(mOrderState);
        return view;
    }

    /**
     * 初始化视图
     */
    private void initView(View view) {
        itemNumber = (TextView) view.findViewById(R.id.item_number);
        mCommitActualNum = (Button) view.findViewById(R.id.ok_actual_number_btn);
        mCommitActualNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrderState(mPurchaseOrderList);
            }
        });
        if (mOrderState == 2) {
            mCommitActualNum.setVisibility(View.GONE);
        }


        mActualNumLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_header_actualnum);
        mActualNumLayout.setVisibility(View.VISIBLE);

        mPriceLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_header_price);
        mPriceLayout.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.examine_actual_number_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
    }

    private void saveOrderState(final List<PurchaseOrder> list1) {
        final List<BmobObject> objects = new ArrayList<>();
        for (final PurchaseOrder order : list1) {
            if (order.getActualNum() != 0 && order.getOrderState() < 3) {//实际数量不等于0且未确认或未录入
                order.setOrderState(2);//将状态改为2，已验货。
                //将当前单价更新到商品库中
//                updateCommodityPrice(order);
                objects.add(order);
            }
        }
        //从列表中移走已经改变状态的商品。如果使用基本语句查询，则此方法会出现java.lang.UnsupportedOperationException
        //因为查询结果是不可修改列表
        mPurchaseOrderList.removeAll(objects);
        mAdapter.notifyDataSetChanged();
        // 保存已经修改状态的商品到数据库
        new BmobBatch().updateBatch(objects).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                Toast toast = Toast.makeText(getContext(), null, Toast.LENGTH_LONG);// 显示时间也可以是数字
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
        if (mOrderState == 1) {
            queryState.addWhereEqualTo("orderState", state);
        } else {
            queryState.addWhereGreaterThanOrEqualTo("orderState", state);
        }
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
    public  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUPPLY_ORDER:
                    mPurchaseOrderList = (List<PurchaseOrder>) msg.getData().getSerializable("orders");
                    mAdapter = new ExamineOrderAdapter(mPurchaseOrderList, mOrderState);
                    mRecyclerView.setAdapter(mAdapter);
                    if (mOrderState == 2) {
                        //关联ItemTouchHelper和RecyclerView
                        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
                        mItemTouchHelper = new ItemTouchHelper(callback);
                        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
                    }
                    //利用RxJava，过滤单价掉单价为0的，更新数据库中商品单价
                    Observable.from(mPurchaseOrderList)
                            .subscribeOn(Schedulers.io())
                            .filter(new Func1<PurchaseOrder, Boolean>() {
                                @Override
                                public Boolean call(PurchaseOrder purchaseOrder) {
                                    return purchaseOrder.getPrice()>0;
                                }
                            })
                            .subscribe(new Action1<PurchaseOrder>() {
                                @Override
                                public void call(PurchaseOrder purchaseOrder) {
                                    updateCommodityPrice(purchaseOrder);
                                }
                            });
                    break;
            }
        }
    };
    /**
     * 修改商品的单价
     */
    public void updateCommodityPrice(final PurchaseOrder order) {
        BmobQuery<Commodity> query = new BmobQuery<>();
        String name = order.getCommodityName();
        query.addWhereEqualTo("commName", name);
        query.findObjects(new FindListener<Commodity>() {
            @Override
            public void done(List<Commodity> list, BmobException e) {
                if (e == null) {
                    Commodity commodity = new Commodity();
                    Float price = order.getPrice();
                    commodity.setValue("price", price);
                    String objectId = list.get(0).getObjectId();
                    commodity.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }
                    });
                }
            }
        });

    }

    /**
     * 构造一个Fragment,同时传入相应值
     *
     * @param state
     * @return
     */
    public static ExamineOrderFragment newInstance(int state, String orderDate, String provider) {
        Bundle bundle = new Bundle();
        bundle.putInt(ORDER_STATE, state);
        bundle.putString(ORDER_DATE, orderDate);
        bundle.putString("provider", provider);
        ExamineOrderFragment fragment = new ExamineOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
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
        }
        if (mOrderState == 2) {
            getPurchaseOrderList(2);
        }

    }
}
