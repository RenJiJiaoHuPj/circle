package com.zzt.circle.app.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.net.PostPhoto;

import java.io.FileNotFoundException;

/**
 * Created by zzt on 15-6-15.
 */
public class PostPhotoActivity extends ActionBarActivity {
    private String account;
    private String token;
    private EditText etTextDescription;
    private ImageButton btnSend;
    private ImageView ivPhoto;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_photo);
        account = Config.getCachedAccount(this);
        token = Config.getCachedToken(this);
        etTextDescription = (EditText) findViewById(R.id.etTextDescription);
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivPhoto.getDrawable() == null) {
                    Toast.makeText(PostPhotoActivity.this, R.string.please_select_a_photo, Toast.LENGTH_LONG).show();
                    return;
                }

                String textDescription;

                if (TextUtils.isEmpty(etTextDescription.getText()))
                    textDescription = "";
                else
                    textDescription = etTextDescription.getText().toString();

                new PostPhoto(account, token, textDescription, bitmap, new PostPhoto.SuccessCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(PostPhotoActivity.this, R.string.post_success, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }, new PostPhoto.FailCallback() {
                    @Override
                    public void onFail() {
                        onFail(Config.RESULT_STATUS_FAIL);
                    }

                    @Override
                    public void onFail(int code) {
                        switch (code) {
                            case Config.RESULT_STATUS_FAIL:
                                Toast.makeText(PostPhotoActivity.this, R.string.post_failed, Toast.LENGTH_LONG).show();
                                break;
                            case Config.RESULT_STATUS_INVALID_TOKEN:
                                Toast.makeText(PostPhotoActivity.this, R.string.invalid_token_please_login_again, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(PostPhotoActivity.this, LoginActivity.class));
                        }
                    }
                });
            }
        });
        btnSend.clearFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_photo:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                break;
        }
        return true;
    }

    public void choosePhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                ivPhoto.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
