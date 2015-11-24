package com.zzt.circle.app.tools;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import org.json.JSONArray;

/**
 * Created by zzt on 15-6-14.
 */
public class MyContacts {

    public static String getContactsJSONString(Context context) {
        Cursor c = context.getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
        String phoneNum;
        JSONArray jsonArray = new JSONArray();
        while (c.moveToNext()) {
            phoneNum = c.getString(c.getColumnIndex(Phone.NUMBER));
            if (phoneNum.charAt(0) == '+') {
                phoneNum = phoneNum.substring(3);
            }
            jsonArray.put(MD5Utils.str2MD5(phoneNum));

        }
        return jsonArray.toString();
    }
}
