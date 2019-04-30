package com.kloudsync.techexcel.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ub.service.activity.WatchCourseActivity2;
import com.ub.service.activity.WatchCourseActivity3;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.service.UploadService;

public class UpoadNull extends Activity {

    private SharedPreferences sharedPreferences;
    public static UpoadNull instance;
    Intent service;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadnull);
        instance = this;
        getUri();
    }

    private void getUri() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        Log.e("null", (uri == null) + ":");
        if (uri != null) {
            Log.e("uri", uri.getPath() + ":");
            AppConfig.OUTSIDE_PATH = uri.getPath();
        }
        service = new Intent(UpoadNull.this, UploadService.class);
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        final boolean isLogIn = sharedPreferences.getBoolean("isLogIn", false);
        final Intent i = getPackageManager()
                .getLaunchIntentForPackage(getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (!isLogIn) {
            startActivity(i);
            finish();
        } else if(MainActivity.instance == null || MainActivity.instance.isFinishing()){
            startActivity(i);
            finish();
        } else if(WatchCourseActivity2.watch2instance || WatchCourseActivity3.watch3instance){
            finish();
        } else {
            service.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startService(service);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(service != null){
            stopService(service);
        }
    }
}
