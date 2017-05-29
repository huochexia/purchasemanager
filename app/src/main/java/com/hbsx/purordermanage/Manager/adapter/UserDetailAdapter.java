package com.hbsx.purordermanage.Manager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hbsx.purordermanage.R;
import com.hbsx.purordermanage.bean.User;
import com.hbsx.purordermanage.utils.onMoveAndSwipedListener;
import com.hbsx.purordermanage.utils.onStateChangedListener;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/1/14 0014.
 */
public class UserDetailAdapter extends RecyclerView.Adapter<UserDetailAdapter.ViewHolder>
implements onMoveAndSwipedListener {
    private Context mContext;
    private List<User> userlist;

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        User user = userlist.get(position);
        String objectId = user.getObjectId();
        User user1 = new User();
        user1.setObjectId(objectId);
        user1.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Toast.makeText(mContext,"删除成功！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext,"删除失败："+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
        userlist.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements onStateChangedListener {
        TextView userName,userPhone,userRole;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            userPhone = (TextView) itemView.findViewById(R.id.user_phone_number);
            userRole = (TextView) itemView.findViewById(R.id.user_role);
        }

        @Override
        public void onItemSelected() {
            userName.setBackgroundColor(Color.RED);
            userPhone.setBackgroundColor(Color.RED);
            userRole.setBackgroundColor(Color.RED);
        }

        @Override
        public void onItemClear() {
            userName.setBackgroundColor(0);
            userPhone.setBackgroundColor(0);
            userRole.setBackgroundColor(0);
        }
    }
    public UserDetailAdapter(List<User> list){
        this.userlist = list;
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = userlist.get(position);
        holder.userName.setText(user.getUsername());
        holder.userPhone.setText(user.getMobilePhoneNumber());
        holder.userRole.setText(user.getRole().getRoleName());

    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }


}
