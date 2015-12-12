package com.zzt.circle.app.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.activity.LoginActivity;
import com.zzt.circle.app.net.UpdateInfo;
import com.zzt.circle.app.tools.ImageUtil;
import com.zzt.circle.app.tools.MD5Utils;

/**
 *
 */
public class MyInfoFragment extends LazyFragment {
//    private

    private String account;
    private String gender;
    private String token;
    private String nickname;
    private String avatarUrl;
    private boolean isPrepared;

    private ImageLoader imageLoader;

    private TextView tv_pw_old;
    private TextView tv_pw_new;
    private TextView tv_pw_new2;
    private EditText et_pw;
    private EditText et_pw2;
    private EditText et_pw_old;
    private EditText et_gender;
    private EditText et_nickname;
    private ImageView iv_image;

    private Button setInfo;
    private Button delsetInfo;

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
        avatarUrl = Config.getCachedAvatagUrl(getActivity());
        final View rootView = inflater.inflate(R.layout.fragment_myinfo, container, false);
        isPrepared = true;

        // 赋值
        et_gender = (EditText) rootView.findViewById(R.id.gender);
        et_nickname = (EditText) rootView.findViewById(R.id.nickname);
        iv_image = (ImageView) rootView.findViewById(R.id.iv_image);

        et_gender.setEnabled(false);
        et_nickname.setEnabled(false);

        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(Config.SERVER_URL + avatarUrl, iv_image);
        et_nickname.setText(nickname);
        et_gender.setText(gender);

        tv_pw_new = (TextView) rootView.findViewById(R.id.tv_pw_new);
        tv_pw_new2 = (TextView) rootView.findViewById(R.id.tv_pw_new2);
        tv_pw_old = (TextView) rootView.findViewById(R.id.tv_pw_old);
        et_pw = (EditText) rootView.findViewById(R.id.et_pw);
        et_pw2 = (EditText) rootView.findViewById(R.id.et_pw2);
        et_pw_old = (EditText) rootView.findViewById(R.id.et_pw_old);
        delsetInfo = (Button) rootView.findViewById(R.id.delsetInfo);

        setInfo = (Button) rootView.findViewById(R.id.setInfo);
        setInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setInfo.getText().equals("设置")) {
                    setMyinfoVisible(View.VISIBLE);
                    setInfo.setText("提交");
                } else if (setInfo.getText().equals("提交")) {
                    // 判断是否输入密码及新密码是否输入相同
                    EditText et_pw = (EditText) rootView.findViewById(R.id.et_pw);
                    EditText et_pw_old = (EditText) rootView.findViewById(R.id.et_pw_old);
                    boolean isUpdatePw = false, isfalse = false;
                    if (et_pw.getText().length() != 0 && et_pw_old.getText().length() != 0
                            && (et_pw.getText().equals(et_pw2.getText()))) {
                        isUpdatePw = true;
                    } else if (et_pw.getText().length() != 0) {
                        Toast.makeText(getActivity(), "两次密码输入不一致!", Toast.LENGTH_LONG).show();
                        isfalse = true;
                    }

                    if (!isfalse) {
                        final ProgressDialog pd = ProgressDialog.show(getActivity(), getString(R.string.now_loading), getString(R.string.please_waite));
                        String img = "";
                        Bitmap bitmap = iv_image.getDrawingCache();
                        iv_image.setDrawingCacheEnabled(false);
                        if (bitmap == null) {
                            img = "";
                        } else {
                            img = ImageUtil.Bitmap2StrByBase64(bitmap);
                        }
                        new UpdateInfo(account, token, et_nickname.getText().toString(),
                                img, et_gender.getText().toString().equals("男") ? 1 : 0,
                                isUpdatePw ? MD5Utils.str2MD5(et_pw_old.getText().toString()) : "",
                                isUpdatePw ? MD5Utils.str2MD5(et_pw.getText().toString()) : "",

                                new UpdateInfo.SuccessCallback() {
                                    @Override
                                    public void onSuccess(String avatag_url) {
                                        pd.dismiss();
                                        imageLoader.displayImage(Config.SERVER_URL + avatarUrl, iv_image);
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
                    }
                    //恢复为不可设置及隐藏
                    setMyinfoVisible(View.GONE);
                    setInfo.setText("设置");
                }
            }
        });
        // 选择图片
        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_nickname.isEnabled()) {
                    Intent intent = new Intent();
                    /* 开启Pictures画面Type设定为image */
                    intent.setType("image/*");
                    /* 使用Intent.ACTION_GET_CONTENT这个Action */
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    /* 取得相片后返回MainActivity */
                    startActivityForResult(intent, 1);
                }
            }
        });
        //
        delsetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //恢复为不可设置及隐藏
                setMyinfoVisible(View.GONE);
                setInfo.setText("设置");
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

    private void setMyinfoVisible(int view) {
        tv_pw_new.setVisibility(view);
        et_pw.setVisibility(view);
        tv_pw_new2.setVisibility(view);
        et_pw2.setVisibility(view);
        et_pw_old.setVisibility(view);
        tv_pw_old.setVisibility(view);
        delsetInfo.setVisibility(view);

        if (View.VISIBLE == view) {
            et_gender.setEnabled(true);
            et_nickname.setEnabled(true);
        } else {
            et_gender.setEnabled(false);
            et_nickname.setEnabled(false);
        }
    }

}
