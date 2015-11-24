package com.zzt.circle.app.net;

import com.zzt.circle.app.Config;
import com.zzt.circle.app.entity.HotspotEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zzt on 15-5-31.
 */
public class GetHotspot {

    public GetHotspot(String account, String token, int msgID, int page, int perpage, final SuccessCallback successCallback, final FailCallback failCallback) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Config.KEY_ACCOUNT, account);
        params.put(Config.KEY_TOKEN, token);
        params.put(Config.KEY_MSG_ID, String.valueOf(msgID));
        params.put(Config.KEY_PAGE, String.valueOf(page));
        params.put(Config.KEY_PERPAGE, String.valueOf(perpage));
        String actionURL = Config.SERVER_URL + Config.ACTION_GET_HOTSPOT + Config.SERVER_ACTION_SUFFIX;
        new NetConnection(actionURL, HttpMethod.POST, new NetConnection.SuccessCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    switch (obj.getInt(Config.KEY_STATUS)) {
                        case Config.RESULT_STATUS_SUCCESS:
                            if (successCallback != null) {
                                List<HotspotEntity> hotspots = new ArrayList<HotspotEntity>();
                                JSONArray array = obj.getJSONArray(Config.KEY_HOTSPOTS);
                                JSONObject hotspot;
                                for (int i = 0; i < array.length(); i++) {
                                    hotspot = array.getJSONObject(i);
                                    hotspots.add(new HotspotEntity(hotspot.getInt(Config.KEY_HOTSPOT_ID),
                                            hotspot.getInt(Config.KEY_X),
                                            hotspot.getInt(Config.KEY_Y), hotspot.getInt(Config.KEY_COUNT)));
                                }
                                successCallback.onSuccess(hotspots);
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
        void onSuccess(List<HotspotEntity> hotspots);
    }

    public interface FailCallback {
        void onFail();
    }
}
