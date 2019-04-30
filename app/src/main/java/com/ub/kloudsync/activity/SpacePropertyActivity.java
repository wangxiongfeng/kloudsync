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
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.SpacePropertyAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.InviteNewActivity;
import com.kloudsync.techexcel.docment.RenameActivity;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Space;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.NetWorkHelp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

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
    private ImageView switchteam;

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
        switchteam = (ImageView) findViewById(R.id.switchteam);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_invite.setOnClickListener(new myOnClick());
        switchteam.setOnClickListener(new myOnClick());

    }

    protected class myOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_invite:
                    GoToInvite();
                    break;
                case R.id.switchteam:
                    ShowMorePop();
                    break;
                default:
                    break;
            }
        }
    }

    private void ShowMorePop() {
        TeamMorePopup teamMorePopup = new TeamMorePopup();
        teamMorePopup.setIsTeam(false);
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
                DeleteSpace();
            }

            @Override
            public void rename() {
                Intent intent = new Intent(SpacePropertyActivity.this, RenameActivity.class);
                intent.putExtra("itemID", itemID);
                intent.putExtra("isteam", false);
                startActivity(intent);
            }

            @Override
            public void quit() {

            }

            @Override
            public void edit() {

            }
        });

        teamMorePopup.StartPop(switchteam);
    }

    private void DeleteSpace() {
        LoginGet lg = new LoginGet();
        lg.setBeforeDeleteSpaceListener(new LoginGet.BeforeDeleteSpaceListener() {
            @Override
            public void getBDS(int retdata) {
                if (0 == retdata) {
                    MergeSpace(retdata);
                } else {
                    GetDeletePop();
                }
            }
        });
        lg.GetBeforeDeleteSpace(this, itemID + "");
    }

    private ArrayList<Customer> cuslist = new ArrayList<Customer>();
    private void GetDeletePop() {
        LoginGet loginget = new LoginGet();
        loginget.setTeamSpaceGetListener(new LoginGet.TeamSpaceGetListener() {
            @Override
            public void getTS(ArrayList<Customer> list) {
                cuslist = new ArrayList<Customer>();
                cuslist.addAll(list);
                for (int i = 0; i < cuslist.size(); i++) {
                    Customer customer = cuslist.get(i);
                    ArrayList<Space> sl = customer.getSpaceList();
                    for (int j = 0; j < sl.size(); j++) {
                        Space sp = sl.get(j);
                        if(sp.getItemID() == itemID){
                            sl.remove(j);
                            break;
                        }
                    }
                }

                SpaceDeletePopup spaceDeletePopup = new SpaceDeletePopup();
                spaceDeletePopup.getPopwindow(SpacePropertyActivity.this);
                spaceDeletePopup.setSP(cuslist, itemID);
                spaceDeletePopup.setFavoritePoPListener(new SpaceDeletePopup.FavoritePoPListener() {
                    @Override
                    public void dismiss() {
                        getWindow().getDecorView().setAlpha(1.0f);
                    }

                    @Override
                    public void open() {
                        getWindow().getDecorView().setAlpha(0.5f);
                    }

                    @Override
                    public void delete(int spaceid) {
                        MergeSpace(spaceid);
                    }

                    @Override
                    public void refresh() {

                    }
                });
                spaceDeletePopup.StartPop(mTeamRecyclerView);

            }
        });
        loginget.GetTeamSpace(this);
    }

    private void MergeSpace(int retdata) {
        int mergeSpaceID = retdata;
        if (0 == mergeSpaceID) {
            mergeSpaceID = 9999;
        }
        final int finalMergeSpaceID = mergeSpaceID;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "TeamSpace/DeleteSpace?spaceID=" +
                                    itemID + "&mergeSpaceID=" +
                                    finalMergeSpaceID
                    );
                    Log.e("DeleteSpace", responsedata.toString());
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
