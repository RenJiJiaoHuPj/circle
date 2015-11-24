package com.zzt.circle.app.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.activity.LoginActivity;
import com.zzt.circle.app.adapter.TimelineAdapter;
import com.zzt.circle.app.entity.ImageMessageEntity;
import com.zzt.circle.app.net.Timeline;
import com.zzt.circle.app.net.UploadContacts;

import java.util.List;

/**
 * Created by zzt on 15-6-15.
 */
public class MyInfoFragment extends LazyFragment {
//    private

    private String account;
    private String gender;
    private String token;
    private String nickname;
    private String avatagUrl;
    private boolean isPrepared;

    private ImageLoader imageLoader;

    private TextView tv_account;
    private TextView tv_gender;
    private TextView tv_nickname;
    private ImageView iv_image;


    public MyInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        account = Config.getCachedAccount(getActivity());
        token = Config.getCachedToken(getActivity());
        gender = Config.getCachedGender(getActivity());
        nickname = Config.getCachedNickname(getActivity());
        avatagUrl = Config.getCachedAvatagUrl(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_myinfo, container, false);
        isPrepared = true;

        //fuzhi
        tv_account = (TextView) rootView.findViewById(R.id.account);
        tv_gender = (TextView) rootView.findViewById(R.id.gender);
        tv_nickname = (TextView) rootView.findViewById(R.id.nickname);
        iv_image = (ImageView) rootView.findViewById(R.id.image);
        //TODO 从地址得到头像
        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(Config.SERVER_URL + avatagUrl, iv_image);
        tv_nickname.setText(nickname);
        tv_gender.setText(gender);
        tv_account.setText(account);

        lazyLoad();
        return rootView;
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible) {
            loadMessage();
        } else return;
    }

    @Override
    public void onResume() {
        super.onResume();
        lazyLoad();
    }

    private void loadMessage() {
    }


}
