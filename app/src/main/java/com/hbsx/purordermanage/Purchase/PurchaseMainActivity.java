package com.hbsx.purordermanage.Purchase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.R;

/**
 * Created by Administrator on 2017/2/14 0014.
 */

public class PurchaseMainActivity extends BaseActivity {
    Toolbar toolbar;
    ImageButton distribute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("订货");
        setSupportActionBar(toolbar);

        distribute = (ImageButton) findViewById(R.id.distribute_purchase_order_btn);
        distribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToProviderActivity.actionStart(PurchaseMainActivity.this);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.purchase_menu,menu);
        return true;
    }
    public static final int SEARCH_TODAY = 0;
    public static final int SEARCH_YESTERDAY = 1;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.today_purchase_order:
                ProviderOrderSearchActivity.actionStart(this,SEARCH_TODAY);
                break;
            case R.id.yesterday_purchase_order:
                ProviderOrderSearchActivity.actionStart(this,SEARCH_YESTERDAY);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    public static void actionStart(Context mContext){
        Intent intent = new Intent(mContext,PurchaseMainActivity.class);
        mContext.startActivity(intent);
    }
    /**
     *实现再按一次返回键退出程序
     */
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 3000){
                toast("再按一次退出程序！",true);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
