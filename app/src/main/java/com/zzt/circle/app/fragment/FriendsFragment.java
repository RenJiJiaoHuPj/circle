package com.zzt.circle.app.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.activity.LoginActivity;
import com.zzt.circle.app.adapter.FriendsAdapter;
import com.zzt.circle.app.entity.UserEntity;
import com.zzt.circle.app.net.LoadFriends;

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
                adapter.clear();
                adapter.addAll(friends);
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
