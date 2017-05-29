package com.hbsx.purordermanage;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合类，对所有的活动进行统一管理
 * Created by liuyong on 2017/1/8 0008.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);

    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
