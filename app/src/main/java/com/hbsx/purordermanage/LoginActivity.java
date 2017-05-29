package com.hbsx.purordermanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hbsx.purordermanage.Comprehensive.RequestAndExamineActivity;
import com.hbsx.purordermanage.Comprehensive.RequestPurchaseExamineInputActivity;
import com.hbsx.purordermanage.Examine.ExamineMainActivity;
import com.hbsx.purordermanage.FirstCheck.EmployeeListActivity;
import com.hbsx.purordermanage.InputData.InputDataMainActivity;
import com.hbsx.purordermanage.Purchase.SendToProviderActivity;
import com.hbsx.purordermanage.bean.MyBmobInstallation;
import com.hbsx.purordermanage.bean.Roles;
import com.hbsx.purordermanage.bean.User;
import com.hbsx.purordermanage.Request.RequestNoteMainActivity;
import com.hbsx.purordermanage.Manager.ManagerActivity;
import com.hbsx.purordermanage.Purchase.PurchaseMainActivity;
import com.hbsx.purordermanage.Supply.SupplyMainActivity;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 用户登录及重置密码界面，绑定用户与设备，用于推送信息
 * Created by Administrator on 2017/1/8 0008.
 */

public class LoginActivity extends BaseActivity {

    private Button loginBtn;
    private EditText mTeleEdit, mPWEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mTeleEdit = (EditText) findViewById(R.id.telephone_edit);
        mPWEdit = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginByPhonePwd();
            }
        });

    }


    /**
     * 利用已注册的手机和密码进行登录
     */
    private void loginByPhonePwd() {
        String number = mTeleEdit.getText().toString().trim();
        String password = mPWEdit.getText().toString().trim();
        addSubscription(BmobUser.loginByAccount(number, password, new LogInListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    String objectId = user.getObjectId();
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.include("role");
                    query.getObject(objectId, new QueryListener<User>() {
                        @Override
                        public void done(final User user, BmobException e) {
                            /*
                            如果注册成功，则将设备与用户通过Installation进行绑定
                             */
                            BmobQuery<MyBmobInstallation> query = new BmobQuery<>();
                            query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(getApplication()));
                            query.findObjects(new FindListener<MyBmobInstallation>() {
                                @Override
                                public void done(List<MyBmobInstallation> list, BmobException e) {
                                    if(list.size()>0){
                                        MyBmobInstallation mbi = list.get(0);
                                        mbi.setUser(user);//绑定用户
                                        mbi.update(list.get(0).getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {

                                            }
                                        });
                                    }
                                }
                            });
                            Roles role = user.getRole();
                            switch (role.getRoleName()) {
                                case "管理员":
                                    Intent intent = new Intent(LoginActivity.this, ManagerActivity.class); //从启动动画ui跳转到主ui
                                    startActivity(intent);

                                    break;
                                case "报货":
                                    RequestNoteMainActivity.actionStart(LoginActivity.this);

                                    break;

                                case "订货":
                                   PurchaseMainActivity.actionStart(LoginActivity.this);
                                    break;
                                case "供货":
                                    SupplyMainActivity.actionStart(LoginActivity.this);
                                    break;
                                case "验货":
                                    ExamineMainActivity.actionStart(LoginActivity.this);
                                    break;
                                case "录入":
                                    InputDataMainActivity.actionStart(LoginActivity.this);
                                    break;
                                case "综合":
                                    RequestPurchaseExamineInputActivity.actionStart(LoginActivity.this);
                                    break;
                                case "报货和验收":
                                    RequestAndExamineActivity.actionStart(LoginActivity.this);
                                    break;
                                case "初核":
                                    EmployeeListActivity.actionStart(LoginActivity.this);
                                    break;
                            }
                            LoginActivity.this.finish();
                        }
                    });
                } else {
                    toast("错误码：" + e.getErrorCode() + ",错误原因：" + e.getLocalizedMessage());
                    mPWEdit.setText("");
                }
            }
        }));

    }


}
