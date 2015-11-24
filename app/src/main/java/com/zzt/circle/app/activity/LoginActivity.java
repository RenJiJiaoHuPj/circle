package com.zzt.circle.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.zzt.circle.app.Config;
import com.zzt.circle.app.R;
import com.zzt.circle.app.net.Login;
import com.zzt.circle.app.tools.MD5Utils;

/**
 * Created by zzt on 15-4-24.
 */
public class LoginActivity extends ActionBarActivity {
    private EditText etAccount;
    private EditText etPassword;
    private EditText etIP;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etAccount = (EditText) findViewById(R.id.etAccount);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etIP = (EditText) findViewById(R.id.etIP);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etAccount.getText())) {
                    Toast.makeText(LoginActivity.this, R.string.please_enter_your_account, Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(etPassword.getText())) {
                    Toast.makeText(LoginActivity.this, R.string.please_enter_yout_password, Toast.LENGTH_LONG).show();
                    return;
                }
                Config.SERVER_URL = etIP.getText().toString();
                Toast.makeText(LoginActivity.this, "IP:"+Config.SERVER_URL, Toast.LENGTH_LONG).show();
                final ProgressDialog pd = ProgressDialog.show(LoginActivity.this, getString(R.string.connecting), getString(R.string.login_now));

                new Login(etAccount.getText().toString(), MD5Utils.str2MD5(etPassword.getText().toString()),
                        new Login.SuccessCallback() {
                            @Override
                            public void onSuccess(String token,String gender,String avatagUrl,String nickname) {
                                pd.dismiss();
                                Config.cacheToken(LoginActivity.this, token);
                                Config.cacheAccount(LoginActivity.this, etAccount.getText().toString());
                                Config.cacheGender(LoginActivity.this, gender);
                                Config.cacheAvatagUrl(LoginActivity.this, avatagUrl);
                                Config.cacheNickname(LoginActivity.this, nickname);
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.putExtra(Config.KEY_TOKEN, token);
                                startActivity(i);

                                finish();
                            }
                        }, new Login.FailCallback() {
                    @Override
                    public void onFail() {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, R.string.fail_to_login, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        findViewById(R.id.btnRegisterNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}