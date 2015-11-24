package com.zzt.circle.app.net;

import android.graphics.Bitmap;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.tools.ImageUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zzt on 15-6-14.
 */
public class PostPhoto {
    public PostPhoto(String account, String token, String textDescription, Bitmap photo, final SuccessCallback successCallback, final FailCallback failCallback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Config.KEY_ACCOUNT, account);
        params.put(Config.KEY_TOKEN, token);
        params.put(Config.KEY_PHOTO, ImageUtil.Bitmap2StrByBase64(photo));
        String s = ImageUtil.Bitmap2StrByBase64(photo);
        System.out.println(s.length());
        params.put(Config.KEY_TEXT_DESCRIPTION, textDescription);
        String actionURL = Config.SERVER_URL + Config.ACTION_POST_PHOTO + Config.SERVER_ACTION_SUFFIX;
        new NetConnection(actionURL, HttpMethod.POST, new NetConnection.SuccessCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    switch (object.getInt(Config.KEY_STATUS)) {
                        case Config.RESULT_STATUS_SUCCESS:
                            if (successCallback != null)
                                successCallback.onSuccess();
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
