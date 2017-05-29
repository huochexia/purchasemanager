package com.hbsx.purordermanage.Manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.hbsx.purordermanage.AgainCheck.AgainCheckMainActivity;
import com.hbsx.purordermanage.Examine.ExamineMainActivity;
import com.hbsx.purordermanage.InputData.InputDataMainActivity;
import com.hbsx.purordermanage.Purchase.SendToProviderActivity;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.Request.RequestNoteMainActivity;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class ManagerMainFragment extends Fragment implements View.OnClickListener {
    ImageButton mRequestNote, mSendTo, mCheck, mExamine, mInput;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comprehensive_layout, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        mRequestNote = (ImageButton) view.findViewById(R.id.compre_request_note);
        mSendTo = (ImageButton) view.findViewById(R.id.compre_purchase_order);
        mCheck = (ImageButton) view.findViewById(R.id.check_request_note);
        mExamine = (ImageButton) view.findViewById(R.id.compre_examine_order);
        mInput = (ImageButton) view.findViewById(R.id.compre_input_order);

    }

    private void initEvent() {
        mRequestNote.setOnClickListener(this);
        mSendTo.setOnClickListener(this);
        mCheck.setOnClickListener(this);
        mExamine.setOnClickListener(this);
        mInput.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.compre_request_note:
                RequestNoteMainActivity.actionStart(getContext());
                break;
            case R.id.compre_purchase_order:
                SendToProviderActivity.actionStart(getContext());
                break;
            case R.id.check_request_note:
                AgainCheckMainActivity.actionStart(getContext());
                break;
            case R.id.compre_examine_order:
                ExamineMainActivity.actionStart(getContext());
                break;
            case R.id.compre_input_order:
                InputDataMainActivity.actionStart(getContext());
                break;
        }
    }
}
