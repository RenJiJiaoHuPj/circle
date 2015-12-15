package com.zzt.circle.app.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.activity.LoginActivity;
import com.zzt.circle.app.net.UpdateInfo;
import com.zzt.circle.app.tools.ImageUtil;

import java.io.FileNotFoundException;

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
    private boolean editMode = false;

    private ImageLoader imageLoader;

    private TextView tv_nickname;
    private EditText et_nickname;
    private TextView label_nickname;
    private TextView label_gender;
    private ImageView iv_image;
    private EditText et_gender;
    private TextView tv_gender;
    private LinearLayout toggle_gender;
    private Switch gender_switch;

    private Button setInfo;
    private static Uri selectedImgUri;


    private View.OnClickListener onAvatarClick = new View.OnClickListener() {
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
    };
    private TextView label_male;
    private TextView label_female;

    public MyInfoFragment() {
        // Required empty public constructor
    }

    public static void setSelectedImgUri(Uri selectedImgUri) {
        MyInfoFragment.selectedImgUri = selectedImgUri;
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

        //fuzhi
        et_gender = (EditText) rootView.findViewById(R.id.gender);
        et_nickname = (EditText) rootView.findViewById(R.id.nickname);
        iv_image = (ImageView) rootView.findViewById(R.id.iv_image);
        tv_nickname = (TextView) rootView.findViewById(R.id.tv_nickname);
        label_nickname = (TextView) rootView.findViewById(R.id.label_nickname);
        label_gender = (TextView) rootView.findViewById(R.id.label_gender);
        tv_gender = (TextView) rootView.findViewById(R.id.tv_gender);
        toggle_gender = (LinearLayout) rootView.findViewById(R.id.toggle_gender);
        gender_switch = (Switch) rootView.findViewById(R.id.gender_switch);
        label_male = (TextView) rootView.findViewById(R.id.label_male);
        label_female = (TextView) rootView.findViewById(R.id.label_female);

        gender_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isMale = getNewGender() == 1;
                updateSwitchState(isMale);
            }
        });

//        et_gender.setEnabled(false);
//        et_nickname.setEnabled(false);

        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(Config.SERVER_URL + avatarUrl, iv_image);
        et_nickname.setText(nickname);
        et_gender.setText(gender);
        tv_nickname.setText(nickname);
        tv_gender.setText(gender);

        setInfo = (Button) rootView.findViewById(R.id.setInfo);
        setInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: animation
                if (editMode) {

                    updateInfo();

                    editMode = false;

                    setInfo.setText(R.string.change_profile);

                    label_nickname.setVisibility(View.GONE);
                    et_nickname.setVisibility(View.GONE);
                    label_gender.setVisibility(View.GONE);
                    toggle_gender.setVisibility(View.GONE);

                    tv_nickname.setText(nickname);
                    tv_gender.setText(gender);
                    iv_image.setOnClickListener(null);

                    tv_nickname.setVisibility(View.VISIBLE);
                    tv_gender.setVisibility(View.VISIBLE);
                } else {
                    editMode = true;

                    setInfo.setText(R.string.finish);

                    tv_nickname.setVisibility(View.GONE);
                    tv_gender.setVisibility(View.GONE);

                    et_nickname.setText(nickname);
                    updateSwitchState(gender.equals(getString(R.string.male)));
                    iv_image.setOnClickListener(onAvatarClick);

                    label_nickname.setVisibility(View.VISIBLE);
                    et_nickname.setVisibility(View.VISIBLE);
                    label_gender.setVisibility(View.VISIBLE);
                    toggle_gender.setVisibility(View.VISIBLE);
                }
            }

            private void updateInfo() {
                final ProgressDialog pd = ProgressDialog.show(getActivity(), getString(R.string.now_loading), getString(R.string.please_waite));
                String img = "";

                ContentResolver cr = getActivity().getContentResolver();
                Bitmap bitmap = null;
                try {
                    if (selectedImgUri != null) {
                        bitmap = BitmapFactory.decodeStream(cr.openInputStream(selectedImgUri));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (bitmap == null) {
                    img = "";
                } else {
                    img = ImageUtil.Bitmap2StrByBase64(bitmap);
                }

                final String newNickname = et_nickname.getText().toString();
                final int newGender = getNewGender();

                new UpdateInfo(account, token, newNickname,
                        img, newGender,
                        "", "",

                        new UpdateInfo.SuccessCallback() {
                            @Override
                            public void onSuccess(String avatag_url) {
                                pd.dismiss();

                                Config.cacheAvatagUrl(getActivity(), avatag_url);
                                Config.cacheNickname(getActivity(), newNickname);
                                Config.cacheGender(getActivity(), (newGender == 1) ? getString(R.string.male) : getString(R.string.female));

                                gender = Config.getCachedGender(getActivity());
                                nickname = Config.getCachedNickname(getActivity());
                                avatarUrl = Config.getCachedAvatagUrl(getActivity());

                                tv_nickname.setText(nickname);
                                tv_gender.setText(gender);
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
        });

        lazyLoad();
        return rootView;
    }

    private void updateSwitchState(boolean isMale) {
        gender_switch.setChecked(!isMale);
        if (isMale) {
            label_male.setTextColor(Color.BLACK);
            label_female.setTextColor(Color.GRAY);
        } else {
            label_male.setTextColor(Color.GRAY);
            label_female.setTextColor(Color.BLACK);
        }
    }

    /**
     * @return 1(male), 0(female)
     */
    private int getNewGender() {
        return gender_switch.isChecked() ? 0 : 1;
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible) {
            loadMessage();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        lazyLoad();
    }

    private void loadMessage() {
    }


}
