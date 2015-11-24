package com.zzt.circle.app.net;

import com.zzt.circle.app.Config;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by zzt on 15-5-30.
 */
public class Login {

    public Login(String account, String password_md5, final SuccessCallback successCallback, final FailCallback failCallback) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Config.KEY_ACCOUNT, account);
        params.put(Config.KEY_PASSWORD_MD5, password_md5);
        String actionURL = Config.SERVER_URL + Config.ACCTION_LOGIN + Config.SERVER_ACTION_SUFFIX;
        new NetConnection(actionURL, HttpMethod.POST, new NetConnection.SuccessCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    switch (obj.getInt(Config.KEY_STATUS)) {
                        case Config.RESULT_STATUS_SUCCESS:
                            if (successCallback != null) {
                                String token = obj.getString(Config.KEY_TOKEN);
                                int gender = obj.getInt(Config.KEY_GENDER);
                                String avatagUrl = obj.getString(Config.KEY_AVATAR_URL);
                                String nickname = obj.getString(Config.KEY_NICKNAME);
                                successCallback.onSuccess(token,gender==1?"男":"女",avatagUrl,nickname);
                            }
                            break;
                        default:
                            if (failCallback != null)
                                failCallback.onFail();
                    }
                } catch (JSONException e) {
                    if (failCallback != null)
                        failCallback.onFail();
                }
            }
        }, new NetConnection.FailCallBack() {
            @Override
            public void onFail() {
                if (failCallback != null)
                    failCallback.onFail();

            }
        }, params);
    }

    public interface SuccessCallback {
        void onSuccess(String token,String gender,String avatagUrl,String nickname);
    }

    public interface FailCallback {
        void onFail();
    }
}
