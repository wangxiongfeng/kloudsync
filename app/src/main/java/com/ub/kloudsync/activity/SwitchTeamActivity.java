package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.adapter.TeamAdapter;
import com.ub.techexcel.bean.SyncRoomBean;
import com.ub.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SwitchTeamActivity extends Activity implements View.OnClickListener {

    private RecyclerView mTeamRecyclerView;
    private TeamAdapter mTeamAdapter;
    private List<TeamSpaceBean> mCurrentTeamData = new ArrayList<>();
    private LinearLayout lin_add;
    private ImageView back;
    private Button createbtn;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean isSync;

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
        editor.putString("teamname", teamSpaceBeans.getName());
        editor.putInt("teamid", teamSpaceBeans.getItemID());
        editor.commit();
        EventBus.getDefault().post(new TeamSpaceBean());
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switchteamlayout);
        isSync = getIntent().getBooleanExtra("isSync", false);
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getAllTeamList();
    }

    private void initView() {
        mTeamRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lin_add = (LinearLayout) findViewById(R.id.lin_add);
        lin_add.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        createbtn = (Button) findViewById(R.id.createbtn);
        createbtn.setOnClickListener(this);
        mTeamAdapter = new TeamAdapter(this, mCurrentTeamData);
        mTeamRecyclerView.setAdapter(mTeamAdapter);
        mTeamAdapter.setOnItemLectureListener(new TeamAdapter.OnItemLectureListener() {
            @Override
            public void onItem(TeamSpaceBean teamSpaceBean) {
                for (int i = 0; i < mCurrentTeamData.size(); i++) {
                    TeamSpaceBean teamSpaceBean1 = mCurrentTeamData.get(i);
                    if (teamSpaceBean1.getItemID() == teamSpaceBean.getItemID()) {
                        teamSpaceBean1.setSelect(true);
                    } else {
                        teamSpaceBean1.setSelect(false);
                    }
                }
                teamSpaceBeans = teamSpaceBean;
                mTeamAdapter.notifyDataSetChanged();
                SwitchOK();
            }
        });
    }

    public void getAllTeamList() {
        if (isSync) {
            TeamSpaceInterfaceTools.getinstance().getTopicList(AppConfig.URL_PUBLIC + "Topic/List?type=1&companyID="
                            + AppConfig.SchoolID,
                    TeamSpaceInterfaceTools.TOPICLIST, new TeamSpaceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {
                            sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                                    MODE_PRIVATE);
                            int itemid = sharedPreferences.getInt("syncteamid", 0);
                            List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                            Log.e("ddddddd", list.size() + "");
                            mCurrentTeamData.clear();
                            mCurrentTeamData.addAll(list);
                            for (int i = 0; i < mCurrentTeamData.size(); i++) {
                                TeamSpaceBean teamSpaceBean1 = mCurrentTeamData.get(i);
                                if (teamSpaceBean1.getItemID() == itemid) {
                                    teamSpaceBean1.setSelect(true);
                                } else {
                                    teamSpaceBean1.setSelect(false);
                                }
                            }
                            mTeamAdapter.notifyDataSetChanged();
                        }
                    });
        } else {
            TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=1&parentID=0",
                    TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {

                            sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                                    MODE_PRIVATE);
                            int itemid = sharedPreferences.getInt("teamid", 0);
                            List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                            Log.e("ddddddd", list.size() + "");
                            mCurrentTeamData.clear();
                            mCurrentTeamData.addAll(list);
                            for (int i = 0; i < mCurrentTeamData.size(); i++) {
                                TeamSpaceBean teamSpaceBean1 = mCurrentTeamData.get(i);
                                if (teamSpaceBean1.getItemID() == itemid) {
                                    teamSpaceBean1.setSelect(true);
                                } else {
                                    teamSpaceBean1.setSelect(false);
                                }
                            }
                            mTeamAdapter.notifyDataSetChanged();
                        }
                    });
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_add:
                Intent intent = new Intent(this, CreateNewTeamActivity.class);
                intent.putExtra("isSync", isSync);
                startActivity(intent);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.createbtn:
                SwitchOK();
                break;
        }

    }

    private void SwitchOK() {

        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();/*

        for (int i = 0; i < mCurrentTeamData.size(); i++) {
            TeamSpaceBean teamSpaceBean1 = mCurrentTeamData.get(i);
            if (teamSpaceBean1.isSelect()) {
                editor.putInt("teamid", teamSpaceBean1.getItemID());
                editor.putString("teamname", teamSpaceBean1.getName());
                editor.commit();
                EventBus.getDefault().post(teamSpaceBean1);
                break;
            }
        }
        finish();*/
        if (isSync) {

            editor = sharedPreferences.edit();
            editor.putString("syncteamname", teamSpaceBeans.getName());
            editor.putInt("syncteamid", teamSpaceBeans.getItemID());
            editor.commit();

            SyncRoomBean syncRoomBean = new SyncRoomBean();
            syncRoomBean.setItemID(teamSpaceBeans.getItemID());
            syncRoomBean.setName(teamSpaceBeans.getName());
            EventBus.getDefault().post(syncRoomBean);

            finish();
        } else {
            AUUserInfo();
        }
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

    TeamSpaceBean teamSpaceBeans = new TeamSpaceBean();

    private JSONObject format2() {
        JSONObject jsonObject = new JSONObject();
        try {

            for (int i = 0; i < mCurrentTeamData.size(); i++) {
                TeamSpaceBean teamSpaceBean1 = mCurrentTeamData.get(i);
                if (teamSpaceBean1.isSelect()) {
                    jsonObject.put("TeamID", teamSpaceBean1.getItemID());
                    jsonObject.put("TeamName", TextUtils.isEmpty(teamSpaceBean1.getName()) ? "" : teamSpaceBean1.getName());
                    teamSpaceBeans = teamSpaceBean1;
                    break;
                }
            }
            jsonObject.put("SchoolID", sharedPreferences.getInt("SchoolID", -1));
            jsonObject.put("SchoolName", sharedPreferences.getString("SchoolName", null));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }


}
