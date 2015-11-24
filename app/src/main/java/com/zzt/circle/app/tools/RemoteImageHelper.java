package com.zzt.circle.app.tools;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import com.zzt.circle.app.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zzt on 15-5-18.
 */
public class RemoteImageHelper {
    private final Map<String, SoftReference<Drawable>> cache = new HashMap<String, SoftReference<Drawable>>();

    private InputStream download(String urlString) throws MalformedURLException, IOException {
        InputStream inputStream = (InputStream) new URL(urlString).getContent();
        return inputStream;
    }

    public void loadImage(final ImageView imageView, final String urlString, boolean useCache) {
        if (useCache&&cache.containsKey(urlString)) {
            imageView.setImageDrawable(cache.get(urlString).get());
        }

        imageView.setImageResource(R.mipmap.ic_launcher);

        Log.d(this.getClass().getSimpleName(), "Image url : " + urlString);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageView.setImageDrawable((Drawable) message.obj);
            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Drawable drawable = null;
                try {
                    InputStream inputStream = download(urlString);
                    drawable = Drawable.createFromStream(inputStream, "src");
                    if (drawable != null) {
                        cache.put(urlString, new SoftReference<Drawable>(drawable));
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), "Image download failed", e);
                }

                Message msg = handler.obtainMessage(1, drawable);
                handler.sendMessage(msg);
            }
        };
        new Thread(runnable).start();
    }
}
