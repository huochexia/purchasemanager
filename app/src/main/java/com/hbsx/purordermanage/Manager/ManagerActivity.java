package com.hbsx.purordermanage.Manager;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.hbsx.purordermanage.BaseActivity;
import com.hbsx.purordermanage.Manager.adapter.CommoCateAdapter;
import com.hbsx.purordermanage.R;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2017/1/10 0010.
 */

public class ManagerActivity extends BaseActivity {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("管理员操作界面！");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.manager_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.open, R.string.close);
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        NavigationView navigationView =
                (NavigationView) findViewById(R.id.manager_nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {

                            managerItem(menuItem);
                            mDrawerLayout.closeDrawers();

                            return true;
                        }
                    });
        }
       ManagerMainFragment mMainInterface = new ManagerMainFragment();
       FragmentManager fm = getSupportFragmentManager();
       FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.manager_fragment_container, mMainInterface);
        ft.commit();
    }

    /**
     * 管理项目：加载不同内容的Fragment
     *
     * @param menuItem
     */
    private void managerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.user_set:
                setToolbarTitle("用户管理");
                UserDetailFragment useFg = new UserDetailFragment();
                FragmentManager mf = getSupportFragmentManager();
                FragmentTransaction mt = mf.beginTransaction();
                mt.replace(R.id.manager_fragment_container, useFg);
                mt.commit();
                break;
            case R.id.commodiyt_set:
                Bundle bundle = new Bundle();
                bundle.putInt("flag", CommoCateAdapter.COMMODITY_MANAGER);
                setToolbarTitle("商品管理");
                CommodityCategoryFragment ccFm = new CommodityCategoryFragment();
                ccFm.setArguments(bundle);
                FragmentManager mFManager = getSupportFragmentManager();
                FragmentTransaction mFTransaction = mFManager.beginTransaction();
                mFTransaction.replace(R.id.manager_fragment_container, ccFm);
                mFTransaction.commit();
                break;
            case R.id.return_main:
                ManagerMainFragment mmf = new ManagerMainFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.manager_fragment_container, mmf);
                ft.commit();
                break;
        }
    }

    /**
     * 修改Toolbar标题的方法
     */
    public void setToolbarTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_btn:
                BmobUser.logOut();   //清除缓存用户对象
//                BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了
                this.finish();
                break;
        }
        return true;
    }

    /**
     * 实现再按一次返回键退出程序
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 3000) {
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
