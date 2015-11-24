package com.zzt.circle.app.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.activity.LoginActivity;
import com.zzt.circle.app.adapter.TimelineAdapter;
import com.zzt.circle.app.entity.ImageMessageEntity;
import com.zzt.circle.app.net.Timeline;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends LazyFragment {

    private ListView lvTimeline;
    private TimelineAdapter timelineAdapter;
    private String account;
    private String token;
    private boolean isPrepared;


    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        account = Config.getCachedAccount(getActivity());
        token = Config.getCachedToken(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        lvTimeline = (ListView) rootView.findViewById(R.id.lvTimeline);
        timelineAdapter = new TimelineAdapter(getActivity());
        lvTimeline.setAdapter(timelineAdapter);
        isPrepared = true;
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
        final ProgressDialog pd = ProgressDialog.show(getActivity(), getString(R.string.now_loading), getString(R.string.please_waite));
        new Timeline(account, token, 1, 20,
                new Timeline.SuccessCallback() {
                    @Override
                    public void onSuccess(List<ImageMessageEntity> timeline) {
                        pd.dismiss();
                        timelineAdapter.clear();
                        timelineAdapter.addAll(timeline);
                    }
                },
                new Timeline.FailCallback() {
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
