package com.hbsx.purordermanage;

import android.app.Application;
import android.content.Context;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * Created by Administrator on 2017/1/7 0007.
 */

public class POManageApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //通过AppId连接Bmob云端
        Bmob.initialize(this, "640680c69663ae2d2c1df82566af1fdc");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        BmobPush.startWork(this);

    }
    public static Context getContext(){
        return context;
    }
}
