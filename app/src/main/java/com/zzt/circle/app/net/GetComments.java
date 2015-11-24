package com.zzt.circle.app.net;

import com.zzt.circle.app.Config;
import com.zzt.circle.app.entity.CommentEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzt on 15-6-14.
 */
public class GetComments {
    public GetComments(String account, String token, int hotspot_id, int page, int perpage, final SuccessCallback successCallback, final FailCallback failCallback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Config.KEY_ACCOUNT, account);
        params.put(Config.KEY_TOKEN, token);
        params.put(Config.KEY_HOTSPOT_ID, String.valueOf(hotspot_id));
        params.put(Config.KEY_PAGE, String.valueOf(page));
        params.put(Config.KEY_PERPAGE, String.valueOf(perpage));

        String actionURL = Config.SERVER_URL + Config.ACTION_GET_COMMENTS + Config.SERVER_ACTION_SUFFIX;

        new NetConnection(actionURL, HttpMethod.POST, new NetConnection.SuccessCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    switch (object.getInt(Config.KEY_STATUS)) {
                        case Config.RESULT_STATUS_SUCCESS:
                            if (successCallback != null) {
                                List<CommentEntity> comments = new ArrayList<CommentEntity>();
                                JSONArray array = object.getJSONArray(Config.KEY_COMMENTS);
                                JSONObject comment;
                                for (int i = 0; i < array.length(); i++) {
                                    comment = array.getJSONObject(i);
                                    comments.add(new CommentEntity(comment.getInt(Config.KEY_COMMENT_ID),
                                            comment.getString(Config.KEY_NICKNAME),
                                            comment.getString(Config.KEY_AVATAR_URL),
                                            comment.getString(Config.KEY_CONTENT),
                                            comment.getLong(Config.KEY_POST_TIME)));
                                }
                                successCallback.onSuccess(comments);
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
        void onSuccess(List<CommentEntity> comments);
    }

    public interface FailCallback {
        void onFail();

        void onFail(int code);
    }
}
