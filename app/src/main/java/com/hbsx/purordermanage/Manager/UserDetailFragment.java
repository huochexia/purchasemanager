package com.hbsx.purordermanage.Manager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import com.hbsx.purordermanage.Manager.adapter.RoleSpinnerAdapter;
import com.hbsx.purordermanage.Manager.adapter.UserDetailAdapter;
import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.Roles;
import com.hbsx.purordermanage.bean.User;
import com.hbsx.purordermanage.utils.SimpleItemTouchHelperCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 商品分类Fragment：当用户需要对商品进行设置时，先进入此界面，然后选择相对应的类别。1.0版先实现
 * 固定数量的分类，未来升级版可实现动态增加、删除分类。
 * Created by Administrator on 2017/1/12 0012.
 */

public class UserDetailFragment extends Fragment  {

    //定义用户数据
    private List<User> userlist = new ArrayList<>();
    private RecyclerView mUserListView;
    private UserDetailAdapter mUseradapter;
    //定义角色数据
    private List<Roles> mRoleList = new ArrayList<>();
    private Spinner mRoleSpinner;
    private RoleSpinnerAdapter mRoleAdapter;
    private Roles mRole;
    private TableLayout mAddUserDialog;

    //定义控件
    private FloatingActionButton addUserBtn;
    //定义item触摸服务
    private ItemTouchHelper mItemTouchHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //获取Fragement视图对象
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        //从视图对象中获取列表对象
        mUserListView = (RecyclerView) view.findViewById(R.id.user_detail_list);
        //定义列表对象管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mUserListView.setLayoutManager(layoutManager);
        //获得增加按钮组件
        addUserBtn = (FloatingActionButton) view.findViewById(R.id.btn_add_user);
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取添加用户对话框视图
                mAddUserDialog = (TableLayout) getActivity()
                        .getLayoutInflater().inflate(R.layout.dialog_add_user,null);
                mRoleSpinner = (Spinner) mAddUserDialog.findViewById(R.id.add_user_form_role_spinner);
                //获取角色列表
                getRoleList();
                mRoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        mRole = (Roles) mRoleAdapter.getItem(i);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                //创建对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("添加用户").setView(mAddUserDialog);
                setPositiveButton(builder);
                setNegativeButton(builder).create().show();
            }
        });
        return view;

    }

    /**
     * 从云数据库中获取用户数据
     */
    private void getUserList() {
        BmobQuery<User> query = new BmobQuery<>();
        //查询策略为先从网中获取，如果网络没有则从缓存获取
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.include("role");
        query.order("role");
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(final List<User> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = USER_LIST;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("userlist", (Serializable) list);
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
     * 从云数据库中获得角色数据
     */
    private void getRoleList(){
        BmobQuery<Roles> query = new BmobQuery<>();
        query.findObjects(new FindListener<Roles>() {
            @Override
            public void done(final List<Roles> list, BmobException e) {
                if(e == null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message  msg = new Message();
                            msg.what = ROLE_LIST;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("rolelist", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }else{
                    Toast.makeText(getActivity(),"查询失败："+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    /**
     * 利用handler为主线程从子线程中获取数据
     */
    public static final int USER_LIST = 1;
    public static final int ROLE_LIST = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case USER_LIST:
                    userlist = (List<User>) msg.getData().getSerializable("userlist");
                    //利用数据源生成配器
                    mUseradapter = new UserDetailAdapter(userlist);
                    mUserListView.setAdapter(mUseradapter);
                    ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mUseradapter);
                    mItemTouchHelper = new ItemTouchHelper(callback);
                    mItemTouchHelper.attachToRecyclerView(mUserListView);
                    break;
                case ROLE_LIST:
                    mRoleList = (List<Roles>) msg.getData().getSerializable("rolelist");
                    mRoleAdapter = new RoleSpinnerAdapter(getActivity(),mRoleList);
                    mRoleSpinner.setAdapter(mRoleAdapter);
                    break;
            }
        }
    };

    /**
     * 对话框确认事件方法
     */
    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {
        return builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                final User user = new User();
                EditText userName = (EditText) mAddUserDialog.findViewById(R.id.add_user_form_name);
                user.setUsername(userName.getText().toString());
                EditText phone = (EditText) mAddUserDialog.findViewById(R.id.add_user_form_phone);
                user.setMobilePhoneNumber(phone.getText().toString());
                user.setPassword("000000");
                user.setRole(mRole);
                user.signUp(new SaveListener<User>() {
                    @Override
                    public void done(User s, BmobException e) {
                        if (e == null) {
                            userlist.add(user);
                            mUseradapter.notifyItemInserted(userlist.size() - 1);
                            Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "添加失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }

    /**
     * 对话框取消按事件
     */
    private AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder) {
        return builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }
}
