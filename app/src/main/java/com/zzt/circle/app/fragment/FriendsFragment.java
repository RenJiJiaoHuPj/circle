package com.zzt.circle.app.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.activity.LoginActivity;
import com.zzt.circle.app.adapter.FriendsAdapter;
import com.zzt.circle.app.entity.UserEntity;
import com.zzt.circle.app.net.AddFriend;
import com.zzt.circle.app.net.LoadFriends;
import com.zzt.circle.app.net.SearchFriend;
import com.zzt.circle.app.net.UpdateInfo;

import java.util.List;

/**
 * Created by zzt on 15-6-6.
 */
public class FriendsFragment extends LazyFragment {
    private ListView lvContact;
    private FriendsAdapter adapter;
    private String token;
    private String account;
    private boolean isPrepared;
    private EditText userNickname;
    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        account = Config.getCachedAccount(getActivity());
        token = Config.getCachedToken(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        lvContact = (ListView) rootView.findViewById(R.id.lvFriends);
        adapter = new FriendsAdapter(getActivity());
        lvContact.setAdapter(adapter);
        isPrepared = true;

        userNickname = (EditText) rootView.findViewById(R.id.userNickname);
        userNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (userNickname.getText().toString().length() == 0)
                    loadFriends();
                else
                    new SearchFriend(account, token, userNickname.getText().toString(),
                            new SearchFriend.SuccessCallback() {
                                @Override
                                public void onSuccess(List<UserEntity> friends) {
                                    adapter.setIsAddFriend(true);
                                    adapter.addAll(friends);
                                    lvContact.setAdapter(adapter);
                                }
                            },
                            new SearchFriend.FailCallback() {
                                @Override
                                public void onFail() {
                                    onFail(Config.RESULT_STATUS_FAIL);
                                }

                                @Override
                                public void onFail(int failCode) {
                                    switch (failCode) {
                                        case Config.RESULT_STATUS_FAIL:
                                            Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_LONG).show();
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

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible) {
            loadFriends();
        } else return;
    }

    @Override
    public void onResume() {
        super.onResume();
        lazyLoad();
    }

    private void loadFriends() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), getString(R.string.now_loading), getString(R.string.please_waite));

        new LoadFriends(account, token, new LoadFriends.SuccessCallback() {
            @Override
            public void onSuccess(List<UserEntity> friends) {
                pd.dismiss();
                adapter = new FriendsAdapter(getActivity());
                adapter.setIsAddFriend(false);
                adapter.addAll(friends);
                lvContact.setAdapter(adapter);
            }
        }, new LoadFriends.FailCallback() {
            @Override
            public void onFail() {
                onFail(Config.RESULT_STATUS_FAIL);
            }

            @Override
            public void onFail(int failCode) {
                pd.dismiss();
                switch (failCode) {
                    case Config.RESULT_STATUS_FAIL:
                        Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_LONG).show();
                        break;
                    case Config.RESULT_STATUS_INVALID_TOKEN:
                        Toast.makeText(getActivity(), R.string.invalid_token_please_login_again, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        break;
                }
            }
        });
    }
}
