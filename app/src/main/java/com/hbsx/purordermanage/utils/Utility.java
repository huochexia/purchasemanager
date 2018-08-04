package com.hbsx.purordermanage.utils;

import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/2/12 0012.
 */

public class Utility {
    /**
     * List去重
     *
     * @param list
     */
    public static void removeDuplicate(List list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).equals(list.get(i))) {
                    list.remove(j);
                }
            }
        }
    }

    /**
     * 获取系统当前日期字符串
     */
    public static String getCurrentDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当日期
        return formatter.format(curDate);
    }

    /**
     * 将日期转换成字符串形式
     */
    public static String getDateString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    /**
     * 将字符串转换成指定格式的时间
     *
     * @param start
     * @return
     */
    @Nullable
    public static Date getDate(String start) {
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        try {
            startDate = formatter2.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate;
    }

    /**
     * 获取当前日期前几天
     */
    public static Date getForwardDate(int number) {
        Calendar c = Calendar.getInstance();
        String current = getCurrentDateString() + " 00:00:00";
        c.setTime(Utility.getDate(current));
        //获取当前日期前几天
        c.add(Calendar.DAY_OF_MONTH, -number);
        return c.getTime();
    }

    /**
     * 当月第一天
     *
     * @return
     */
    public static String getFirstDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);


        String day_first = df.format(calendar.getTime());
        StringBuffer str = new StringBuffer().append(day_first);
        return str.toString();


    }

    /**
     * 当月最后一天
     *
     * @return
     */
    public static String getLastDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();
        // 获取前月的最后一天
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        String s = df.format(calendar.getTime());
        StringBuffer str = new StringBuffer().append(s).append(" 23:59:59");
        return str.toString();

    }

    /**
     * 系统当前时间
     */
    public static String getCurrentTime() {
        Date now = new Date();
        DateFormat d1 = DateFormat.getTimeInstance(); //默认语言（汉语）下的默认风格（MEDIUM风格，比如：2008-6-16 20:54:53）
        return d1.format(now);
    }

    /**
     * 与系统当前时间比较
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static boolean compareTime(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");    //设置时间格式
        String curDate = sdf.format(new Date(System.currentTimeMillis()));//获取当前时间
        if (curDate.compareTo(time) < 0) {
            return false;
        }
        return true;
    }

}
