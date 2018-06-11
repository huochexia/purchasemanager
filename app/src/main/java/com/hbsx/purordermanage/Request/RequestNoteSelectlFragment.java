package com.hbsx.purordermanage.Request;

import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Manager.adapter.UnitSpinnerAdapter;
import com.hbsx.purordermanage.Request.adapter.RequestNoteSelectAdapter;
import com.hbsx.purordermanage.bean.Commodity;
import com.hbsx.purordermanage.bean.CommodityCategory;
import com.hbsx.purordermanage.bean.ShoppingCart;
import com.hbsx.purordermanage.bean.UnitOfMeasurement;
import com.hbsx.purordermanage.utils.SimpleItemTouchHelperCallback;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 商品管理与报货管理共用
 * Created by Administrator on 2017/1/14 0014.
 */

public class RequestNoteSelectlFragment extends Fragment {


    private FloatingActionButton mAddCommodityInputCartBtn;
    private Toolbar toolbar;

    //商品列表
    private List<Commodity> commodityList = new ArrayList<>();
    private RecyclerView mListView;
    private RequestNoteSelectAdapter mAdapter;
    private CommodityCategory mCategory;//商品种类名


    //单位列表
    private List<UnitOfMeasurement> unitList = new ArrayList<>();
    private Spinner mUnitSpinner;
    private UnitSpinnerAdapter mSpinnerAdapter;
    private UnitOfMeasurement mUnit;//用于存储用户选择结果

    private TableLayout mDialogView;//对话框视图，用于获取视图中数据

    private RelativeLayout mCheckBox;//用于显示复选框标题
    private RelativeLayout mPriceLayout;//显示单价表头

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        mCategory = (CommodityCategory) bundle.getSerializable("category");
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commodity_detail, container, false);

        mListView = (RecyclerView) view.findViewById(R.id.commodity_detail_list);
        //获取Toolbar对象，将Toolbar标题栏设置为对应的值
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(mCategory.getCategoryName());
        toolbar.setNavigationIcon(R.mipmap.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        mCheckBox = (RelativeLayout) view.findViewById(R.id.commodity_item_header_select);
        mCheckBox.setVisibility(View.VISIBLE);
        mPriceLayout = (RelativeLayout) view.findViewById(R.id.commodity_item_header_price);
        mPriceLayout.setVisibility(View.VISIBLE);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(manager);
        getCommodityList();

        //获取浮动按钮并添加事件
        mAddCommodityInputCartBtn = (FloatingActionButton) view.findViewById(R.id.btn_add_commodity);
        mAddCommodityInputCartBtn.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.input_shopping_cart, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_commodity_menu:
                //获得对话框自定义视图对象
                mDialogView = (TableLayout) getActivity().getLayoutInflater()
                        .inflate(R.layout.dialog_add_commodity, null);
                //获得下拉列表对象
                mUnitSpinner = (Spinner) mDialogView.findViewById(R.id.add_commodity_form_unit_spinner);
                //从网络中获取数据
                getUnitList();
                //默认第一项被选中
                mUnitSpinner.setSelection(0, true);
                mUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        mUnit = (UnitOfMeasurement) mSpinnerAdapter.getItem(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                //创建对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(mCategory.getCategoryName() + "系列").setView(mDialogView);

                setPositiveButton(builder);
                setNegativeButton(builder).create().show();
                break;
            case R.id.add_shopping_cart:
                Map<Integer,Boolean> map = mAdapter.getMap();
                Iterator iterator=map.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry entry = (Map.Entry) iterator.next();
                    int key = (int) entry.getKey();
                    Boolean value = (Boolean) entry.getValue();
                    if(value==true){
                        ShoppingCart good = RequestNoteMainActivity.getShopping(commodityList.get(key));
                        good.save();
                    }
                }
                getActivity().finish();
                break;
        }
        return true;
    }

    /**
     * 利用handler 将从数据源中获取的数据传递给主线程变量
     */
    //传递的信息为单位列表内容
    public static final int UNIT_LIST = 1;
    //传递的信息为商品列表内容
    public static final int COMMODITY_LIST = 2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UNIT_LIST:
                    /**
                     * Bmob的查询是异步查询，数据还没有查询完，程序已经运行完，所以对通过获取网络数据
                     * 形成的UI必须放在handler中进行处理。
                     */
                    unitList = (List<UnitOfMeasurement>) msg.getData().getSerializable("groups");
                    while (unitList == null) {
                        Toast.makeText(getActivity(), "正在加载单位数据....", Toast.LENGTH_SHORT).show();
                    }
                    mSpinnerAdapter = new UnitSpinnerAdapter(getActivity(), unitList);
                    mUnitSpinner.setAdapter(mSpinnerAdapter);
                    break;
                case COMMODITY_LIST:
                    commodityList = (List<Commodity>) msg.getData().getSerializable("commodities");
                    while (commodityList == null) {
                        Toast.makeText(getActivity(), "正在加载单位数据....", Toast.LENGTH_SHORT).show();
                    }
                    //去掉已包含在购物车中的商品
                    List<ShoppingCart> selecteds = DataSupport.findAll(ShoppingCart.class);
                    for(ShoppingCart selected : selecteds){
                        for(Commodity commodity : commodityList){
                            if(commodity.getObjectId().equals(selected.getObjectId())){
                                commodityList.remove(commodity);
                                break;
                            }
                        }
                    }
                    mAdapter = new RequestNoteSelectAdapter(commodityList);
                    mListView.setAdapter(mAdapter);
                    //关联ItemTouchHelper和RecyclerView
                    ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
                    ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
                    mItemTouchHelper.attachToRecyclerView(mListView);
                    break;
            }
        }
    };

    /**
     * 从数据源中获得商品列表内容，查询策略为先从缓存中获取，如果缓存没有则从网络获取。
     */
    private void getCommodityList() {
        BmobQuery<Commodity> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        query.addWhereEqualTo("commCategory", mCategory);
        query.include("unit,commCategory");

        query.findObjects(new FindListener<Commodity>() {
            @Override
            public void done(final List<Commodity> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = COMMODITY_LIST;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("commodities", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 从数据源中获得单位列表内容，查询策略为先从缓存中获取，如果缓存没有则从网络获取。
     */
    private void getUnitList() {
        //从数据源中获得下拉列表内容
        BmobQuery<UnitOfMeasurement> query = new BmobQuery<UnitOfMeasurement>();
        //查询策略为先从网中获取，如果网络没有则从缓存获取
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        //执行查询
        query.findObjects(new FindListener<UnitOfMeasurement>() {
            @Override
            public void done(final List<UnitOfMeasurement> list, BmobException e) {
                if (e == null) {
                    //数据查询是网络异步线程，所以需要通过另一个子线程将数据传递给主线程
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = UNIT_LIST;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("groups", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();

                } else {
                    Toast.makeText(getActivity(), "失败原因：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * 对话框确认事件方法
     */
    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {
        return builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                final Commodity comm = new Commodity();
                EditText commName = (EditText) mDialogView.findViewById(R.id.add_commodity_form_name);
                comm.setCommName(commName.getText().toString());
                comm.setCommCategory(mCategory);
                comm.setUnit(mUnit);

                comm.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            commodityList.add(comm);
                            mAdapter.notifyItemInserted(commodityList.size() - 1);
                            Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "添加失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }

    /**
     * 对话框取消按事件
     */
    private AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder) {
        return builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }
}
