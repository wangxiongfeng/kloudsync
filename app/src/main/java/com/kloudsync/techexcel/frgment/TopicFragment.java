package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.RenameActivity;
import com.kloudsync.techexcel.help.DialogDeleteDocument;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Space;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.ub.kloudsync.activity.CreateNewTeamActivity;
import com.ub.kloudsync.activity.SpaceDeletePopup;
import com.ub.kloudsync.activity.SpaceDocumentsActivity;
import com.ub.kloudsync.activity.SpaceSyncRoomActivity;
import com.ub.kloudsync.activity.SwitchTeamActivity;
import com.ub.kloudsync.activity.TeamMorePopup;
import com.ub.kloudsync.activity.TeamPropertyActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceBeanFile;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.activity.SyncRoomActivity;
import com.ub.techexcel.adapter.SpaceAdapter;
import com.ub.techexcel.adapter.SyncRoomAdapter;
import com.ub.techexcel.bean.SyncRoomBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TopicFragment extends MyFragment implements View.OnClickListener, SpaceAdapter.OnItemLectureListener {


    private RecyclerView syncroomRecyclerView;
    private RelativeLayout teamRl;
    private RelativeLayout createNewSpace;
    private ImageView switchTeam;
    private TextView teamSpacename;
    private RecyclerView spaceRecycleView;
    private List<TeamSpaceBean> spacesList = new ArrayList<>();
    private SpaceAdapter spaceAdapter;
    private TeamSpaceBean teamSpaceBean = new TeamSpaceBean();
    private SharedPreferences sharedPreferences;
    private SyncRoomAdapter syncRoomAdapter;
    private ImageView moreOpation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topicfragment, container, false);
        EventBus.getDefault().register(this);
        initView(view);
        isPrepared = true;
        lazyLoad();
        return view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(List<TeamSpaceBean> list) {

        Log.e("duang", "biubiu");
        getTeamhaha();
        spacesList.clear();
        spacesList.addAll(list);
        spaceAdapter.notifyDataSetChanged();
        getSyncRoomList();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible) {  //isPrepared 可见在onCreate之前执行
            if (!isLoadDataFinish) {
                isLoadDataFinish = true;
                getSpaceList();
                getSyncRoomList();
            }
        }
    }


    private void getSpaceList() {
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + teamSpaceBean.getItemID(),
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        spacesList.clear();
                        spacesList.addAll(list);
                        spaceAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void getSyncRoomList() {
        TeamSpaceInterfaceTools.getinstance().getSyncRoomList(AppConfig.URL_PUBLIC + "SyncRoom/List?companyID=" + AppConfig.SchoolID + "&teamID=" + teamSpaceBean.getItemID() + "&spaceID=0",
                TeamSpaceInterfaceTools.GETSYNCROOMLIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SyncRoomBean> list = (List<SyncRoomBean>) object;
                        syncRoomAdapter = new SyncRoomAdapter(getActivity(), list);
                        syncroomRecyclerView.setAdapter(syncRoomAdapter);
                        syncRoomAdapter.setOnItemLectureListener(new SyncRoomAdapter.OnItemLectureListener() {

                            @Override
                            public void view(SyncRoomBean syncRoomBean) {
                                enterSyncroom(syncRoomBean);
                            }

                            @Override
                            public void deleteSuccess() {
                                getSpaceList();
                                getSyncRoomList();
                            }

                            @Override
                            public void switchSuccess() {
                                getSpaceList();
                                getSyncRoomList();
                            }

                            @Override
                            public void dismiss() {
                                getActivity().getWindow().getDecorView().setAlpha(1.0f);
                            }

                            @Override
                            public void open() {
                                getActivity().getWindow().getDecorView().setAlpha(0.5f);
                            }
                        });
                    }
                });
    }



    private void enterSyncroom(SyncRoomBean syncRoomBean) {

        Intent intent = new Intent(getActivity(), SyncRoomActivity.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", syncRoomBean.getItemID() + "");
        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("lessionId", syncRoomBean.getItemID() + "");
        intent.putExtra("syncRoomname", syncRoomBean.getName() + "");
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);
        startActivity(intent);

    }


    private void initView(View view) {

        syncroomRecyclerView = (RecyclerView) view.findViewById(R.id.recycleview);
        spaceRecycleView = (RecyclerView) view.findViewById(R.id.spacerecycleview);

        syncroomRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        spaceRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        spaceAdapter = new SpaceAdapter(getActivity(), spacesList, true);
        spaceRecycleView.setAdapter(spaceAdapter);
        spaceAdapter.setOnItemLectureListener(this);

        teamRl = (RelativeLayout) view.findViewById(R.id.teamrl);
        createNewSpace = (RelativeLayout) view.findViewById(R.id.createnewspace);
        switchTeam = (ImageView) view.findViewById(R.id.switchteam);
        teamSpacename = (TextView) view.findViewById(R.id.teamspacename);
        moreOpation = (ImageView) view.findViewById(R.id.moreOpation);
        moreOpation.setOnClickListener(this);
        teamRl.setOnClickListener(this);
        switchTeam.setOnClickListener(this);
        createNewSpace.setOnClickListener(this);

        getTeamhaha();
    }


    private void getTeamhaha() {
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,
                Context.MODE_PRIVATE);
        teamSpaceBean.setName(sharedPreferences.getString("teamname", ""));
        teamSpaceBean.setItemID(sharedPreferences.getInt("teamid", 0));
        teamSpacename.setText(teamSpaceBean.getName());
    }

    @Override
    public void onClick(View v) {
        //fffff
        switch (v.getId()) {
            case R.id.teamrl:
//                Intent intent = new Intent(getActivity(), TeamPropertyActivity.class);
//                if (teamSpaceBean.getItemID() != 0) {
//                    intent.putExtra("ItemID", teamSpaceBean.getItemID());
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getActivity(), "请先选择Team", Toast.LENGTH_LONG).show();
//                }
                break;
            case R.id.switchteam:
                Intent intent2 = new Intent(getActivity(), SwitchTeamActivity.class);
                startActivity(intent2);
                break;
            case R.id.moreOpation:

                MoreForTeam();
                break;
            case R.id.createnewspace:
                Intent intent3 = new Intent(getActivity(), CreateNewTeamActivity.class);
                if (teamSpaceBean.getItemID() != 0) {
                    intent3.putExtra("ItemID", teamSpaceBean.getItemID());
                    startActivity(intent3);
                } else {
                    Toast.makeText(getActivity(), "请先选择Team", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void MoreForTeam() {
        TeamMorePopup teamMorePopup=new TeamMorePopup();
        teamMorePopup.setIsTeam(true);
        teamMorePopup.getPopwindow(getActivity());
        teamMorePopup.setFavoritePoPListener(new TeamMorePopup.FavoritePoPListener() {
            @Override
            public void dismiss() {
                getActivity().getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getActivity().getWindow().getDecorView().setAlpha(0.5f);
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
                            Toast.makeText(getActivity(), "Please delete space first", Toast.LENGTH_LONG).show();
                        } else {
                            DeleteTeam();
                        }
                    }
                });
                lg.GetBeforeDeleteTeam(getActivity(), teamSpaceBean.getItemID() + "");
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
                GoToTeamp();
            }
        });

        teamMorePopup.StartPop(moreOpation);
    }

    private void GoToRename() {
        Intent intent = new Intent(getActivity(), RenameActivity.class);
        intent.putExtra("itemID",teamSpaceBean.getItemID());
        intent.putExtra("isteam", true);
        startActivity(intent);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(getActivity(),
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.DELETESUCCESS:
                    EventBus.getDefault().post(new TeamSpaceBean());
                    break;
                default:
                    break;
            }
        }
    };


    private void DeleteTeam() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = com.kloudsync.techexcel.service.ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "TeamSpace/DeleteTeam?teamID=" +
                                    teamSpaceBean.getItemID());
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
                    if (!NetWorkHelp.checkNetWorkStatus(getActivity())) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void GoToTeamp() {
        Intent intent = new Intent(getActivity(), TeamPropertyActivity.class);
        if (teamSpaceBean.getItemID() != 0) {
            intent.putExtra("ItemID", teamSpaceBean.getItemID());
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "请先选择Team", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItem(TeamSpaceBean teamSpaceBean2) {
        Intent intent = new Intent(getActivity(), SpaceSyncRoomActivity.class);
        intent.putExtra("teamid", teamSpaceBean.getItemID());
        intent.putExtra("spaceid", teamSpaceBean2.getItemID());
        intent.putExtra("spaceName", teamSpaceBean2.getName());
        startActivity(intent);
    }


}
