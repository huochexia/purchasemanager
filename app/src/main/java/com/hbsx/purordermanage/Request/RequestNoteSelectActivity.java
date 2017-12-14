package com.hbsx.purordermanage.Request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.CommodityCategory;

/**
 * Created by Administrator on 2017/1/25 0025.
 */

public class RequestNoteSelectActivity extends BaseActivity {

    private RequestNoteSelectlFragment cdf;
    CommodityCategory mCategory;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baohuo_detail);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mCategory = (CommodityCategory) bundle.getSerializable("category");

        FragmentManager fm = getSupportFragmentManager();
        cdf = new RequestNoteSelectlFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.baohuo_detail_fragment_container, cdf);
        ft.commit();
    }


    /**
     * 静态方法，启动此活动时，传入一个数据
     *
     * @param context
     * @param
     */
    public static void actionStart(Context context, CommodityCategory category) {
        Intent intent = new Intent(context, RequestNoteSelectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", category);

        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
