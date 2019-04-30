package com.kloudsync.techexcel.start;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.ui.MainActivity;
import com.pgyersdk.update.PgyUpdateManager;

public class LoginActivity extends Activity {

    private TextView tv_cphone, tv_login, tv_atjoin, tv_fpass;
    private EditText et_telephone, et_password;
    private FrameLayout fl_login;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String telephone;
    private String password;
    public static LoginActivity instance = null;

    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PgyUpdateManager.register(this);
        instance = this;
        initView();

    }

    private void initView() {
        tv_cphone = (TextView) findViewById(R.id.tv_cphone);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_atjoin = (TextView) findViewById(R.id.tv_atjoin);
        tv_fpass = (TextView) findViewById(R.id.tv_fpass);
        et_telephone = (EditText) findViewById(R.id.et_telephone);
        et_password = (EditText) findViewById(R.id.et_password);
        fl_login = (FrameLayout) findViewById(R.id.fl_login);

        tv_login.setEnabled(false);
//        editListener();
        setEditChangeInput();
        getSP();
        tv_login.setOnClickListener(new myOnClick());
        tv_atjoin.setOnClickListener(new myOnClick());
        tv_fpass.setOnClickListener(new myOnClick());
        tv_cphone.setOnClickListener(new myOnClick());
    }

    private void editListener() {
        et_telephone.setOnKeyListener(Key_listener());
        et_password.setOnKeyListener(Key_listener());
    }


    @NonNull
    private View.OnKeyListener Key_listener() {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String t1 = et_telephone.getText().toString();
                    String t2 = et_password.getText().toString();
                    Log.e("haha", "KEYCODE_ENTER" + flag);
                    if (!TextUtils.isEmpty(t1) && !TextUtils.isEmpty(t2)) {
                        if (flag) {
                            GetLogin();
                        }
                        flag = !flag;
                    }
                }
                return false;
            }
        };
    }

    private void getSP() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        telephone = sharedPreferences.getString("telephone", null);
        password = com.kloudsync.techexcel.start.LoginGet.DecodeBase64Password(sharedPreferences.getString("password", ""));
        AppConfig.COUNTRY_CODE = sharedPreferences.getInt("countrycode", 86);
        et_telephone.setText(telephone);
        et_password.setText(password);
        tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);

    }

    private void setEditChangeInput() {
        et_telephone.addTextChangedListener(new myTextWatch());
        et_password.addTextChangedListener(new myTextWatch());

    }

    protected class myTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @SuppressLint("NewApi")
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (et_password.getText().length() > 0
                    && et_telephone.getText().length() > 0) {
                tv_login.setAlpha(1.0f);
                tv_login.setEnabled(true);
            } else {
                tv_login.setAlpha(0.6f);
                tv_login.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

    }

    protected class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_login:
                    GetLogin();
                    // GoToMain();
                    break;
                case R.id.tv_atjoin:
                    GoToSign();
                    break;
                case R.id.tv_fpass:
                    GoToForget();
                    break;
                case R.id.tv_cphone:
                    GotoChangeCode();
                    break;
                default:
                    break;
            }
        }
    }


    public void GotoChangeCode() {
        Intent intent = new Intent(getApplicationContext(), com.kloudsync.techexcel.start.ChangeCountryCode.class);
        String code = tv_cphone.getText().toString();
        code = code.replaceAll("\\+", "");
        AppConfig.COUNTRY_CODE = Integer.parseInt(code);
        startActivityForResult(intent, com.kloudsync.techexcel.start.RegisterActivity.CHANGE_COUNTRY_CODE);
        overridePendingTransition(R.anim.tran_in4, R.anim.tran_out4);

    }

    private void GetLogin() {
        tv_login.setEnabled(false);
        telephone = et_telephone.getText().toString();
        password = et_password.getText().toString();
        editor.putString("telephone", telephone);
        editor.putString("password", com.kloudsync.techexcel.start.LoginGet.getBase64Password(password));
        editor.putInt("countrycode", AppConfig.COUNTRY_CODE);
        editor.commit();
        telephone = tv_cphone.getText().toString() + telephone;
        com.kloudsync.techexcel.start.LoginGet.LoginRequest(LoginActivity.this, telephone, password, 1,
                sharedPreferences, editor);
        fl_login.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(!instance.isFinishing()) {
                    tv_login.setEnabled(true);
                    fl_login.setVisibility(View.GONE);
                }
            }
        }, 5000);

    }

    public void GoToForget() {
        Intent intent = new Intent(LoginActivity.this,
                com.kloudsync.techexcel.start.ForgetPasswordActivity.class);
        startActivity(intent);
    }

    public void GoToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void GoToSign() {
        Intent intent = new Intent(LoginActivity.this, com.kloudsync.techexcel.start.RegisterActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case com.kloudsync.techexcel.start.RegisterActivity.CHANGE_COUNTRY_CODE:
                tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
                break;
            default:
                break;
        }
    }

    public void onResume() {
        super.onResume();
//	    MobclickAgent.onPageStart("LoginActivity");
//	    MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("LoginActivity");
//	    MobclickAgent.onPause(this);
    }

}
