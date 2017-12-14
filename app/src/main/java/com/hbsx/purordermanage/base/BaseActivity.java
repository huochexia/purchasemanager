package com.hbsx.purordermanage.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hbsx.purordermanage.ActivityCollector;
import com.hbsx.purordermanage.R;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/1/8 0008.
 */

public class BaseActivity extends AppCompatActivity {
    private CompositeSubscription mCompositeSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityCollector.addActivity(this);
    }

    /**
     * 异步显示提示内容
     */
    public void runOnMain(Runnable runnable) {
        runOnUiThread(runnable);
    }

    protected static final String NULL = "";
    private Toast t;

    public void toast(final Object obj, final boolean isSucess) {
        try {
            runOnMain(new Runnable() {
                @Override
                public void run() {
                    if (t == null) {
                        t = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_LONG);// 显示时间也可以是数字
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.setText(obj.toString());
                        LinearLayout toastLayout = (LinearLayout) t.getView();
                        ImageView imageView = new ImageView(getApplicationContext());
                        if (isSucess) {
                            imageView.setImageResource(R.drawable.sucess);
                        } else {
                            imageView.setImageResource(R.drawable.error);
                        }

                        toastLayout.addView(imageView, 0);// 0 图片在文字的上方 ， 1 图片在文字的下方
                        t.show();

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动其他活动
     */
    public  void startActivity(Class<? extends Activity> target, Bundle bundle, boolean isFinish) {
        Intent intent = new Intent();
        intent.setClass(this, target);
        if (bundle != null) {
            intent.putExtra(getPackageName(), bundle);
        }
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public Bundle getBundle() {
        if (getIntent() != null && getIntent().hasExtra(getPackageName())) {
            return getIntent().getBundleExtra(getPackageName());
        } else {
            return null;
        }

    }
    /**
     * 解决Subscription内存泄露问题
     *
     * @param s
     */
    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


}