package com.hbsx.purordermanage.Request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Request.adapter.ShoppingCartListAdapter;
import com.hbsx.purordermanage.bean.Commodity;
import com.hbsx.purordermanage.bean.RequestNote;
import com.hbsx.purordermanage.bean.ShoppingCart;
import com.hbsx.purordermanage.bean.User;
import com.hbsx.purordermanage.utils.SimpleItemTouchHelperCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;

/**
 * 购物车Activity
 * Created by Administrator on 2017/2/10 0010.
 */

public class ShoppingCartActivity extends BaseActivity  {

    private Toolbar toolbar;
    public TextView priceTotal;
    private List<ShoppingCart> orders;//生成订单中商品列表

    private List<BmobObject> mCommodityList;//购物车中商品列表
    private RecyclerView mCommodityView;
    private ShoppingCartListAdapter mAdapter;
    private FloatingActionButton mFB;
    private ItemTouchHelper mItemTouchHelper;

    RelativeLayout mPurchaseNumLayout;
    RelativeLayout mPurchasePriceLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        EventBus.getDefault().register(this);
        //初始化工具栏
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("购物车");
        toolbar.setNavigationIcon(R.mipmap.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        //购物车中商品总价值
        priceTotal = (TextView) findViewById(R.id.price_total);
        //屏敝增加商品的浮动按钮功能
        mFB = (FloatingActionButton) findViewById(R.id.btn_add_commodity);
        mFB.setVisibility(View.GONE);
        //显示商品列表上表头的订量
        mPurchaseNumLayout = (RelativeLayout) findViewById(R.id.commodity_item_header_dingliang);
        mPurchaseNumLayout.setVisibility(View.VISIBLE);
        mPurchasePriceLayout = (RelativeLayout) findViewById(R.id.commodity_item_header_price);
        mPurchasePriceLayout.setVisibility(View.VISIBLE);
        mCommodityList = new ArrayList<>();
        orders = new ArrayList<>();
        mCommodityView = (RecyclerView) findViewById(R.id.commodity_detail_list);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mCommodityView.setLayoutManager(lm);
//        getCommodityList();

        orders = DataSupport.findAll(ShoppingCart.class, true);
        priceTotal.setText(computeTotal()+"");
        mAdapter = new ShoppingCartListAdapter(orders);
        mCommodityView.setAdapter(mAdapter);
        toolbar.setSubtitle("共"+orders.size()+"项商品");

        //关联ItemTouchHelper和RecyclerView,用于侧滑删除
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mCommodityView);

    }
    /**
     * 从购物车中获取物品数据，并转换成临时商品对象,因为向云端数据库存储时需要是BmobObject对象
     *
     */
    private void getCommodityList() {

         List<ShoppingCart> goods = DataSupport.findAll(ShoppingCart.class, true);
        if (goods.size() > 0) {

            for (ShoppingCart good : goods) {
                Commodity commodity = RequestNoteMainActivity.getCommodity(good);
                mCommodityList.add(commodity);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commit_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.commit_shopping_btn:
                saveRequestNote(getRequestNote());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public Float computeTotal() {
        Float total = 0.0f;
        for (ShoppingCart sc : orders) {
            total =total+ sc.getPurchaseNum()*sc.getPurchasePrice();
        }
        return total;
    }
    /**
     * 保存报货单
     * @param list
     */
    private void saveRequestNote(List<BmobObject> list) {

        new BmobBatch().insertBatch(list).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if (e == null) {
                    //提交成功后，要清空购物车，返回主界面
                    DataSupport.deleteAll(ShoppingCart.class,"purchaseNum <> ?","0.0");
                    toast("提交成功",true);
                    mCommodityList.clear();
                    orders.clear();
                    getCommodityList();
                    mAdapter.notifyDataSetChanged();
                } else {
                    toast("提交失败",false);
                }
            }
        });
    }

    /**
     * 生成报货单
     * @return
     */
    @NonNull
    public List<BmobObject> getRequestNote() {
        List<BmobObject> objects = new ArrayList<>();
        //获取购物车中的物品
        List<ShoppingCart> goodList = DataSupport.findAll(ShoppingCart.class, true);
        //获取当前用户
        User myUser = BmobUser.getCurrentUser(User.class);
        //形成报货单号
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        final String ordernumber = "BH" + myUser.getUsername() + str;

        for (ShoppingCart good : goodList) {
            if(good.getPurchaseNum()!=0){
                RequestNote order = new RequestNote();
                order.setPerson(myUser.getUsername());//报货人
                order.setOrderNumber(ordernumber);//订单编号
                order.setCategory(good.getCategory());//商品类别
                order.setCommodityName(good.getCommodityName());//商品名称
                order.setUnit(good.getUnit().getUnitName());//商品单位
                order.setPurchaseNum(good.getPurchaseNum());//订购数量
                order.setOrderState(0);//0为报货初始状态，1为报货经过汇总处理了；2为报货汇总值
                objects.add(order);
            }
        }
        return objects;
    }

    /**
     * 启动本活动的静态方法
     *
     * @param mContext
     */
    public static void actionStart(Context mContext) {
        Intent intent = new Intent(mContext, ShoppingCartActivity.class);
        mContext.startActivity(intent);
    }
    @Subscribe
    public void onEventMainThread(TotalEvent event) {
        priceTotal.setText(event.getTotal()+"");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
