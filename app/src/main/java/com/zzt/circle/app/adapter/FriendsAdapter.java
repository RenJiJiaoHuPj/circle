package com.zzt.circle.app.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.activity.LoginActivity;
import com.zzt.circle.app.activity.MainActivity;
import com.zzt.circle.app.entity.UserEntity;
import com.zzt.circle.app.net.AddFriend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzt on 15-6-6.
 */
public class FriendsAdapter extends BaseAdapter {

    private List<UserEntity> friends = new ArrayList<UserEntity>();
    private Context context;
    private ImageLoader imageLoader;
    private boolean isAddFriend;
    public FriendsAdapter(Context context) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
    }

    public void addAll(List<UserEntity> data) {
        friends.clear();
        notifyDataSetChanged();
        friends.addAll(data);
        notifyDataSetChanged();
    }

    public void setIsAddFriend(boolean isAddFriend){
        this.isAddFriend = isAddFriend;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public UserEntity getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final UserEntity friend = friends.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cell_friend, null);

            holder = new ViewHolder();
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
            holder.tvNickname = (TextView) convertView.findViewById(R.id.tvNickname);

            holder.tvNickname.setText(friend.getNickname());
            holder.addFriend = (Button) convertView.findViewById(R.id.addFriend);

            if (isAddFriend) {
                holder.addFriend.setVisibility(View.VISIBLE);
                holder.addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AddFriend(Config.getCachedAccount(context.getApplicationContext()),
                                Config.getCachedToken(context.getApplicationContext()), friend.getAccount(),
                                new AddFriend.SuccessCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(context.getApplicationContext(), "添加成功," + friend.getNickname() + "已经是你的好友", Toast.LENGTH_LONG).show();
                                        isAddFriend = false;
//                                context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
                                    }
                                },
                                new AddFriend.FailCallback() {
                                    @Override
                                    public void onFail() {
                                        onFail(Config.RESULT_STATUS_FAIL);
                                    }

                                    @Override
                                    public void onFail(int failCode) {
                                        switch (failCode) {
                                            case Config.RESULT_STATUS_FAIL:
                                                isAddFriend = false;
                                                Toast.makeText(context.getApplicationContext(), "添加失败,该用户已是您好友", Toast.LENGTH_LONG).show();
                                                break;
                                            case Config.RESULT_STATUS_INVALID_TOKEN:
                                                isAddFriend = false;
                                                Toast.makeText(context.getApplicationContext(), R.string.invalid_token_please_login_again, Toast.LENGTH_LONG).show();
                                                context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
                                                break;
                                        }
                                    }
                                });
                    }
                });
            }else
                holder.addFriend.setVisibility(View.INVISIBLE);

            //点击昵称可以弹出好友详情 TODO 返回与取消关注事件
            holder.tvNickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View layout = inflater.inflate(R.layout.friend_detail, null);
                    final AlertDialog builder = new AlertDialog.Builder(context).create();
                    builder.setView(layout);
                    builder.setCancelable(false);
                    builder.show();
                    EditText account = (EditText) layout.findViewById(R.id.account);
                    EditText gender = (EditText) layout.findViewById(R.id.gender);
                    EditText nickname = (EditText) layout.findViewById(R.id.nickname);
                    ImageView iv_image = (ImageView) layout.findViewById(R.id.iv_image);
                    account.setText(friend.getAccount());
                    //TODO 好友中还未有性别,服务器端需要添加返回项,客户端需要添加接收项(在数据类中)
                    gender.setText("男");
                    nickname.setText(friend.getNickname());
                    ImageLoader imageLoader;
                    imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(Config.SERVER_URL + friend.getAvatarURL(), iv_image);
                    layout.findViewById(R.id.ret).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.cancel();
                        }
                    });
                    Button cancel = (Button) layout.findViewById(R.id.cancel);
                    if (isAddFriend)
                        cancel.setText("添加关注");
//                    ad.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//                    ad.getWindow().setContentView(R.layout.friend_detail);
//                    ad.setContentView(R.layout.friend_detail);
                }
            });
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();


        imageLoader.displayImage(Config.SERVER_URL + friend.getAvatarURL(), holder.ivAvatar);

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivAvatar;
        TextView tvNickname;
        Button addFriend;
    }
}
