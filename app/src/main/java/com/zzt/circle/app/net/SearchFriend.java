package com.zzt.circle.app.net;

import com.zzt.circle.app.Config;
import com.zzt.circle.app.entity.UserEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zzt on 15-6-13.
 */
public class SearchFriend {

    public SearchFriend(String account, String token, String friendNickname,
                        final SuccessCallback successCallback, final FailCallback failCallback) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Config.KEY_ACCOUNT, account);
        params.put(Config.KEY_TOKEN, token);
        params.put(Config.KEY_NICKNAME, friendNickname);

        String actionURL = Config.SERVER_URL + Config.ACTION_SEARCH_USER + Config.SERVER_ACTION_SUFFIX;
        new NetConnection(actionURL, HttpMethod.POST, new NetConnection.SuccessCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    switch (obj.getInt(Config.KEY_STATUS)) {
                        case Config.RESULT_STATUS_SUCCESS:
                            if (successCallback != null) {
                                JSONArray array = obj.getJSONArray(Config.KEY_PERSON);
                                List<UserEntity> friends = new ArrayList<UserEntity>();
                                JSONObject friend;
                                for (int i = 0; i < array.length(); i++) {
                                    friend = array.getJSONObject(i);
                                    friends.add(new UserEntity(friend.getString(Config.KEY_ACCOUNT),
                                            friend.getString(Config.KEY_NICKNAME),
                                            friend.getInt(Config.KEY_GENDER),
                                            friend.getString(Config.KEY_AVATAR_URL)));
                                }
                                successCallback.onSuccess(friends);
                            }
                            break;
                        default:
                            if (failCallback != null)
                                failCallback.onFail(obj.getInt(Config.KEY_STATUS));
                            break;
                    }
                } catch (JSONException e) {
                    if (failCallback != null)
                        failCallback.onFail();
                }
            }
        }, new NetConnection.FailCallBack() {
            @Override
            public void onFail() {

            }
        }, params);
    }

    public interface SuccessCallback {
        void onSuccess(List<UserEntity> friends);
    }

    public interface FailCallback {
        void onFail();

        void onFail(int failCode);
    }
}
