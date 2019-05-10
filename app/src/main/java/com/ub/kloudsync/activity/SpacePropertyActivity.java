package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.SpacePropertyAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.InviteNewActivity;
import com.kloudsync.techexcel.info.Customer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SpacePropertyActivity extends Activity {

    private RecyclerView mTeamRecyclerView;
    private List<TeamUser> mTeamUserData = new ArrayList<>();
    private int itemID;
    private TextView teamspacename;
    private TextView tv_invite;
    private TextView tv_fs;
    private ImageView img_back;

    private SpacePropertyAdapter madapter;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.DELETESUCCESS:
                    EventBus.getDefault().post(new TeamSpaceBean());
                    EventBus.getDefault().post(new Customer());
                    finish();
                    break;
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(),
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();

                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_property);
        EventBus.getDefault().register(this);
        initView();
        itemID = getIntent().getIntExtra("ItemID", 0);
        getTeamItem();

    }

    private void initView() {

        mTeamRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        madapter = new SpacePropertyAdapter(this,mTeamUserData);
        madapter.setFavoritePoPListener(new SpacePropertyAdapter.FavoritePoPListener() {
            @Override
            public void dismiss() {
                getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getWindow().getDecorView().setAlpha(0.5f);
            }

            @Override
            public void sendMeaage(TeamUser user) {

            }

            @Override
            public void setAdmin(TeamUser user) {

            }

            @Override
            public void removeTeam(TeamUser user) {

            }
        });
        mTeamRecyclerView.setAdapter(madapter);
        teamspacename = (TextView) findViewById(R.id.teamspacename);
        tv_invite = (TextView) findViewById(R.id.tv_invite);
        tv_fs = (TextView) findViewById(R.id.tv_fs);
        img_back = (ImageView) findViewById(R.id.img_notice);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_invite.setOnClickListener(new myOnClick());

    }

    protected class myOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_invite:
                    GoToInvite();
                    break;
                default:
                    break;
            }
        }
    }
    

    private void GoToInvite() {
        Intent intent = new Intent(this, InviteNewActivity.class);
        intent.putExtra("itemID", itemID);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(TeamSpaceBean teamSpaceBean) {
        flagr = true;
    }

    public void getTeamItem() {
        TeamSpaceInterfaceTools.getinstance().getTeamItem(AppConfig.URL_PUBLIC + "TeamSpace/Item?itemID=" + itemID,
                TeamSpaceInterfaceTools.GETTEAMITEM, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        TeamSpaceBean teamSpaceBean = (TeamSpaceBean) object;
                        teamspacename.setText(teamSpaceBean.getName());
                        if(teamSpaceBean.getName().length() > 0) {
                            tv_fs.setText(teamSpaceBean.getName().substring(0, 1));
                        }else{
                            tv_fs.setText("");
                        }
                        mTeamUserData = teamSpaceBean.getMemberList();
                        madapter.UpdateRV(mTeamUserData);
                    }
                });

    }

    private boolean flagr;

    @Override
    protected void onResume() {
        super.onResume();
        if (flagr) {
            getTeamItem();
        }
        flagr = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
