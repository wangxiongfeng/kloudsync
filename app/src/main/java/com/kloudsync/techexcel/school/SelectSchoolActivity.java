package com.kloudsync.techexcel.school;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.SchoolAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DialogSelectSchool;
import com.kloudsync.techexcel.info.School;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.view.ClearEditText;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SelectSchoolActivity extends SwipeBackActivity {

    private RecyclerView rv_ss;
    private LinearLayout lin_main;
    private ImageView img_back;
    private ClearEditText et_search;
    private TextView tv_OK;

    private ArrayList<School> mlist = new ArrayList<>();
    private SchoolAdapter sAdapter;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private School school;
    private TeamSpaceBean teamSpaceBean = new TeamSpaceBean();


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.AddOrUpdateUserPreference:
                    String result = (String) msg.obj;
                    SaveSchoolInfo();
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

    private void SaveSchoolInfo() {
        editor = sharedPreferences.edit();
        AppConfig.SchoolID = school.getSchoolID();
        editor.putInt("SchoolID", school.getSchoolID());
        editor.putString("SchoolName", school.getSchoolName());
        editor.putString("teamname", teamSpaceBean.getName());
        editor.putInt("teamid", teamSpaceBean.getItemID());
        editor.commit();
        EventBus.getDefault().post(new TeamSpaceBean());
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_school);

        findView();
        initView();
        getAllSchool();
    }

    private void getAllSchool() {
        LoginGet loginGet = new LoginGet();
        loginGet.setMySchoolGetListener(new LoginGet.MySchoolGetListener() {
            @Override
            public void getSchool(ArrayList<School> list) {
                mlist = new ArrayList<>();
                mlist.addAll(list);
                sAdapter.UpdateRV(mlist, GetSaveInfo());
                SetMySchool();
            }
        });
        loginGet.GetSchoolInfo(getApplicationContext());
    }

    private void SetMySchool() {
        int id = GetSaveInfo();
        for (int i = 0; i < mlist.size(); i++) {
            if (mlist.get(i).getSchoolID() == id) {
                school = mlist.get(i);
                break;
            }
        }
    }

    private void initView() {
        sAdapter = new SchoolAdapter(mlist, GetSaveInfo());
        sAdapter.setOnItemClickListener(new SchoolAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                school = mlist.get(position);
                sAdapter.UpdateRV(mlist, school.getSchoolID());
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rv_ss.setLayoutManager(layoutManager);
        rv_ss.setAdapter(sAdapter);
        img_back.setOnClickListener(new MyOnClick());
        tv_OK.setOnClickListener(new MyOnClick());
    }

    private int GetSaveInfo() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        return sharedPreferences.getInt("SchoolID", -1);
    }

    private void findView() {
        img_back = (ImageView) findViewById(R.id.img_back);
        rv_ss = (RecyclerView) findViewById(R.id.rv_ss);
        et_search = (ClearEditText) findViewById(R.id.et_search);
        tv_OK = (TextView) findViewById(R.id.tv_OK);
        lin_main = (LinearLayout) findViewById(R.id.lin_main);
    }

    protected class MyOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_back:
                    finish();
                    break;
                case R.id.tv_OK:
                    ShowPop(v);
                    break;
            }
        }
    }

    private void ShowPop(View v) {

        if (school == null || mlist.size() == 0) {
            return;
        }
        DialogSelectSchool ds = new DialogSelectSchool();
        ds.setPoPDismissListener(new DialogSelectSchool.DialogDismissListener() {
            @Override
            public void PopSelect(boolean isSelect) {
                BackChange(1.0f);
                if (isSelect) {
                    if(school.getSchoolID() != GetSaveInfo()){
                        getMyTeamList();
                    }else{
                        finish();
                    }
                }
            }
        });
        ds.EditCancel(SelectSchoolActivity.this, school);
        BackChange(0.5f);

    }

    public void getMyTeamList() {

        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + school.getSchoolID() + "&type=1&parentID=0",
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        Log.e("ddddddd", list.size() + "");
                        teamSpaceBean = new TeamSpaceBean();
                        if (list.size() > 0) {
                            teamSpaceBean = list.get(0);
                        }
                        AUUserInfo();
                    }
                });

    }

    private void AUUserInfo() {

        final JSONObject jsonObject = format();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "User/AddOrUpdateUserPreference", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddOrUpdateUserPreference;
                        msg.obj = responsedata.toString();
                    } else {
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
            jsonObject.put("FieldID", 10001);
//            jsonObject.put("PreferenceValue", 0);
            jsonObject.put("PreferenceText", format2() + "");
//            jsonObject.put("PreferenceMemo", "");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    private JSONObject format2() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SchoolID", school.getSchoolID());
            jsonObject.put("TeamID", teamSpaceBean.getItemID());
            jsonObject.put("SchoolName", school.getSchoolName());
            jsonObject.put("TeamName", TextUtils.isEmpty(teamSpaceBean.getName()) ? "" : teamSpaceBean.getName());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void BackChange(float value) {
        lin_main.animate().alpha(value);
        lin_main.animate().setDuration(500);
    }

}
