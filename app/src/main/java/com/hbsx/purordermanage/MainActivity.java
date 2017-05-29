package com.hbsx.purordermanage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
import android.view.View;

import com.hbsx.purordermanage.Comprehensive.RequestAndExamineActivity;
import com.hbsx.purordermanage.Comprehensive.RequestPurchaseExamineInputActivity;
import com.hbsx.purordermanage.Examine.ExamineMainActivity;
import com.hbsx.purordermanage.FirstCheck.EmployeeListActivity;
import com.hbsx.purordermanage.InputData.InputDataMainActivity;
import com.hbsx.purordermanage.Purchase.SendToProviderActivity;
import com.hbsx.purordermanage.bean.Roles;
import com.hbsx.purordermanage.bean.User;
import com.hbsx.purordermanage.Request.RequestNoteMainActivity;
import com.hbsx.purordermanage.Manager.ManagerActivity;
import com.hbsx.purordermanage.Purchase.PurchaseMainActivity;
import com.hbsx.purordermanage.Supply.SupplyMainActivity;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * 主程序入口：先判断是否存在当前用户，如果有存在则获取当前用户的角色，根据角色不同，调用不同的活动。
 * 如果不存在当前用户，则调用登录界面。
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private FloatingActionButton repairPW;
    private Handler handler;
    private Runnable runnable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        repairPW = (FloatingActionButton) findViewById(R.id.repair_pw_text);
        repairPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // 关闭延时线程
                handler.removeCallbacks(runnable);
                Intent intent = new Intent(MainActivity.this, RepairPassWordActivity.class);
                startActivity(intent);
            }
        });

        getlocalUser();
    }


    /**
     * 获取本地用户
     */
    private void getlocalUser() {
        User myUser = BmobUser.getCurrentUser(User.class);
        if (myUser != null) {
            /**
             * 实现启动闪屏界面效果
             */
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    String objectId = (String) BmobUser.getObjectByKey("objectId");
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.include("role");
                    query.getObject(objectId, new QueryListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            Roles role = user.getRole();
                            switch (role.getRoleName()) {
                                case "管理员":
                                    Intent intent = new Intent(MainActivity.this, ManagerActivity.class); //从启动动画ui跳转到主ui
                                    startActivity(intent);
                                    break;
                                case "报货":
                                    RequestNoteMainActivity.actionStart(MainActivity.this);
                                    break;
                                case "订货":
                                    PurchaseMainActivity.actionStart(MainActivity.this);
                                    break;
                                case "供货":
                                    SupplyMainActivity.actionStart(MainActivity.this);
                                    break;
                                case "验货":
                                    ExamineMainActivity.actionStart(MainActivity.this);
                                    break;
                                case "录入":
                                    InputDataMainActivity.actionStart(MainActivity.this);
                                    break;
                                case "综合":
                                    RequestPurchaseExamineInputActivity.actionStart(MainActivity.this);
                                    break;
                                case "报货和验收":
                                    RequestAndExamineActivity.actionStart(MainActivity.this);
                                    break;
                                case "初核":
                                    EmployeeListActivity.actionStart(MainActivity.this);
                                    break;
                            }
                            MainActivity.this.finish(); // 结束启动动画界面
                        }
                    });
                }
            };
            //启动延时3秒
            handler.postDelayed(runnable, 3000); //启动动画持续3秒钟
        } else {
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
            MainActivity.this.finish();
        }
    }
    /**
     *实现再按一次返回键退出程序
     */
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                toast("再按一次退出程序！");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}