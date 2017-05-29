package com.learnandroid.liuyong.bmobtest;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/1/7 0007.
 */

public class Person extends BmobObject {
    private String name;
    private Integer age;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
