package com.zzt.circle.app.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.activity.LoginActivity;
import com.zzt.circle.app.activity.MainActivity;
import com.zzt.circle.app.adapter.TimelineAdapter;
import com.zzt.circle.app.entity.ImageMessageEntity;
import com.zzt.circle.app.net.Timeline;
import com.zzt.circle.app.net.UpdateInfo;
import com.zzt.circle.app.net.UploadContacts;
import com.zzt.circle.app.tools.ImageUtil;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
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

    private EditText tv_account;
    private EditText tv_gender;
    private EditText tv_nickname;
    private ImageView iv_image;

    private Button setInfo;
    private boolean canClick;
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
        tv_account = (EditText) rootView.findViewById(R.id.account);
        tv_gender = (EditText) rootView.findViewById(R.id.gender);
        tv_nickname = (EditText) rootView.findViewById(R.id.nickname);
        iv_image = (ImageView) rootView.findViewById(R.id.iv_image);

        tv_account.setEnabled(false);
        tv_gender.setEnabled(false);
        tv_nickname.setEnabled(false);

        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(Config.SERVER_URL + avatagUrl, iv_image);
        tv_nickname.setText(nickname);
        tv_gender.setText(gender);
        tv_account.setText(account);

        rootView.findViewById(R.id.quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        setInfo = (Button) rootView.findViewById(R.id.setInfo);
        setInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setInfo.getText().equals("设置")) {
//                    tv_account.setEnabled(true);
                    tv_gender.setEnabled(true);
                    tv_nickname.setEnabled(true);
                    setInfo.setText("提交");
                } else if (setInfo.getText().equals("提交")) {
                    final ProgressDialog pd = ProgressDialog.show(getActivity(), getString(R.string.now_loading), getString(R.string.please_waite));
                    String img = "";
                    Bitmap bitmap = iv_image.getDrawingCache();
                    iv_image.setDrawingCacheEnabled(false);
                    if (bitmap == null) {
                        img = "";
                    } else {
                        img = ImageUtil.Bitmap2StrByBase64(bitmap);
                    }

                    new UpdateInfo(tv_account.getText().toString(), token, tv_nickname.getText().toString(),
                            img, tv_gender.getText().toString().equals("男") ? 1 : 0,
                            "", "",

                            new UpdateInfo.SuccessCallback() {
                                @Override
                                public void onSuccess(String avatag_url) {
                                    pd.dismiss();
                                    imageLoader.displayImage(Config.SERVER_URL + avatagUrl, iv_image);
                                    Toast.makeText(getActivity(), "更新成功!", Toast.LENGTH_LONG).show();

                                }
                            },
                            new UpdateInfo.FailCallback() {
                                @Override
                                public void onFail() {
                                    onFail(Config.RESULT_STATUS_FAIL);
                                }

                                @Override
                                public void onFail(int failCode) {
                                    pd.dismiss();
                                    switch (failCode) {
                                        case Config.RESULT_STATUS_FAIL:
                                            Toast.makeText(getActivity(), "更新失败!", Toast.LENGTH_LONG).show();
                                            break;
                                        case Config.RESULT_STATUS_INVALID_TOKEN:
                                            Toast.makeText(getActivity(), R.string.invalid_token_please_login_again, Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getActivity(), LoginActivity.class));
                                            break;
                                    }
                                }
                            });
                    setInfo.setText("设置");
                }

            }
        });

        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //TODO 获得一张图片
                    Intent intent = new Intent();
                    /* 开启Pictures画面Type设定为image */
                    intent.setType("image/*");
                    /* 使用Intent.ACTION_GET_CONTENT这个Action */
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    /* 取得相片后返回MainActivity */
                    startActivityForResult(intent, 1);

            }
        });

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
