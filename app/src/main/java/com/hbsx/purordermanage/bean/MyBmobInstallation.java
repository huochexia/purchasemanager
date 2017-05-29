package com.hbsx.purordermanage.bean;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * 自定义BmobInstallation，增加用户
 * Created by Administrator on 2017/3/6 0006.
 */

public class MyBmobInstallation extends BmobInstallation {
    private User user;
    private Context mContext;
    public MyBmobInstallation(Context context) {
        super();
        mContext = context;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
