package com.kloudsync.techexcel.personal;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateOrganizationActivity extends AppCompatActivity {

    private ImageView img_back;
    private EditText et_name;
    private TextView tv_submit;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.CreateOrganization:
                    String result = (String) msg.obj;
                    COjson(result);
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), result,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };

    private void COjson(String result) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(result);
            int RetData = obj.getInt("RetData");

            sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                    MODE_PRIVATE);
            editor = sharedPreferences.edit();
            AppConfig.SchoolID = RetData;
            editor.putInt("SchoolID",RetData);
            editor.putInt("teamid",0);
            editor.putString("SchoolName",et_name.getText().toString());
            editor.putString("teamname","");
            editor.commit();
            EventBus.getDefault().post(new TeamSpaceBean());
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);
        initView();

    }

    private void initView() {
        img_back= (ImageView) findViewById(R.id.img_back);
        et_name= (EditText) findViewById(R.id.et_name);
        tv_submit= (TextView) findViewById(R.id.tv_submit);

        img_back.setOnClickListener(new MyOnclick());
        tv_submit.setOnClickListener(new MyOnclick());
    }

    protected class MyOnclick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_back:
                    finish();
                    break;
                case R.id.tv_submit:
                    SubmitNewOrganizaion();
                    break;
                default:
                    break;
            }
        }
    }

    private void SubmitNewOrganizaion() {
        final JSONObject jsonObject = format();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "School/CreateSchool", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.CreateOrganization;
                        msg.obj = responsedata.toString();
                    }else{
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SchoolName", et_name.getText().toString());
            jsonObject.put("Category1", 2);
            jsonObject.put("Category2", 0);
            jsonObject.put("OwnerID", AppConfig.UserID);
            jsonObject.put("AdminID", AppConfig.UserID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }
}
