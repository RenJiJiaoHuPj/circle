package com.zzt.circle.app.net;

import com.zzt.circle.app.Config;
import com.zzt.circle.app.entity.ImageMessageEntity;
import com.zzt.circle.app.tools.MD5Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zzt on 15-6-13.
 */
public class UpdateInfo {

    public UpdateInfo(String account,String token,String nickname,String avatar,int gender,
                      String old_pwd_md5,String new_pwd_md5,
                      final SuccessCallback successCallback, final FailCallback failCallback) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Config.KEY_ACCOUNT, account);
        params.put(Config.KEY_TOKEN, token);
        params.put(Config.KEY_NICKNAME, nickname);
        //头像上传 这里avatar传的是二进制文件流
        params.put(Config.KEY_AVATAR, avatar);
        params.put(Config.KEY_GENDER, String.valueOf(gender));

        if (old_pwd_md5.length()!=0&&new_pwd_md5.length()!=0) {
            params.put(Config.KEY_OLD_PWD_MD5, MD5Utils.str2MD5(old_pwd_md5));
            params.put(Config.KEY_NEW_PWD_MD5, MD5Utils.str2MD5(new_pwd_md5));
        }

        String actionURL = Config.SERVER_URL + Config.ACTION_UPDATE_INFO + Config.SERVER_ACTION_SUFFIX;
        new NetConnection(actionURL, HttpMethod.POST, new NetConnection.SuccessCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    switch (obj.getInt(Config.KEY_STATUS)) {
                        case Config.RESULT_STATUS_SUCCESS:
                            if (successCallback != null) {
                                List<ImageMessageEntity> msgs = new ArrayList<ImageMessageEntity>();
                                String avatar_url = obj.getString(Config.KEY_AVATAR_URL);
                                successCallback.onSuccess(avatar_url);
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
        void onSuccess(String avatar_url);
    }

    public interface FailCallback {
        void onFail();

        void onFail(int failCode);
    }
}
