package com.zzt.circle.app.net;

import com.zzt.circle.app.Config;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zzt on 15-6-14.
 */
public class PostComment {
    public PostComment(String account, String token, String content, int x, int y, int msgID, final SuccessCallback successCallback, final FailCallback failCallback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Config.KEY_ACCOUNT, account);
        params.put(Config.KEY_TOKEN, token);
        params.put(Config.KEY_CONTENT, content);
        params.put(Config.KEY_X, String.valueOf(x));
        params.put(Config.KEY_Y, String.valueOf(y));
        params.put(Config.KEY_MSG_ID, String.valueOf(msgID));

        String actionURL = Config.SERVER_URL + Config.ACTION_POST_COMMENT + Config.SERVER_ACTION_SUFFIX;

        new NetConnection(actionURL, HttpMethod.POST, new NetConnection.SuccessCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    switch (object.getInt(Config.KEY_STATUS)) {
                        case Config.RESULT_STATUS_SUCCESS:
                            if (successCallback != null) {
                                successCallback.onSuccess();
                            }
                            break;
                        default:
                            if (failCallback != null)
                                failCallback.onFail(object.getInt(Config.KEY_STATUS));
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
        void onSuccess();
    }

    public interface FailCallback {
        void onFail();

        void onFail(int code);
    }
}
