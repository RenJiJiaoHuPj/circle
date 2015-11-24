package com.zzt.circle.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.zzt.circle.app.R;
import com.zzt.circle.app.net.Register;
import com.zzt.circle.app.tools.MD5Utils;

/**
 * Created by zzt on 15-4-24.
 */
public class RegisterActivity extends ActionBarActivity {
    private EditText etAccount, etNickname, etPassword, etConfirmPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etAccount = (EditText) findViewById(R.id.etAccount);
        etNickname = (EditText) findViewById(R.id.etNickname);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString();
                String nickname = etNickname.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                if (TextUtils.isEmpty(etAccount.getText()) || TextUtils.isEmpty(etNickname.getText())
                        || TextUtils.isEmpty(etPassword.getText()) || TextUtils.isEmpty(etConfirmPassword.getText())) {
                    Toast.makeText(RegisterActivity.this, R.string.please_complete_above_info, Toast.LENGTH_LONG).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, R.string.inconsistent_passwords, Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog pd = ProgressDialog.show(RegisterActivity.this, getString(R.string.connecting), getString(R.string.login_now));


                new Register(account, MD5Utils.str2MD5(password), nickname, new Register.SuccessCallback() {
                    @Override
                    public void onSuccess() {
                        pd.dismiss();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                        finish();
                    }
                }, new Register.FailCallback() {
                    @Override
                    public void onFail() {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, R.string.fail_to_register, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

}