package com.zzt.circle.app.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.activity.LoginActivity;
import com.zzt.circle.app.net.UpdateInfo;
import com.zzt.circle.app.tools.MD5Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePassword#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePassword extends DialogFragment {

    public ChangePassword() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChangePassword.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePassword newInstance() {
        ChangePassword fragment = new ChangePassword();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        Button submit = (Button) view.findViewById(R.id.submit);
        final EditText oldPasswordTextView = (EditText) view.findViewById(R.id.old_password);
        final EditText newPasswordTextView = (EditText) view.findViewById(R.id.new_password);
        final EditText newPassword2TextView = (EditText) view.findViewById(R.id.repeat_new_password);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword =  oldPasswordTextView.getText().toString();
                String newPassword =  newPasswordTextView.getText().toString();
                String newPassword2 =  newPassword2TextView.getText().toString();

                if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(newPassword2)) {
                    return;
                }

                if (!newPassword.equals(newPassword2)) {
                    Toast.makeText(getActivity(), getString(R.string.inconsistent_passwords), Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog pd = ProgressDialog.show(getActivity(), getString(R.string.now_loading), getString(R.string.please_waite));
                System.out.println("Change password. newPassword = " + newPassword);
                new UpdateInfo(Config.getCachedAccount(getActivity()),
                        Config.getCachedToken(getActivity()),
                        Config.getCachedNickname(getActivity()),
                        "",
                        Config.getCachedGender(getActivity()).equals(getString(R.string.male)) ? 1 : 0,
                        MD5Utils.str2MD5(oldPassword),
                        MD5Utils.str2MD5(newPassword),
                        new UpdateInfo.SuccessCallback() {
                            @Override
                            public void onSuccess(String avatar_url) {
                                pd.dismiss();
                                Toast.makeText(getActivity(), R.string.update_success, Toast.LENGTH_LONG).show();
                                dismiss();
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
                        }
                );
            }
        });

        return view;
    }

}
