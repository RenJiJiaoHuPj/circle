package com.zzt.circle.app.net;

import com.zzt.circle.app.Config;
import com.zzt.circle.app.entity.ImageMessageEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zzt on 15-6-13.
 */
public class UploadContacts {
    //TODO 根据接口修改
    public UploadContacts(String account, String token, int page, int perpage, final SuccessCallback successCallback, final FailCallback failCallback) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Config.KEY_ACCOUNT, account);
        params.put(Config.KEY_TOKEN, token);
        params.put(Config.KEY_PAGE, String.valueOf(page));
        params.put(Config.KEY_PERPAGE, String.valueOf(perpage));
        String actionURL = Config.SERVER_URL + Config.ACTION_TIMELINE + Config.SERVER_ACTION_SUFFIX;
        new NetConnection(actionURL, HttpMethod.POST, new NetConnection.SuccessCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    switch (obj.getInt(Config.KEY_STATUS)) {
                        case Config.RESULT_STATUS_SUCCESS:
                            if (successCallback != null) {
                                List<ImageMessageEntity> msgs = new ArrayList<ImageMessageEntity>();
                                JSONArray timeline = obj.getJSONArray(Config.KEY_TIMELINE);
                                JSONObject msgObj;
                                for (int i = 0; i < timeline.length(); i++) {
                                    msgObj = timeline.getJSONObject(i);
                                    msgs.add(new ImageMessageEntity(msgObj.getInt(Config.KEY_MSG_ID),
                                            msgObj.getString(Config.KEY_AVATAR_URL),
                                            msgObj.getString(Config.KEY_NICKNAME),
                                            msgObj.getLong(Config.KEY_POST_TIME),
                                            msgObj.getString(Config.KEY_PHOTO_URL),
                                            msgObj.getString(Config.KEY_TEXT_DESCRIPTION)));
                                }
                                successCallback.onSuccess(msgs);
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
        void onSuccess(List<ImageMessageEntity> timeline);
    }

    public interface FailCallback {
        void onFail();

        void onFail(int failCode);
    }
}
