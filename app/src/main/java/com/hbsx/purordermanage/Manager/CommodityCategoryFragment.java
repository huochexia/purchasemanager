package com.hbsx.purordermanage.Manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hbsx.purordermanage.Manager.adapter.CommoCateAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.CommodityCategory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 商品分类Fragment：当用户需要对商品进行设置时，先进入此界面，然后选择相对应的类别。1.0版先实现
 * 固定数量的分类，未来升级版可实现动态增加、删除分类。
 * Created by Administrator on 2017/1/12 0012.
 */

public class CommodityCategoryFragment extends Fragment {


    private List<CommodityCategory> catelist = new ArrayList<>();

    private RecyclerView mCategoryView;
    private CommoCateAdapter adapter;
    //传入不同的标志，启动不同的功能。1：商品管理；2：报货。
    private int mFlag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCategoryList();
        mFlag=getArguments().getInt("flag");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //获取Fragement视图对象
        View view = inflater.inflate(R.layout.fragment_commo_cate, container, false);
        //从视图对象中获取列表对象
        mCategoryView = (RecyclerView) view.findViewById(R.id.cate_list_view);
        //定义列表对象管理器
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mCategoryView.setLayoutManager(layoutManager);
        return view;

    }

    /**
     * 从云数据库中获取数据
     */
    private void getCategoryList() {
        BmobQuery<CommodityCategory> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(new FindListener<CommodityCategory>() {
            @Override
            public void done(final List<CommodityCategory> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = CATEGORY_LIST;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("categorylist", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                } else {
                    Toast.makeText(getActivity(), "查询失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 利用handler得到数据
     */
    public static final int CATEGORY_LIST = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CATEGORY_LIST:
                    catelist = (List<CommodityCategory>) msg.getData().getSerializable("categorylist");
                    while (catelist == null) {
                        Toast.makeText(getActivity(), "正在加载单位数据....", Toast.LENGTH_SHORT).show();
                    }
                    //利用数据源生成配器
                    adapter = new CommoCateAdapter(catelist,mFlag);
                    mCategoryView.setAdapter(adapter);
                    break;
            }

        }
    };
}
