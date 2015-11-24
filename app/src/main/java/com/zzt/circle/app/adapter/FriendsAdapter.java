package com.zzt.circle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzt on 15-6-6.
 */
public class FriendsAdapter extends BaseAdapter {

    private List<UserEntity> friends = new ArrayList<UserEntity>();
    private Context context;
    private ImageLoader imageLoader;

    public FriendsAdapter(Context context) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
    }

    public void addAll(List<UserEntity> data) {
        friends.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        friends.clear();
        notifyDataSetChanged();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cell_friend, null);

            holder = new ViewHolder();
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
            holder.tvNickname = (TextView) convertView.findViewById(R.id.tvNickname);

            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        UserEntity friend = getItem(position);

        imageLoader.displayImage(Config.SERVER_URL + friend.getAvatarURL(), holder.ivAvatar);
        holder.tvNickname.setText(friend.getNickname());
        return convertView;
    }

    private static class ViewHolder {
        ImageView ivAvatar;
        TextView tvNickname;
    }
}
