package com.hbsx.purordermanage.Request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hbsx.purordermanage.ActivityCollector;
import com.hbsx.purordermanage.BaseActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Manager.adapter.CommoCateAdapter;
import com.hbsx.purordermanage.bean.Commodity;
import com.hbsx.purordermanage.bean.CommodityCategory;
import com.hbsx.purordermanage.bean.ShoppingCart;
import com.hbsx.purordermanage.bean.Unit;
import com.hbsx.purordermanage.bean.UnitOfMeasurement;
import com.hbsx.purordermanage.Manager.CommodityCategoryFragment;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

/**
 * 报货活动：该活动主要负责处理报货员根据菜谱审报预采购商品及数量
 * Created by Administrator on 2017/1/23 0023.
 */

public class RequestNoteMainActivity extends BaseActivity {
    //查询今天，或当月标志
    public static final int SEARCH_WEEK= 1;
    public static final int SEARCH_TODAY = 0;

    private Toolbar mToolbar;
    private Button myShoppingCart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baohuo);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("选择购买商品");
        setSupportActionBar(mToolbar);

        myShoppingCart = (Button) findViewById(R.id.my_shopping_cart_btn);
        myShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestNoteMainActivity.this, ShoppingCartActivity.class);
                startActivity(intent);
            }
        });
        //初始化本地数据库工具
        LitePal.initialize(this);
        Connector.getDatabase();

        Bundle bundle = new Bundle();
        bundle.putInt("flag", CommoCateAdapter.COMMODITY_BAOHUO);
        CommodityCategoryFragment ccf = new CommodityCategoryFragment();
        ccf.setArguments(bundle);

        FragmentManager mFManager = getSupportFragmentManager();
        FragmentTransaction mFTransaction = mFManager.beginTransaction();
        mFTransaction.add(R.id.baohuo_fragment_container, ccf);
        mFTransaction.commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.request_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.search_today_note:
                RequestNoteNumSearchActivity.actionStart(this, RequestNoteMainActivity.SEARCH_TODAY);
                break;
            case R.id.search_threeday_note:
                RequestNoteNumSearchActivity.actionStart(this, RequestNoteMainActivity.SEARCH_WEEK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //启动本活动方法
    public static void actionStart(Context mContext) {
        Intent intent = new Intent(mContext, RequestNoteMainActivity.class);
        mContext.startActivity(intent);
    }


    /**
     * Bmob数据类转换本地数据库类,因为单位在本地数据库中是另一个关联表，所以在转换成本地商品时，
     * 先查找是否有这个单位，如果有，则将其赋值给变量；如果没有则先存入，再赋值。
     */
    public static ShoppingCart getShopping(Commodity commodity) {
        ShoppingCart good = new ShoppingCart();
        good.setObjectId(commodity.getObjectId());
        String category = commodity.getCommCategory().getCategoryName();
        good.setCategory(category);
        good.setCommodityName(commodity.getCommName());
        List<Unit> units = DataSupport.where("objectId = ?", commodity.getUnit().getObjectId()).find(Unit.class);
        if (units.size() > 0) {
            good.setUnit(units.get(0));
        } else {
            Unit unit = new Unit();
            unit.setObjectId(commodity.getUnit().getObjectId());
            unit.setUnitName(commodity.getUnit().getUnitName());
            unit.save();
            good.setUnit(unit);
        }
        return good;
    }

    /**
     * 本地数据库类转换Bmob数据类
     */
    @NonNull
    public static Commodity getCommodity(ShoppingCart good) {

        Commodity commodity = new Commodity();
        commodity.setObjectId(good.getObjectId());
        commodity.setCommName(good.getCommodityName());
        CommodityCategory category = new CommodityCategory();
        category.setCategoryName(good.getCategory());
        UnitOfMeasurement unit = new UnitOfMeasurement();
        unit.setObjectId(good.getUnit().getObjectId());
        unit.setUnitName(good.getUnit().getUnitName());
        commodity.setUnit(unit);
        commodity.setPurchaseNum(good.getPurchaseNum());
        return commodity;
    }

//    /**
//     *实现再按一次返回键退出程序
//     */
//    private long exitTime = 0;
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
//            if((System.currentTimeMillis()-exitTime) > 3000){
//                toast("再按一次退出程序！");
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
