package com.ub.kloudsync.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DialogRename;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Space;
import com.kloudsync.techexcel.linshi.LinshiActivity;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.ub.service.activity.SyncRoomActivity;
import com.ub.techexcel.adapter.SyncRoomAdapter;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.SyncRoomBean;
import com.ub.techexcel.tools.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SpaceSyncRoomActivity extends Activity implements View.OnClickListener {

    private RecyclerView syncroomRecyclerView;
    private int teamId, spaceId;
    private String spaceName;
    private TextView teamspacename;
    private ImageView tv_fs;
    private ImageView img_back;
    private ImageView adddocument;
    private RelativeLayout teamRl;
    private SyncRoomAdapter syncRoomAdapter;
    private RelativeLayout createnewsyncroom;
    private ImageView switchteam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.spacesyncroomteam);
        teamId = getIntent().getIntExtra("teamid", 0);
        spaceId = getIntent().getIntExtra("spaceid", 0);
        spaceName = getIntent().getStringExtra("spaceName");
        initView();
        getSyncRoomList();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(SyncRoomBean syncRoomBean) {
        getSyncRoomList();
        isRefresh = true;
    }


    private void initView() {
        syncroomRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        syncroomRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        teamspacename = (TextView) findViewById(R.id.teamspacename);
        tv_fs = (ImageView) findViewById(R.id.tv_fs);
        createnewsyncroom = (RelativeLayout) findViewById(R.id.createnewsyncroom);
        createnewsyncroom.setOnClickListener(this);
        teamspacename.setText(spaceName);
        img_back = (ImageView) findViewById(R.id.img_notice);
        switchteam = (ImageView) findViewById(R.id.switchteam);
        switchteam.setOnClickListener(this);
        adddocument = (ImageView) findViewById(R.id.adddocument);
        teamRl = (RelativeLayout) findViewById(R.id.teamrl);
        teamRl.setOnClickListener(this);
        adddocument.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    private boolean isRefresh;

    private void getSyncRoomList() {
        TeamSpaceInterfaceTools.getinstance().getSyncRoomList(AppConfig.URL_PUBLIC + "SyncRoom/List?companyID=" + AppConfig.SchoolID + "&teamID=" + teamId + "&spaceID=" + spaceId,
                TeamSpaceInterfaceTools.GETSYNCROOMLIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SyncRoomBean> list = (List<SyncRoomBean>) object;
                        syncRoomAdapter = new SyncRoomAdapter(SpaceSyncRoomActivity.this, list);
                        syncroomRecyclerView.setAdapter(syncRoomAdapter);
                        syncRoomAdapter.setOnItemLectureListener(new SyncRoomAdapter.OnItemLectureListener() {
                            @Override
                            public void view(SyncRoomBean syncRoomBean) {
                                enterSyncroom(syncRoomBean);
                            }

                            @Override
                            public void deleteSuccess() {
                                getSyncRoomList();
                                isRefresh = true;
                            }


                            @Override
                            public void switchSuccess() {  //move
                                getSyncRoomList();
                                isRefresh = true;
                            }

                            @Override
                            public void item(SyncRoomBean syncRoomBean) {
                                Intent intent=new Intent(SpaceSyncRoomActivity.this, LinshiActivity.class);
                                intent.putExtra("syncRoomBean",(Serializable) syncRoomBean);
                                startActivity(intent);
                            }

                            @Override
                            public void dismiss() {
                                getWindow().getDecorView().setAlpha(1.0f);
                            }

                            @Override
                            public void open() {
                                getWindow().getDecorView().setAlpha(0.5f);
                            }
                        });
                    }
                });
    }

    private void enterSyncroom(SyncRoomBean syncRoomBean) {

        Intent intent = new Intent(this, SyncRoomActivity.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", syncRoomBean.getItemID() + "");
        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("lessionId", syncRoomBean.getItemID() + "");
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);
        startActivity(intent);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createnewsyncroom:
                Intent intent = new Intent(this, CreateNewSyncRoomActivity.class);
                intent.putExtra("spaceid", spaceId);
                intent.putExtra("teamid", teamId);
                startActivity(intent);
                break;
            case R.id.teamrl:
                Intent intent2 = new Intent(this, SwitchSpaceActivity.class);
                intent2.putExtra("ItemID", spaceId);
                intent2.putExtra("isSyncRoom",true);
                startActivityForResult(intent2, REQUEST_CODE_CHANGESPACE);
                break;
            case R.id.img_notice:
                finish();
                break;
            case R.id.switchteam:
                ShowMorePop();
                break;
        }
    }


    public static final int REQUEST_CODE_CHANGESPACE = 1;
    private TeamSpaceBean selectSpace;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHANGESPACE) {
            if (resultCode == RESULT_OK) {
                selectSpace = (TeamSpaceBean) data.getSerializableExtra("selectSpace");
                if (spaceId != selectSpace.getItemID()) {
                    spaceId = selectSpace.getItemID();
                    getSyncRoomList();
                }
            }

        }
    }


    private void ShowMorePop() {

        TeamMorePopup teamMorePopup = new TeamMorePopup();
        teamMorePopup.setIsTeam(false);
        teamMorePopup.setTSid(spaceId);
        teamMorePopup.getPopwindow(this);
        teamMorePopup.setFavoritePoPListener(new TeamMorePopup.FavoritePoPListener() {
            @Override
            public void dismiss() {
                getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getWindow().getDecorView().setAlpha(0.5f);
            }

            @Override
            public void delete() {
//                DeleteSpace();
            }

            @Override
            public void rename() {
//                GotoRename();
                DialogRename dr = new DialogRename();
                dr.EditCancel(SpaceSyncRoomActivity.this, spaceId, false);
            }

            @Override
            public void quit() {
                finish();
            }

            @Override
            public void edit() {
                Intent intent2 = new Intent(SpaceSyncRoomActivity.this, SpacePropertyActivity.class);
                intent2.putExtra("ItemID", spaceId);
                startActivity(intent2);
            }
        });

        teamMorePopup.StartPop(switchteam);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (isRefresh) {
            EventBus.getDefault().post(new TeamSpaceBean());
        }
    }
}
