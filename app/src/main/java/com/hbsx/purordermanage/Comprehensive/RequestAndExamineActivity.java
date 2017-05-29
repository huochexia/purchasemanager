package com.hbsx.purordermanage.Comprehensive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.hbsx.purordermanage.BaseActivity;
import com.hbsx.purordermanage.Examine.ExamineMainActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Request.RequestNoteMainActivity;

/**
 * 组合活动，适用于综合岗，包括订货、购买、验货及录入功能
 * Created by Administrator on 2017/3/5 0005.
 */

public class RequestAndExamineActivity extends BaseActivity
        implements View.OnClickListener {
    Toolbar toolbar;
    ImageButton mRequestNote, mExamine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_and_examine);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("综合功能");
        setSupportActionBar(toolbar);

        initView();
        initEvent();
    }

    private void initEvent() {
        mRequestNote.setOnClickListener(this);
        mExamine.setOnClickListener(this);
    }

    private void initView() {
        mRequestNote = (ImageButton) findViewById(R.id.request_note);
        mExamine = (ImageButton) findViewById(R.id.examine_order);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_note:
                RequestNoteMainActivity.actionStart(RequestAndExamineActivity.this);
                break;

            case R.id.examine_order:
                ExamineMainActivity.actionStart(RequestAndExamineActivity.this);
                break;

        }

    }

    /**
     * 启动
     */
    public static void actionStart(Context mContext) {
        Intent intent = new Intent(mContext, RequestAndExamineActivity.class);
        mContext.startActivity(intent);
    }
}
