package com.ub.kloudsync.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.adapter.SpaceAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SwitchSpaceActivity extends Activity implements View.OnClickListener {

    private RecyclerView mTeamRecyclerView;
    private List<TeamSpaceBean> spacesList = new ArrayList<>();
    private SpaceAdapter spaceAdapter;
    private LinearLayout lin_add;
    private ImageView back;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int spaceId;
    private int teamId;
    private boolean isSyncRoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switchspacelayout);
        spaceId = getIntent().getIntExtra("ItemID", 0);
        isSyncRoom = getIntent().getBooleanExtra("isSyncRoom", false);
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getSpaceList();
    }

    private void initView() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        teamId = sharedPreferences.getInt("teamid", 0);
        mTeamRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lin_add = (LinearLayout) findViewById(R.id.lin_add);
        lin_add.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        spaceAdapter = new SpaceAdapter(SwitchSpaceActivity.this, spacesList, isSyncRoom, true);
        mTeamRecyclerView.setAdapter(spaceAdapter);
        spaceAdapter.setOnItemLectureListener(new SpaceAdapter.OnItemLectureListener() {
            @Override
            public void onItem(TeamSpaceBean teamSpaceBean) {

            }

            @Override
            public void select(TeamSpaceBean teamSpaceBean) {
                for (int i = 0; i < spacesList.size(); i++) {
                    TeamSpaceBean teamSpaceBean1 = spacesList.get(i);
                    if (teamSpaceBean1.getItemID() == teamSpaceBean.getItemID()) {
                        teamSpaceBean1.setSelect(true);
                    } else {
                        teamSpaceBean1.setSelect(false);
                    }
                }
                spaceAdapter.notifyDataSetChanged();
                Intent intent = getIntent();
                intent.putExtra("selectSpace", (Serializable) teamSpaceBean);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    private void getSpaceList() {
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + teamId,
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        spacesList.clear();
                        spacesList.addAll(list);
                        for (int i = 0; i < spacesList.size(); i++) {
                            TeamSpaceBean teamSpaceBean1 = spacesList.get(i);
                            if (teamSpaceBean1.getItemID() == spaceId) {
                                teamSpaceBean1.setSelect(true);
                            } else {
                                teamSpaceBean1.setSelect(false);
                            }
                        }
                        spaceAdapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_add:
                Intent intent = new Intent(this, CreateNewSpaceActivity.class);
                intent.putExtra("ItemID", teamId);
                startActivity(intent);
                break;
            case R.id.back:
                finish();
                break;
        }

    }


}


