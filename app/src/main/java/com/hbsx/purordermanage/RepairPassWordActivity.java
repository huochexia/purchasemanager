package com.hbsx.purordermanage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hbsx.purordermanage.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/1/9 0009.
 */

public class RepairPassWordActivity extends BaseActivity implements View.OnClickListener {
    private Button mConfirmBtn, mCancelBtn;
    private EditText mOldPWEdit, mNewPWEdit;
    private Toolbar mToolBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repairpassword_layout);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("修改你的密码");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_btn:
                BmobUser.logOut();   //清除缓存用户对象
//                BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了
                ActivityCollector.finishAll();
                break;
        }
        return true;
    }

    private void initEvent() {
        mConfirmBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);


    }

    private void initView() {
        mOldPWEdit = (EditText) findViewById(R.id.edit_old_pw);
        mOldPWEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mCancelBtn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mNewPWEdit = (EditText) findViewById(R.id.edit_new_pw);
        mConfirmBtn = (Button) findViewById(R.id.btn_confirm_repair);
        mCancelBtn = (Button) findViewById(R.id.btn_cancel_repair);
        mCancelBtn.setEnabled(false);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_confirm_repair:
                String oldpw = mOldPWEdit.getText().toString().trim();
                String newpw = mNewPWEdit.getText().toString().trim();
                checkPassword(oldpw, newpw);

                break;
            case R.id.btn_cancel_repair:
                mOldPWEdit.setText("");
                mNewPWEdit.setText("");
                break;

        }
    }
    /**
     * 验证旧密码是否正确
     *
     * @param
     * @return void
     */
    private void checkPassword(final String oldPw, final String newPw) {
        BmobQuery<User> query = new BmobQuery<User>();
        final User bmobUser = BmobUser.getCurrentUser(User.class);
        // 如果你传的密码是正确的，那么arg0.size()的大小是1，这个就代表你输入的旧密码是正确的，否则是失败的
        query.addWhereEqualTo("password", oldPw);
        query.addWhereEqualTo("username", bmobUser.getUsername());
        addSubscription(query.findObjects(new FindListener<User>() {

            @Override
            public void done(List<User> object, BmobException e) {
                if (e == null) {

                    if (object.size() == 1) {
                        updateCurrentUserPwd(oldPw, newPw);
                    } else {
                        toast("旧密码不对！");
                    }

                } else {
                    toast("错误码：" + e.getErrorCode() + ",错误原因：" + e.getLocalizedMessage());
                }
            }

        }));
    }

    /**
     * 修改当前用户密码
     *
     * @return void
     * @throws
     */
    private void updateCurrentUserPwd(String oldPw, String newPw) {
        addSubscription(BmobUser.updateCurrentUserPassword(oldPw, newPw, new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //密码修改成功后，注销当前用户
                    BmobUser.logOut();
                    toast("密码修改成功，可以用新密码进行登录");
                } else {

                }
            }
        }));
    }
}
