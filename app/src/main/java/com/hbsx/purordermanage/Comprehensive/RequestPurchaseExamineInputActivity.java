package com.hbsx.purordermanage.Comprehensive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.hbsx.purordermanage.BaseActivity;
import com.hbsx.purordermanage.Examine.ExamineMainActivity;
import com.hbsx.purordermanage.InputData.InputDataMainActivity;
import com.hbsx.purordermanage.Purchase.PurchaseMainActivity;
import com.hbsx.purordermanage.Purchase.SendToProviderActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Request.RequestNoteMainActivity;

/**
 * 组合活动，适用于综合岗，包括订货、购买、验货及录入功能
 * Created by Administrator on 2017/3/5 0005.
 */

public class RequestPurchaseExamineInputActivity extends BaseActivity
        implements View.OnClickListener {
    Toolbar toolbar;
    ImageButton mRequestNote, mSendTo, mExamine, mInput;
    CardView mCheckCard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprehensive);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("综合功能");
        setSupportActionBar(toolbar);

        initView();
        initEvent();
    }

    private void initEvent() {
        mRequestNote.setOnClickListener(this);
        mSendTo.setOnClickListener(this);
        mExamine.setOnClickListener(this);
        mInput.setOnClickListener(this);
    }

    private void initView() {
        mCheckCard = (CardView) findViewById(R.id.check_card_view);
        mCheckCard.setVisibility(View.GONE);
        mRequestNote = (ImageButton) findViewById(R.id.compre_request_note);
        mSendTo = (ImageButton) findViewById(R.id.compre_purchase_order);
        mExamine = (ImageButton) findViewById(R.id.compre_examine_order);
        mInput = (ImageButton) findViewById(R.id.compre_input_order);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.compre_request_note:
                RequestNoteMainActivity.actionStart(RequestPurchaseExamineInputActivity.this);
                break;
            case R.id.compre_purchase_order:
                SendToProviderActivity.actionStart(RequestPurchaseExamineInputActivity.this);
                break;
            case R.id.compre_examine_order:
                ExamineMainActivity.actionStart(RequestPurchaseExamineInputActivity.this);
                break;
            case R.id.compre_input_order:
                InputDataMainActivity.actionStart(RequestPurchaseExamineInputActivity.this);
                break;
        }

    }

    /**
     * 启动
     */
    public static void actionStart(Context mContext) {
        Intent intent = new Intent(mContext, RequestPurchaseExamineInputActivity.class);
        mContext.startActivity(intent);
    }
}
