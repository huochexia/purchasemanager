package com.hbsx.purordermanage.AgainCheck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.R;

/**
 * 报货活动：该活动主要负责处理报货员根据菜谱审报预采购商品及数量
 * Created by Administrator on 2017/1/23 0023.
 */

public class AgainCheckMainActivity extends BaseActivity {

    private Toolbar mToolbar;
    private Button myShoppingCart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baohuo);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("选择拟审核商品");
        setSupportActionBar(mToolbar);
        //因为是复用报货主界面layout,所以此处不显示这个按钮
        myShoppingCart = (Button) findViewById(R.id.my_shopping_cart_btn);
        myShoppingCart.setVisibility(View.GONE);


        CheckCategoryFragment ccf = new CheckCategoryFragment();

        FragmentManager mFManager = getSupportFragmentManager();
        FragmentTransaction mFTransaction = mFManager.beginTransaction();
        mFTransaction.add(R.id.baohuo_fragment_container, ccf);
        mFTransaction.commit();

    }

    //启动本活动方法
    public static void actionStart(Context mContext) {
        Intent intent = new Intent(mContext, AgainCheckMainActivity.class);
        mContext.startActivity(intent);
    }


}
