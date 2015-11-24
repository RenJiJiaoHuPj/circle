package com.zzt.circle.app.tools;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by zzt on 15-6-14.
 */
public class ImageUtil {

    public static String Bitmap2StrByBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        try {

            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();
        System.out.println(bytes.length);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
