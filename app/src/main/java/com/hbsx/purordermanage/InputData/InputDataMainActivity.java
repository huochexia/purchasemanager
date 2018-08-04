package com.hbsx.purordermanage.InputData;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.hbsx.purordermanage.InputData.adapter.InputOrderAdapter;
import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.utils.Utility;
import com.hbsx.purordermanage.utils.calendar.CalendarAdapter;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

/**
 * 录入数据功能应用主程序，日历显示activity，点击日期进入供货商选择界面
 */
public class InputDataMainActivity extends BaseActivity implements OnClickListener {


    private Toolbar toolbar;
    private Button refreshBtn;
    private GestureDetector gestureDetector = null;
    private CalendarAdapter calV = null;
    private GridView gridView = null;
    private TextView topText = null;
    private static int jumpMonth = 0; // 每次滑动，增加或减去默认（即显示当前月）
    private static int jumpYear = 0; // (即当前年)
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private String currentDate = "";

    private TextView nextMonth; // 下一月文本框
    private TextView preMonth; // 上一月文本框

    public InputDataMainActivity() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
        currentDate = sdf.format(date); // 当期日期
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("录入系统");
        setSupportActionBar(toolbar);
        //强制确认按钮
        refreshBtn = findViewById(R.id.btn_refresh_force);
        refreshBtn.setOnClickListener(this);
        try {
            refreshBtn.setEnabled(Utility.compareTime("10:30:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        gestureDetector = new GestureDetector(this, new MyGestureListener());
        calV = new CalendarAdapter(this, getResources(), new Date(), jumpMonth, jumpYear,
                year_c, month_c, day_c);
        addGridView();//创一个GridView实例
        gridView.setAdapter(calV);

        topText = (TextView) findViewById(R.id.tv_month);
        addTextToTopTextView(topText);
        nextMonth = (TextView) this.findViewById(R.id.right_img);
        preMonth = (TextView) this.findViewById(R.id.left_img);
        nextMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addGridView(); // 添加一个gridView
                jumpMonth++; // 下一个月

                calV = new CalendarAdapter(InputDataMainActivity.this,
                        getResources(), new Date(), jumpMonth, jumpYear, year_c, month_c,
                        day_c);
                gridView.setAdapter(calV);
                addTextToTopTextView(topText);
            }
        });

        preMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addGridView(); // 添加gridView
                jumpMonth--; // 上一个月

                calV = new CalendarAdapter(InputDataMainActivity.this,
                        getResources(), new Date(), jumpMonth, jumpYear, year_c, month_c,
                        day_c);
                gridView.setAdapter(calV);
                // gvFlag++;
                addTextToTopTextView(topText);
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return this.gestureDetector.onTouchEvent(event);
    }

    // 添加头部的年哪月等信息
    public void addTextToTopTextView(TextView view) {
        StringBuffer textDate = new StringBuffer();
        textDate.append(calV.getShowYear()).append("年")
                .append(calV.getShowMonth()).append("月").append("\t");
        view.setText(textDate);
        view.setTextColor(Color.WHITE);
        view.setTypeface(Typeface.DEFAULT_BOLD);
    }

    // 添加gridview
    private void addGridView() {

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                // 将Gridview中的触摸事件回传给gestureDetector
                return InputDataMainActivity.this.gestureDetector
                        .onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            // gridView中的每一个item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // 点击任何item，得到这个item的日程排除点击的是周日到周六点击不响应)
                int startPosition = calV.getStartPositon();
                int endPosition = calV.getEndPosition();
                if (startPosition <= position + 7
                        && position <= endPosition - 7) {
                    String scheduleDay = calV.getDateByClickItem(position)
                            .split("\\.")[0]; // 这一天的阳历
                    // String scheduleLunarDay =
                    // calV.getDateByClickItem(position).split("\\.")[1];
                    // //这一天的阴历
                    String scheduleYear = calV.getShowYear();
                    String scheduleMonth = calV.getShowMonth();
                    if (Integer.parseInt(scheduleMonth) < 10) {
                        scheduleMonth = "0" + scheduleMonth;
                    }
                    if (Integer.parseInt(scheduleDay) < 10) {
                        scheduleDay = "0" + scheduleDay;
                    }
                    String purchaseDate = scheduleYear + "-" + scheduleMonth + "-"
                            + scheduleDay;
                    /**
                     * 启动每日订单活动
                     */
                    SelectProviderActivity.actionStart(InputDataMainActivity.this, purchaseDate);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        getPurchaseOrderList(2);
    }

    /**
     * 获取订单列表
     */
    private void getPurchaseOrderList(int state) {
        List<BmobQuery<PurchaseOrder>> and = new ArrayList<>();
        //订单日期
        BmobQuery<PurchaseOrder> queryDate = new BmobQuery<>();
        System.out.println(Utility.getDateString(Utility.getForwardDate(1)));
        queryDate.addWhereEqualTo("orderDate", Utility.getDateString(Utility.getForwardDate(1)));
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
    private List<PurchaseOrder> mPurchaseOrderList;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUPPLY_ORDER:
                    mPurchaseOrderList = (List<PurchaseOrder>) msg.getData().getSerializable("orders");
                    if (mPurchaseOrderList.size() > 0) {
                        List<BmobObject> objects = new ArrayList<>();
                        for (PurchaseOrder order : mPurchaseOrderList) {
                            order.setOrderState(3);
                            objects.add(order);
                        }
                        new BmobBatch().updateBatch(objects).doBatch(new QueryListListener<BatchResult>() {
                            @Override
                            public void done(List<BatchResult> list, BmobException e) {
                                if (e == null) {
                                    Toast.makeText(InputDataMainActivity.this, "成功确认" + list.size() + "项订单！"
                                            , Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(InputDataMainActivity.this, "确认失败" + e.getMessage()
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(InputDataMainActivity.this, "没有需要确认的订单！"
                                , Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    /**
     * 内部类
     */
    private class MyGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        // 用户（轻触触摸屏后）松开，由�?��1个MotionEvent ACTION_UP触发
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        // 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        // 用户按下触摸屏，并拖动，�?个MotionEvent ACTION_DOWN,
        // 多个ACTION_MOVE触发
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        // 用户按下触摸屏�?快�?移动后松�?���?个MotionEvent ACTION_DOWN,
        // 多个ACTION_MOVE, 1个ACTION_UP触发
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            // int gvFlag = 0; //每次添加gridview到viewflipper中时给的标记
            if (e1.getX() - e2.getX() > 120) {
                // 向左滑动
                nextMonth.performClick();
                return true;
            } else if (e1.getX() - e2.getX() < -120) {
                // 向右滑动
                preMonth.performClick();
                return true;
            }
            return false;
        }

        // 用户轻触触摸屏，尚未松开或拖动，由一�?个MotionEvent ACTION_DOWN触发
        // 注意和onDown()的区别，强调的是没有松开或�?拖动的状�?		@Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        // 用户轻触触摸屏，�?个MotionEvent ACTION_DOWN触发
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        // 双击的第二下Touch down时触�?		@Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        // 双击的第二下Touch down和up都会触发，可用e.getAction()区分
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        // 点击�?��稍微慢点�?不滑�?Touch Up:
        // onDown->onShowPress->onSingleTapUp->onSingleTapConfirmed
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

    }

    /**
     * 启动本活动
     *
     * @param mContext
     */
    public static void actionStart(Context mContext) {
        Intent intent = new Intent(mContext, InputDataMainActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 实现再按一次返回键退出程序
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 3000) {
                toast("再按一次退出程序！", true);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}