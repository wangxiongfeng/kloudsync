package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import com.kloudsync.techexcel.help.DialogAddFavorite;
import com.kloudsync.techexcel.help.DialogDeleteDocument;
import com.kloudsync.techexcel.help.DialogRename;
import com.kloudsync.techexcel.help.PopAlbums;
import com.kloudsync.techexcel.help.PopDeleteDocument;
import com.kloudsync.techexcel.help.PopDocument;
import com.kloudsync.techexcel.help.PopEditDocument;
import com.kloudsync.techexcel.help.PopShareKloudSync;
import com.kloudsync.techexcel.help.Popupdate;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Favorite;
import com.kloudsync.techexcel.info.Space;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.Md5Tool;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ub.kloudsync.activity.CreateNewSpaceActivity;
import com.ub.kloudsync.activity.CreateNewTeamActivity;
import com.ub.kloudsync.activity.SpaceDeletePopup;
import com.ub.kloudsync.activity.SpaceDocumentsActivity;
import com.ub.kloudsync.activity.SwitchTeamActivity;
import com.ub.kloudsync.activity.TeamMorePopup;
import com.ub.kloudsync.activity.TeamPropertyActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceBeanFile;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.activity.WatchCourseActivity2;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.adapter.SpaceAdapter;
import com.ub.techexcel.adapter.TeamSpaceDocumentAdapter;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentsFragment extends MyFragment implements View.OnClickListener, SpaceAdapter.OnItemLectureListener {

    private RecyclerView mCurrentTeamRecyclerView;
    private RelativeLayout teamRl;
    private RelativeLayout createNewSpace;
    private ImageView switchTeam;
    private ImageView addService;
    private ImageView moreOpation;
    private TextView teamSpacename;
    private RecyclerView spaceRecycleView;
    private List<TeamSpaceBean> spacesList = new ArrayList<>();
    private SpaceAdapter spaceAdapter;
    private TeamSpaceBean teamSpaceBean = new TeamSpaceBean();
    private TeamSpaceDocumentAdapter teamSpaceDocumentAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.documentfragment, container, false);
        EventBus.getDefault().register(this);
        initView(view);
        isPrepared = true;
        lazyLoad();
        return view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(TeamSpaceBean teamSpaceBean) {
        Log.e("duang", "biubiu");
        getTeamhaha();
        getSpaceList();
        getAllDocumentList();
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
                getAllDocumentList();
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

                        EventBus.getDefault().post(spacesList);
                    }
                });
    }


    private void getAllDocumentList() {
        TeamSpaceInterfaceTools.getinstance().getAllDocumentList(AppConfig.URL_PUBLIC + "SpaceAttachment/TeamDocumentList?companyID=" + AppConfig.SchoolID + "&teamID="
                        + teamSpaceBean.getItemID() + "&type=0&pageIndex=0&pageSize=100",
                TeamSpaceInterfaceTools.GETALLDOCUMENTLIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBeanFile> list = (List<TeamSpaceBeanFile>) object;
                        teamSpaceDocumentAdapter = new TeamSpaceDocumentAdapter(getActivity(), list);
                        mCurrentTeamRecyclerView.setAdapter(teamSpaceDocumentAdapter);
                        teamSpaceDocumentAdapter.setOnItemLectureListener(new TeamSpaceDocumentAdapter.OnItemLectureListener() {
                            @Override
                            public void onItem(final TeamSpaceBeanFile lesson, View view) {
                                PopDocument pd = new PopDocument();
                                pd.getPopwindow(getActivity(), lesson);
                                pd.setPoPMoreListener(new PopDocument.PopDocumentListener() {
                                    boolean flags;

                                    @Override
                                    public void PopView() {
//                                        getTempLesson(lesson);
//                                        GoToVIew(lesson);
                                    }

                                    @Override
                                    public void PopDelete() {
                                        DialogDelete(lesson);
                                    }

                                    @Override
                                    public void PopEdit() {
                                        flags = true;
                                        EditLesson(lesson);
                                    }

                                    @Override
                                    public void PopShare() {
                                        flags = true;
                                        ShareKloudSync(lesson, -1);
                                    }

                                    @Override
                                    public void PopMove() {
                                        MoveDocument(lesson);
                                    }

                                    @Override
                                    public void PopBack() {
                                        if (!flags)
                                            getActivity().getWindow().getDecorView().setAlpha(1.0f);
                                    }

                                });
                                pd.StartPop(view);
                                getActivity().getWindow().getDecorView().setAlpha(0.5f);

                            }

                            @Override
                            public void onRealItem(TeamSpaceBeanFile lesson, View view) {
                                GoToVIew(lesson);
                            }

                            @Override
                            public void share(int s, TeamSpaceBeanFile teamSpaceBeanFile) {
                                ShareKloudSync(teamSpaceBeanFile, s);
                            }

                            @Override
                            public void dismiss() {
                                getActivity().getWindow().getDecorView().setAlpha(1.0f);
                            }

                            @Override
                            public void open() {
                                getActivity().getWindow().getDecorView().setAlpha(0.5f);
                            }

                            @Override
                            public void deleteRefresh() {
                                getAllDocumentList();
                            }
                        });
                    }
                });
    }

    private ArrayList<Customer> cuslist = new ArrayList<Customer>();

    private void MoveDocument(final TeamSpaceBeanFile lesson) {

        LoginGet loginget = new LoginGet();
        loginget.setTeamSpaceGetListener(new LoginGet.TeamSpaceGetListener() {
            @Override
            public void getTS(ArrayList<Customer> list) {
                cuslist = new ArrayList<Customer>();
                cuslist.addAll(list);
                for (int i = 0; i < cuslist.size(); i++) {
                    Customer customer = cuslist.get(i);
                    ArrayList<Space> sl = customer.getSpaceList();
                    if (0 == sl.size()) {
                        cuslist.remove(i--);
                    }
                }

                SpaceDeletePopup spaceDeletePopup = new SpaceDeletePopup();
                spaceDeletePopup.getPopwindow(getActivity());
                spaceDeletePopup.setSP(cuslist);
                spaceDeletePopup.ChangeMove(lesson);
                spaceDeletePopup.SendTeam(teamSpaceBean.getItemID(), teamSpaceBean.getName());
                spaceDeletePopup.setFavoritePoPListener(new SpaceDeletePopup.FavoritePoPListener() {
                    @Override
                    public void dismiss() {
                        getActivity().getWindow().getDecorView().setAlpha(1.0f);
                    }

                    @Override
                    public void open() {
                        getActivity().getWindow().getDecorView().setAlpha(0.5f);
                    }

                    @Override
                    public void delete(int spaceid) {

                    }

                    @Override
                    public void refresh() {
                        getSpaceList();
                        getAllDocumentList();
                    }
                });
                spaceDeletePopup.StartPop(spaceRecycleView);

            }
        });
        loginget.GetTeamSpace(getActivity());

    }


    private void ShareKloudSync(final TeamSpaceBeanFile lesson, final int id) {
        final PopShareKloudSync psk = new PopShareKloudSync();
        psk.getPopwindow(getActivity(), lesson, id);
        psk.setPoPDismissListener(new PopShareKloudSync.PopShareKloudSyncDismissListener() {
            @Override
            public void CopyLink() {

            }

            @Override
            public void Wechat() {

            }

            @Override
            public void Moment() {

            }

            @Override
            public void Scan() {

            }

            @Override
            public void PopBack() {
                getActivity().getWindow().getDecorView().setAlpha(1.0f);
            }
        });
        psk.StartPop(teamRl);
        getActivity().getWindow().getDecorView().setAlpha(0.5f);
    }

    private void EditLesson(TeamSpaceBeanFile lesson) {
        PopEditDocument ped = new PopEditDocument();
        ped.setPopEditDocumentListener(new PopEditDocument.PopEditDocumentListener() {
            @Override
            public void PopEdit() {
                getAllDocumentList();
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
        ped.getPopwindow(getActivity(), lesson);
        ped.StartPop(teamRl);
    }

    private void DialogDelete(final TeamSpaceBeanFile lesson) {
        DialogDeleteDocument ddd = new DialogDeleteDocument();
        ddd.setPoPDismissListener(new DialogDeleteDocument.DialogDismissListener() {
            @Override
            public void PopDelete(boolean isdelete) {
                if (isdelete) {
                    DeleteLesson(lesson);
                }
            }
        });
        ddd.EditCancel(getActivity());

    }

    private void DeleteLesson(final TeamSpaceBeanFile lesson) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = com.kloudsync.techexcel.service.ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "SpaceAttachment/RemoveDocument?itemID=" +
                                    lesson.getItemID());
                    Log.e("RemoveDocument", responsedata.toString());
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

    private void initView(View view) {

        mCurrentTeamRecyclerView = (RecyclerView) view.findViewById(R.id.recycleview);
        spaceRecycleView = (RecyclerView) view.findViewById(R.id.spacerecycleview);

        mCurrentTeamRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        spaceRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        spaceAdapter = new SpaceAdapter(getActivity(), spacesList, false,false);
        spaceRecycleView.setAdapter(spaceAdapter);
        spaceAdapter.setOnItemLectureListener(this);

        teamRl = (RelativeLayout) view.findViewById(R.id.teamrl);
        createNewSpace = (RelativeLayout) view.findViewById(R.id.createnewspace);
        switchTeam = (ImageView) view.findViewById(R.id.switchteam);
        addService = (ImageView) view.findViewById(R.id.addService);
        teamSpacename = (TextView) view.findViewById(R.id.teamspacename);
        moreOpation = (ImageView) view.findViewById(R.id.moreOpation);
        teamRl.setOnClickListener(this);
        switchTeam.setOnClickListener(this);
        addService.setOnClickListener(this);
        createNewSpace.setOnClickListener(this);
        moreOpation.setOnClickListener(this);
        teamSpacename.setOnClickListener(this);


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
        switch (v.getId()) {
            case R.id.teamrl:
//                GoToTeamp();
                GoToSwitch();
                break;
            case R.id.switchteam:
                GoToSwitch();
                break;
            case R.id.teamspacename:
                GoToSwitch();
                break;
            case R.id.addService:
                AddBiuBiu();
                break;
            case R.id.createnewspace:
                Intent intent3 = new Intent(getActivity(), CreateNewSpaceActivity.class);
                if (teamSpaceBean.getItemID() != 0) {
                    intent3.putExtra("ItemID", teamSpaceBean.getItemID());
                    startActivity(intent3);
                } else {
                    Toast.makeText(getActivity(), "请先选择Team", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.moreOpation:
                MoreForTeam();
                break;
        }
    }

    private void GoToSwitch() {
        Intent intent2;
        intent2 = new Intent(getActivity(), SwitchTeamActivity.class);
        startActivity(intent2);
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


    private void MoreForTeam() {
        TeamMorePopup teamMorePopup = new TeamMorePopup();
        teamMorePopup.setIsTeam(true);
        teamMorePopup.setTSid(teamSpaceBean.getItemID());
        teamMorePopup.setTName(teamSpaceBean.getName());
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
                PopDeleteDocument pdd = new PopDeleteDocument();
                pdd.getPopwindow(getActivity());
                pdd.setPoPDismissListener(new PopDeleteDocument.PopDeleteDismissListener() {
                    @Override
                    public void PopDelete() {


                        LoginGet lg = new LoginGet();
                        lg.setBeforeDeleteTeamListener(new LoginGet.BeforeDeleteTeamListener() {
                            @Override
                            public void getBDT(int retdata) {
                                if (retdata > 0) {
                                    Toast.makeText(getActivity(), "Please delete space first", Toast.LENGTH_LONG).show();
                                } else {
                                    DeleteTeam();
                                }
                            }
                        });
                        lg.GetBeforeDeleteTeam(getActivity(), teamSpaceBean.getItemID() + "");
                    }

                    @Override
                    public void Open() {
                        getActivity().getWindow().getDecorView().setAlpha(0.5f);
                    }

                    @Override
                    public void Close() {
                        getActivity().getWindow().getDecorView().setAlpha(1.0f);
                    }
                });
                pdd.StartPop(moreOpation);
            }

            @Override
            public void rename() {
//                GoToRename();

                DialogRename dr = new DialogRename();
                dr.EditCancel(getActivity(), teamSpaceBean.getItemID(), true);
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
        intent.putExtra("itemID", teamSpaceBean.getItemID());
        intent.putExtra("isteam", true);
        startActivity(intent);
    }

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

    private int itemID;

    private void AddBiuBiu() {
        if (0 == spacesList.size()) {
            Toast.makeText(getActivity(), "This team is no space haha", Toast.LENGTH_SHORT).show();
            return;
        }
        /*DialogSDadd dadd = new DialogSDadd();
        dadd.setPoPDismissListener(new DialogSDadd.DialogDismissListener() {
            @Override
            public void PopSelect(TeamSpaceBean tsb) {
                itemID = tsb.getItemID();
                AddDocument();
            }
        });
        dadd.EditCancel(getActivity(), spacesList);*/


        LoginGet loginget = new LoginGet();
        loginget.setTeamSpaceGetListener(new LoginGet.TeamSpaceGetListener() {
            @Override
            public void getTS(ArrayList<Customer> list) {
                cuslist = new ArrayList<Customer>();
                cuslist.addAll(list);
                for (int i = 0; i < cuslist.size(); i++) {
                    Customer customer = cuslist.get(i);
                    ArrayList<Space> sl = customer.getSpaceList();
                    if (0 == sl.size()) {
                        cuslist.remove(i--);
                    }
                }

                SpaceDeletePopup spaceDeletePopup = new SpaceDeletePopup();
                spaceDeletePopup.getPopwindow(getActivity());
                spaceDeletePopup.setSP(cuslist);
                spaceDeletePopup.SendTeam(teamSpaceBean.getItemID(), teamSpaceBean.getName());
                spaceDeletePopup.setFavoritePoPListener(new SpaceDeletePopup.FavoritePoPListener() {
                    boolean flags;

                    @Override
                    public void dismiss() {
                        if (!flags)
                            getActivity().getWindow().getDecorView().setAlpha(1.0f);
                    }

                    @Override
                    public void open() {
                        getActivity().getWindow().getDecorView().setAlpha(0.5f);
                    }

                    @Override
                    public void delete(int spaceid) {
                        itemID = spaceid;
                        flags = true;
                        AddDocument();
                    }

                    @Override
                    public void refresh() {
                    }
                });
                spaceDeletePopup.StartPop(spaceRecycleView);

            }
        });
        loginget.GetTeamSpace(getActivity());
    }

    private void AddDocument() {
        PopAlbums pa = new PopAlbums();
        pa.getPopwindow(getActivity());
        pa.BulinBulin();
        pa.setPoPDismissListener(new PopAlbums.PopAlbumsDismissListener() {
            @Override
            public void PopDismiss(boolean isAdd) {
                if (isAdd) {
                    DialogAddFavorite daf = new DialogAddFavorite();
                    daf.setPoPDismissListener(new DialogAddFavorite.DialogDismissListener() {
                        @Override
                        public void DialogDismiss(Favorite fav) {
                            AddFavorite(fav);
                        }
                    });
                    daf.EditCancel(getActivity());
                }
            }

            @Override
            public void PopDismissPhoto(boolean isAdd) {
                if (isAdd) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SpaceDocumentsActivity.REQUEST_CODE_CAPTURE_ALBUM);
                }
            }

            @Override
            public void PopBack() {
                getActivity().getWindow().getDecorView().setAlpha(1.0f);
            }
        });
        pa.StartPop(addService);
        getActivity().getWindow().getDecorView().setAlpha(0.5f);
    }

    private void AddFavorite(final Favorite fa) {
        final JSONObject jsonObject = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "SpaceAttachment/UploadFromFavorite?spaceID=" + itemID
                                    + "&itemIDs=" + fa.getItemID(), jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        EventBus.getDefault().post(new TeamSpaceBean());
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

    private void getTempLesson(final TeamSpaceBeanFile fa) {
        final JSONObject jsonObject = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "Lesson/AddTempLesson?attachmentID=" + fa.getAttachmentID()
                                    + "&Title=" + LoginGet.getBase64Password(fa.getTitle()), jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "  " + responsedata.toString());
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddTempLesson;
                        fa.setLessonId(responsedata.getString("RetData"));
                        msg.obj = fa;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                case AppConfig.AddTempLesson:
//                    result = (String) msg.obj;
//                    ViewdoHaha(result);
                    GoToVIew((TeamSpaceBeanFile) msg.obj);
                    break;
                case AppConfig.LOAD_FINISH:
                    GoToVIew();
                    break;
                case AppConfig.DELETESUCCESS:
                    EventBus.getDefault().post(new TeamSpaceBean());
                    break;
                default:
                    break;
            }
        }
    };

    private void GoToVIew() {
        Intent intent = new Intent(getActivity(), WatchCourseActivity2.class);
        intent.putExtra("userid", bean.getUserId());
        intent.putExtra("meetingId", bean.getId() + "");
        intent.putExtra("teacherid", bean.getTeacherId());
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", bean.getRoleinlesson());
        intent.putExtra("isStartCourse", true);
        intent.putExtra("isPrepare", true);
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("yinxiangmode", 0);
        startActivity(intent);
    }

    private void GoToVIew(TeamSpaceBeanFile lesson) {
        Intent intent = new Intent(getActivity(), WatchCourseActivity3.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", lesson.getAttachmentID() + "," + AppConfig.UserID);
        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("lessionId", "");
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);
        startActivity(intent);
    }

    private void ViewdoHaha(final String meetingID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnjson = ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "Lesson/Item?lessonID=" + meetingID);
                formatServiceData(returnjson);
            }
        }).start();
    }

    private ServiceBean bean = new ServiceBean();

    private void formatServiceData(JSONObject returnJson) {
        Log.e("returnJson", returnJson.toString());
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONObject service = returnJson.getJSONObject("RetData");
                    bean = new ServiceBean();

                    bean.setId(service.getInt("LessonID"));
                    String des = service.getString("Description");
                    bean.setDescription(des);
                    int statusID = service.getInt("StatusID");
                    bean.setStatusID(statusID);
                    bean.setRoleinlesson(service.getInt("RoleInLesson"));
                    JSONArray memberlist = service.getJSONArray("MemberInfoList");
                    for (int i = 0; i < memberlist.length(); i++) {
                        JSONObject jsonObject = memberlist.getJSONObject(i);
                        int role = jsonObject.getInt("Role");
                        if (role == 2) { //teacher
                            bean.setTeacherName(jsonObject.getString("MemberName"));
                            bean.setTeacherId(jsonObject.getString("MemberID"));
                        } else if (role == 1) {
                            bean.setUserName(jsonObject.getString("MemberName"));
                            bean.setUserId(jsonObject.getString("MemberID"));
                        }
                    }
                    handler.obtainMessage(AppConfig.LOAD_FINISH).sendToTarget();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItem(TeamSpaceBean teamSpaceBean) {
        Intent intent = new Intent(getActivity(), SpaceDocumentsActivity.class);
        intent.putExtra("ItemID", teamSpaceBean.getItemID());
        startActivity(intent);
    }

    @Override
    public void select(TeamSpaceBean teamSpaceBean) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SpaceDocumentsActivity.REQUEST_CODE_CAPTURE_ALBUM && resultCode == Activity.RESULT_OK
                && data != null) {
            String path = FileUtils.getPath(getActivity(), data.getData());
            String pathname = path.substring(path.lastIndexOf("/") + 1);
            Log.e("path", path + "    " + pathname + "   " + data.getData());
            LineItem attachmentBean = new LineItem();
            attachmentBean.setUrl(path);
            attachmentBean.setFileName(pathname);
            UploadFileWithHash(attachmentBean);
        }
    }


    private void UploadFileWithHash(final LineItem attachmentBean) {
        Log.e("UploadFileWithHash", "UploadFileWithHash" + "");
        final JSONObject jsonobject = null;
        String url = null;
        File file = new File(attachmentBean.getUrl());
        String title = attachmentBean.getFileName();
        if (file.exists()) {
            try {
                url = AppConfig.URL_PUBLIC + "SpaceAttachment/UploadFileWithHash?spaceID=" + itemID + "&folderID=-1&Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(title), "UTF-8") +
                        "&Hash=" +
                        Md5Tool.getMd5ByFile(file);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String finalUrl = url;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject responsedata = com.kloudsync.techexcel.service.ConnectService
                                .submitDataByJson(finalUrl, jsonobject);
                        Log.e("UploadFileWithHash", responsedata.toString() + "   " + finalUrl);
                        String retcode = responsedata.getString("RetCode");
                        Message msg = new Message();
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {  //刷新
//                            msg.what = AppConfig.DELETESUCCESS;
                            EventBus.getDefault().post(new TeamSpaceBean());
                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                            uploadFile2(attachmentBean);
                        } else if (retcode.equals(AppConfig.Upload_Exist + "")) { //不要重复上传
                            msg.what = AppConfig.FAILED;
                            final String ErrorMessage = responsedata
                                    .getString("ErrorMessage");
                            msg.obj = ErrorMessage;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), ErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Toast.makeText(getActivity(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }

    private String fileNamebase;
    private HttpHandler httpHandler;
    private Popupdate puo;

    public void uploadFile2(final LineItem attachmentBean) {
        String fileName = attachmentBean.getFileName();
        attachmentBean.setFileName(fileName.replace(" ", "_"));
        RequestParams params = new RequestParams();
        params.setHeader("UserToken", AppConfig.UserToken);
        params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
        File file = new File(attachmentBean.getUrl());
        if (file.exists()) {
            String name = attachmentBean.getFileName();
            Log.e("filename----",
                    name + "      文件大小 " + file.length());
            params.addBodyParameter(name, file);
            String url = null;
            try {
                String baseurl = LoginGet.getBase64Password(name);
                fileNamebase = URLEncoder.encode(baseurl, "UTF-8");
                url = AppConfig.URL_PUBLIC + "SpaceAttachment/AddNewSpaceDocumentMultipart?Description=description&Hash=" + Md5Tool.getMd5ByFile(file) + "&spaceID=" + itemID + "&folderID=-1&Title=" + fileNamebase + "&Guid=" + Md5Tool.getUUID() + "&Total=1&Index=1";
                Log.e("URRRRRRRRRL", url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("url", url);
            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("UTF-8");
            httpHandler = http.send(HttpRequest.HttpMethod.POST, url, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            Log.e("iiiiiiiiii", "onStart");
                            puo = new Popupdate();
                            puo.getPopwindow(getActivity(), "");
                            puo.setPopCancelListener(new Popupdate.PopCancelListener() {
                                @Override
                                public void Cancel() {
                                    if (httpHandler != null) {
                                        httpHandler.cancel();
                                        httpHandler = null;
                                    }
                                }
                            });
                            puo.setPoPDismissListener(new Popupdate.PopDismissListener() {
                                @Override
                                public void PopDismiss() {
                                    EventBus.getDefault().post(new TeamSpaceBean());
                                }
                            });
                            puo.StartPop(mCurrentTeamRecyclerView);
                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {
                            Log.e("iiiiiiiiii", current + "");
                            if (puo != null) {
                                puo.setProgress(total, current);
                            }
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {   // converting
                            Log.e("iiiiiiiiii", "onSuccess  " + responseInfo.result);
                            getSpaceList();
                            if (puo != null) {
                                puo.DissmissPop();
                            }
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Log.e("iiiiiiiiii", "onFailure    " + msg);
                            if (puo != null) {
                                puo.DissmissPop();
                            }
                            Toast.makeText(getActivity(),
                                    msg,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }

}
