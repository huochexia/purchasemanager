package com.hbsx.purordermanage.Other;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hbsx.purordermanage.Other.Adapter.ProviderAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.bean.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/3/25 0025.
 */

public class CalculateActivity extends BaseActivity {
    private Toolbar mToolbar;
    private Button mStartDateBtn;
    private Button mEndDateBtn;
    private Button mCalculateBtn;
    private Button mMeragerBtn;

    private TextView mStartText;
    private TextView mEndText;
    private TextView mOriginal;
    private TextView mDifference;
    AppCompatSpinner providerSpinner;
    List<User> mProviderList = new ArrayList<>();
    ProviderAdapter spinnerAdapter;
    User provider;

    List<PurchaseOrder> mList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("核对");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.left_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initView();
        initEnvent();
    }

    private void initView() {
        providerSpinner = (AppCompatSpinner) findViewById(R.id.select_spinner);
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
        mStartDateBtn = (Button) findViewById(R.id.btn_start_date);
        mEndDateBtn = (Button) findViewById(R.id.btn_end_date);
        mCalculateBtn = (Button) findViewById(R.id.btn_calculate_total);
        mMeragerBtn = (Button) findViewById(R.id.btn_merge);
        mMeragerBtn.setVisibility(View.VISIBLE);//合并按钮
        mStartText = (TextView) findViewById(R.id.tv_start_date);
        mEndText = (TextView) findViewById(R.id.tv_end_date);
        mOriginal = (TextView) findViewById(R.id.original_summer);
        mDifference = (TextView) findViewById(R.id.difference_summer);
    }

    /**
     * 初始化事件
     */
    public void initEnvent() {
        mStartDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(0);

            }
        });
        mEndDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(1);

            }
        });
        mCalculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOriginal.setText("");
                mDifference.setText("");
                getTotal(mStartText.getText().toString(), mEndText.getText().toString());
            }
        });
        //合并
        mMeragerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(CalculateActivity.this);
                builder1.setTitle("是否合并");
                builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         getMerge(mStartText.getText().toString(), mEndText.getText().toString());
                    }
                });
                builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder1.create().show();
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

    public static final int SUM = 1;
    public static final int USER_REQUEST = 2;
    public static final int MERGE = 3;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case USER_REQUEST:
                    List<User> users = (List<User>) msg.getData().getSerializable("user");
                    for (User user : users) {
                        if (user.getRole().getRoleName().equals("供货")) {
                            mProviderList.add(user);
                        }
                    }
                    spinnerAdapter = new ProviderAdapter(CalculateActivity.this, mProviderList);
                    providerSpinner.setAdapter(spinnerAdapter);
                    break;
                case SUM:
                    Float sumprice = 0.0f;
                    Float diffpice = 0.0f;
                    List<PurchaseOrder> list = (List<PurchaseOrder>) msg.getData().getSerializable("total");
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        PurchaseOrder psi = (PurchaseOrder) it.next();
                        sumprice = sumprice + psi.getActualNum() * psi.getPrice();
                        diffpice = diffpice + (psi.getActualAgain() - psi.getActualNum()) * psi.getPrice();

                    }
                    Float sum = (float) (Math.round(sumprice * 100)) / 100;
                    mOriginal.setText(sum + "");
                    Float diff = (float) (Math.round(diffpice * 100)) / 100;
                    mDifference.setText(diff + "");
                    break;
                case MERGE:
                    List<PurchaseOrder> mergeList = (List<PurchaseOrder>) msg.getData().getSerializable("merge");
                    Observable.from(mergeList)
                            .subscribeOn(Schedulers.io())
                            .filter(new Func1<PurchaseOrder, Boolean>() {
                                @Override
                                public Boolean call(PurchaseOrder purchaseOrder) {
                                    return !purchaseOrder.getActualNum().equals(purchaseOrder.getActualAgain());
                                }
                            })
                            .subscribe(new Action1<PurchaseOrder>() {
                                @Override
                                public void call(PurchaseOrder order) {
                                    String objectId = order.getObjectId();
                                    order.setActualNum(order.getActualAgain());
                                    float f =order.getActualAgain();
                                    int i = (int)f;
                                    order.setPurchaseNum((float) i);
                                    order.update(objectId, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {

                                        }
                                    });
                                }
                            });
                    break;

            }
        }
    };

    /**
     * 获取日期数据
     */
    private void setDate(final int i) {

        //通过自定义控件AlertDialog实现
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = (LinearLayout) getLayoutInflater().inflate(R.layout.date_picker, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        //设置日期简略显示 否则详细显示 包括:星期\周
        datePicker.setCalendarViewShown(false);

        //设置date布局
        builder.setView(view);
        builder.setTitle("设置日期信息");
        builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //日期格式
                StringBuffer sb = new StringBuffer();
                sb.append(String.format("%d-%02d-%02d",
                        datePicker.getYear(),
                        datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth()));
                switch (i) {
                    case 0:
                        mStartText.setText(sb);
                        break;
                    case 1:
                        mEndText.setText(sb);
                }
                dialog.cancel();

            }
        });
        builder.setNegativeButton("取  消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();

    }

    /**
     * 计算两个日期间商品金额合计
     *
     * @param
     * @return
     */
    public void getTotal(String start, String end) {
        List<BmobQuery<PurchaseOrder>> and = new ArrayList<>();

        BmobQuery<PurchaseOrder> queryUser = new BmobQuery<>();
        queryUser.addWhereEqualTo("providername", provider.getUsername());
        and.add(queryUser);

        BmobQuery<PurchaseOrder> startQ = new BmobQuery<>();
        startQ.addWhereGreaterThanOrEqualTo("orderDate", start);
        and.add(startQ);

        BmobQuery<PurchaseOrder> endQ = new BmobQuery<>();
        endQ.addWhereLessThanOrEqualTo("orderDate", end);
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

    /**
     * 计算两个日期间商品金额合计
     *
     * @param
     * @return
     */
    public void getMerge(String start, String end) {
        List<BmobQuery<PurchaseOrder>> and = new ArrayList<>();

        BmobQuery<PurchaseOrder> queryUser = new BmobQuery<>();
        queryUser.addWhereEqualTo("providername", provider.getUsername());
        and.add(queryUser);

        BmobQuery<PurchaseOrder> startQ = new BmobQuery<>();
        startQ.addWhereGreaterThanOrEqualTo("orderDate", start);
        and.add(startQ);

        BmobQuery<PurchaseOrder> endQ = new BmobQuery<>();
        endQ.addWhereLessThanOrEqualTo("orderDate", end);
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
                            msg.what = MERGE;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("merge", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
    }
}
