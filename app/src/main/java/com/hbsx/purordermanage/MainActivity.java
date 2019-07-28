package com.hbsx.purordermanage;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.hbsx.purordermanage.Comprehensive.CheckAndOtherActivity;
import com.hbsx.purordermanage.Comprehensive.RequestAndExamineActivity;
import com.hbsx.purordermanage.Comprehensive.RequestPurchaseExamineInputActivity;
import com.hbsx.purordermanage.Examine.ExamineMainActivity;
import com.hbsx.purordermanage.FirstCheck.EmployeeListActivity;
import com.hbsx.purordermanage.InputData.InputDataMainActivity;
import com.hbsx.purordermanage.Manager.ManagerActivity;
import com.hbsx.purordermanage.Other.OtherMainActivity;
import com.hbsx.purordermanage.Purchase.PurchaseMainActivity;
import com.hbsx.purordermanage.Request.RequestNoteMainActivity;
import com.hbsx.purordermanage.Supply.SupplyMainActivity;
import com.hbsx.purordermanage.base.BaseActivity;
import com.hbsx.purordermanage.bean.Commodity;
import com.hbsx.purordermanage.bean.PurchaseOrder;
import com.hbsx.purordermanage.bean.Roles;
import com.hbsx.purordermanage.bean.User;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 主程序入口：先判断是否存在当前用户，如果有存在则获取当前用户的角色，根据角色不同，调用不同的活动。
 * 如果不存在当前用户，则调用登录界面。
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private RelativeLayout mainLayout;
    private FloatingActionButton repairPW;
    private Handler handler;
    private Runnable runnable;

    private ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.bing_pic_img);

        loadBingPic();
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
                            if (user == null) {
                                return;
                            }
                            Roles role = user.getRole();
                            switch (role.getRoleName()) {
                                case "管理员":
                                    startActivity(ManagerActivity.class, null, true);
                                    break;
                                case "报货":
                                    startActivity(RequestNoteMainActivity.class, null, true);
                                    break;
                                case "订货":
                                    startActivity(PurchaseMainActivity.class, null, true);
                                    break;
                                case "供货":
                                    startActivity(SupplyMainActivity.class, null, true);
                                    break;
                                case "验货":
                                    startActivity(ExamineMainActivity.class, null, true);
                                    break;
                                case "录入":
                                    startActivity(InputDataMainActivity.class, null, true);
                                    break;
                                case "综合":
                                    startActivity(RequestPurchaseExamineInputActivity.class, null, true);
                                    break;
                                case "报货和验收":
                                    startActivity(RequestAndExamineActivity.class, null, true);
                                    break;
                                case "初核":
                                    startActivity(EmployeeListActivity.class, null, true);
                                    break;
                                case "其他":
                                    startActivity(CheckAndOtherActivity.class, null, true);
                                    break;
                            }
                        }
                    });
                }
            };
            //启动延时3秒
            handler.postDelayed(runnable, 3000); //启动动画持续3秒钟
        } else {
            startActivity(LoginActivity.class, null, true);
        }
    }

    /**
     * 实现再按一次返回键退出程序
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                toast("再按一次退出程序！",true);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
/**
 * 加载必应每日一图
 */
private void loadBingPic() {
    String requestBingPic = "http://guolin.tech/api/bing_pic";
    sendOkHttpRequest(requestBingPic, new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String bingPic = response.body().string();
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
            editor.putString("bing_pic",bingPic);
            editor.apply();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(getApplicationContext()).load(bingPic).into(mImageView);
                }
            });
        }
    });

}

    public void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

}