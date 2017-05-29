package com.learnandroid.liuyong.bmobtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.bmob.v3.Bmob;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Bmob.initialize(this, "640680c69663ae2d2c1df82566af1fdc","order");
    }
}
