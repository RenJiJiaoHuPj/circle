package com.zzt.circle.app.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.zzt.circle.app.activity.LargePhotoActivity;
import com.zzt.circle.app.entity.ImageMessageEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzt on 15-5-18.
 */
public class TimelineAdapter extends BaseAdapter {
    private List<ImageMessageEntity> msgLists = new ArrayList<ImageMessageEntity>();
    private Context context;
    private ImageLoader imageLoader;

    public TimelineAdapter(Context context) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
    }

    public void addAll(List<ImageMessageEntity> data) {
        msgLists.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        msgLists.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return msgLists.size();
    }

    @Override
    public ImageMessageEntity getItem(int position) {
        return msgLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int parentWidth = parent.getWidth();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cell_message, null);
            holder = new ViewHolder();
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
            holder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivLargePhoto);
            holder.tvNickname = (TextView) convertView.findViewById(R.id.tvNickname);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            holder.tvPostTime = (TextView) convertView.findViewById(R.id.tvPostTime);

            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        ViewGroup.LayoutParams lp = holder.ivPhoto.getLayoutParams();
        lp.width = parentWidth;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.ivPhoto.setLayoutParams(lp);

        holder.ivPhoto.setMaxWidth(parentWidth);
        holder.ivPhoto.setMaxHeight(parentWidth * 5);

        final ImageMessageEntity msg = getItem(position);

        holder.tvNickname.setText(msg.getNickname());
        holder.tvDescription.setText(msg.getTextDescription());
        holder.tvPostTime.setText(DateFormat.format("yyyy-MM-dd kk:mm", msg.getPostTime()));

        imageLoader.displayImage(Config.SERVER_URL + msg.getPhotoURL(), holder.ivPhoto);
        imageLoader.displayImage(Config.SERVER_URL + msg.getAvatarURL(), holder.ivAvatar);
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, LargePhotoActivity.class);
                i.putExtra(Config.KEY_PHOTO_URL, msg.getPhotoURL());
                i.putExtra(Config.KEY_MSG_ID, msg.getMsgID());
                context.startActivity(i);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivAvatar;
        ImageView ivPhoto;
        TextView tvNickname;
        TextView tvDescription;
        TextView tvPostTime;
    }
}
