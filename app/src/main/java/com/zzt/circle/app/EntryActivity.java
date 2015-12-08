package com.zzt.circle.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zzt.circle.app.activity.LargePhotoActivity;
import com.zzt.circle.app.activity.LoginActivity;
import com.zzt.circle.app.activity.MainActivity;

/**
 * Created by zzt on 15-6-6.
 */
public class EntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        startActivity(new Intent(this, LargePhotoActivity.class));

//        Config.cacheToken(this, "812B4BA287F5EE0BC9D43BBF5BBE87FB");
//        Config.cacheAccount(this, "123");
        String token = Config.getCachedToken(this);
        String account = Config.getCachedAccount(this);
        if ((token == null || token.length() == 0) || (account == null || account.length() == 0)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
//        startActivity(new Intent(this,LargePhotoActivity.class));
    }
}
