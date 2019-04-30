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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.SpacePropertyAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.InviteNewActivity;
import com.kloudsync.techexcel.docment.RenameActivity;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.NetWorkHelp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeamPropertyActivity extends Activity implements View.OnClickListener {

    private RecyclerView mTeamRecyclerView;
    private List<TeamUser> mTeamUserData = new ArrayList<>();
    private int itemID;
    private TextView teamspacename;
    private TextView tv_invite;
    private ImageView img_back;
    private ImageView moreOpation;
    private RelativeLayout teamrl;

    private SpacePropertyAdapter madapter;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.DELETESUCCESS:
                    EventBus.getDefault().post(new TeamSpaceBean());
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
        setContentView(R.layout.documentteam);
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
        img_back = (ImageView) findViewById(R.id.img_notice);
        moreOpation = (ImageView) findViewById(R.id.moreOpation);
        teamrl = (RelativeLayout) findViewById(R.id.teamrl);
        moreOpation.setOnClickListener(this);
        teamrl.setOnClickListener(this);
        img_back.setOnClickListener(this);
        tv_invite.setOnClickListener(this);
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
                mTeamUserData = teamSpaceBean.getMemberList();
                madapter.UpdateRV(mTeamUserData);

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_notice:
                finish();
                break;
            case R.id.tv_invite:
                GoToInvite();
                break;
            case R.id.teamrl:
//                GoToRename();
                break;
            case R.id.moreOpation:
                MoreForTeam();
                break;
        }
    }

    private void MoreForTeam() {
        TeamMorePopup teamMorePopup=new TeamMorePopup();
        teamMorePopup.setIsTeam(true);
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
                /*SpaceDeletePopup spaceDeletePopup = new SpaceDeletePopup();
                spaceDeletePopup.getPopwindow(TeamPropertyActivity.this);
                spaceDeletePopup.setFavoritePoPListener(new SpaceDeletePopup.FavoritePoPListener() {
                    @Override
                    public void dismiss() {
                        getWindow().getDecorView().setAlpha(1.0f);
                    }

                    @Override
                    public void open() {
                        getWindow().getDecorView().setAlpha(0.5f);
                    }
                });
                spaceDeletePopup.StartPop(mTeamRecyclerView);*/

                LoginGet lg = new LoginGet();
                lg.setBeforeDeleteTeamListener(new LoginGet.BeforeDeleteTeamListener() {
                    @Override
                    public void getBDT(int retdata) {
                        if(retdata > 0){
                            Toast.makeText(getApplicationContext(), "Please delete space first", Toast.LENGTH_LONG).show();
                        } else {
                            DeleteTeam();
                        }
                    }
                });
                lg.GetBeforeDeleteTeam(TeamPropertyActivity.this, itemID + "");
            }

            @Override
            public void rename() {
                GoToRename();
            }

            @Override
            public void quit() {

            }

            @Override
            public void edit() {

            }
        });

        teamMorePopup.StartPop(moreOpation);
    }

    private void GoToRename() {
        Intent intent = new Intent(TeamPropertyActivity.this, RenameActivity.class);
        intent.putExtra("itemID",itemID);
        intent.putExtra("isteam", true);
        startActivity(intent);
    }

    private void GoToInvite() {
        Intent intent = new Intent(this, InviteNewActivity.class);
        intent.putExtra("itemID", itemID);
        intent.putExtra("flag_c2", true);
        startActivity(intent);
    }

    private void DeleteTeam() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "TeamSpace/DeleteTeam?teamID=" +
                                    itemID);
                    Log.e("DeleteTeam", responsedata.toString());
                    int retcode = (Integer) responsedata.get("RetCode");
                    msg = new Message();
                    if (0 == retcode) {
                        msg.what = AppConfig.DELETESUCCESS;
                        String result = responsedata.toString();
                        msg.obj = result;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("errorMessage");
                        msg.obj = ErrorMessage;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(getApplicationContext())) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private boolean flagr;
    @Override
    protected void onResume() {
        super.onResume();
        if(flagr){
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
