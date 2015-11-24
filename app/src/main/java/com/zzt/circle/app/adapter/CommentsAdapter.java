package com.zzt.circle.app.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.entity.CommentEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzt on 15-6-14.
 */
public class CommentsAdapter extends BaseAdapter {

    private List<CommentEntity> comments = new ArrayList<CommentEntity>();
    private Context context;
    private ImageLoader imageLoader;

    public CommentsAdapter(Context context) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
    }

    public void addAll(List<CommentEntity> data) {
        comments.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public CommentEntity getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cell_comment, null);
            holder = new ViewHolder();
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
            holder.tvNickname = (TextView) convertView.findViewById(R.id.tvNickname);
            holder.tvPostTime = (TextView) convertView.findViewById(R.id.tvPostTime);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        CommentEntity comment = comments.get(position);

        imageLoader.displayImage(Config.SERVER_URL + comment.getAvatarUrl(), holder.ivAvatar);
        holder.tvNickname.setText(comment.getNickname());
        holder.tvPostTime.setText(comment.getContent());
        holder.tvContent.setText(DateFormat.format("yyyy-MM-dd kk:mm", comment.getPostTime()));
        return convertView;
    }

    private class ViewHolder {
        ImageView ivAvatar;
        TextView tvNickname;
        TextView tvPostTime;
        TextView tvContent;
    }
}
