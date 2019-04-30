package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DialogAddFavorite;
import com.kloudsync.techexcel.help.DialogDeleteDocument;
import com.kloudsync.techexcel.help.PopAlbums;
import com.kloudsync.techexcel.help.PopDocument;
import com.kloudsync.techexcel.help.PopEditDocument;
import com.kloudsync.techexcel.help.PopShareKloudSync;
import com.kloudsync.techexcel.help.Popupdate;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Favorite;
import com.kloudsync.techexcel.info.Space;
import com.kloudsync.techexcel.service.ConnectService;
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
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.adapter.TeamSpaceDocumentAdapter;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;
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
import java.util.List;

public class SpaceDocumentsActivity extends Activity implements View.OnClickListener {

    private RecyclerView mTeamRecyclerView;
    private int itemID;
    private TextView teamspacename;
    private TextView tv_fs;
    private ImageView img_back;
    private ImageView adddocument;
    private RelativeLayout teamRl;
    private TeamSpaceDocumentAdapter teamSpaceDocumentAdapter;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(SpaceDocumentsActivity.this,
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.DELETESUCCESS:
                    getTeamItem();
                    EventBus.getDefault().post(new TeamSpaceBean());
                    break;
                case AppConfig.AddTempLesson:
                    result = (String) msg.obj;
                    ViewdoHaha(result);
                    break;
                case AppConfig.LOAD_FINISH:
                    GoToVIew();
                    break;
                default:
                    break;
            }
        }
    };

    private void GoToVIew() {
        Intent intent = new Intent(SpaceDocumentsActivity.this, WatchCourseActivity3.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", bean.getId() + "," + AppConfig.UserID);
        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("lessionId",  "");
        intent.putExtra("isInstantMeeting", 1);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);


        startActivity(intent);
    }

    private void GoToVIew(TeamSpaceBeanFile lesson) {
        Intent intent = new Intent(SpaceDocumentsActivity.this, WatchCourseActivity3.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", lesson.getAttachmentID() + "," + AppConfig.UserID);
        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);

//        intent.putExtra("teacherid", bean.getTeacherId());
//        intent.putExtra("identity", bean.getRoleinlesson());
//        intent.putExtra("isStartCourse", true);
//        intent.putExtra("isPrepare", true);
//        intent.putExtra("isInstantMeeting", 0);
//        intent.putExtra("yinxiangmode", 0);

        intent.putExtra("identity", 2);
        intent.putExtra("lessionId",  "");
        intent.putExtra("isInstantMeeting", 1);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);
        startActivity(intent);
    }

    private void ViewdoHaha(final String meetingID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnjson = com.ub.techexcel.service.ConnectService
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.spacedocumentteam);
        initView();
        itemID = getIntent().getIntExtra("ItemID", 0);
        getTeamItem();
    }

    private void initView() {
        mTeamRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        teamspacename = (TextView) findViewById(R.id.teamspacename);
        tv_fs = (TextView) findViewById(R.id.tv_fs);
        img_back = (ImageView) findViewById(R.id.img_notice);
        adddocument = (ImageView) findViewById(R.id.adddocument);
        teamRl = (RelativeLayout) findViewById(R.id.teamrl);
        teamRl.setOnClickListener(this);
        adddocument.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }


    public void getTeamItem() {

        TeamSpaceInterfaceTools.getinstance().getTeamItem(AppConfig.URL_PUBLIC + "TeamSpace/Item?itemID=" + itemID, TeamSpaceInterfaceTools.GETTEAMITEM, new TeamSpaceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                TeamSpaceBean teamSpaceBean = (TeamSpaceBean) object;
                teamspacename.setText(teamSpaceBean.getName());
                if(teamSpaceBean.getName().length() > 0) {
                    tv_fs.setText(teamSpaceBean.getName().substring(0, 1));
                }else{
                    tv_fs.setText("");
                }
                getSpaceList();
            }
        });
    }

    private void getSpaceList() {
        TeamSpaceInterfaceTools.getinstance().getSpaceDocumentList(AppConfig.URL_PUBLIC + "SpaceAttachment/List?spaceID=" + itemID + "&type=1&pageIndex=0&pageSize=20&searchText=",
                TeamSpaceInterfaceTools.GETSPACEDOCUMENTLIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBeanFile> list = (List<TeamSpaceBeanFile>) object;
                        teamSpaceDocumentAdapter = new TeamSpaceDocumentAdapter(SpaceDocumentsActivity.this, list);
                        mTeamRecyclerView.setAdapter(teamSpaceDocumentAdapter);

                        teamSpaceDocumentAdapter.setOnItemLectureListener(new TeamSpaceDocumentAdapter.OnItemLectureListener() {
                            @Override
                            public void onItem(final TeamSpaceBeanFile lesson, View view) {
                                PopDocument pd = new PopDocument();
                                pd.getPopwindow(SpaceDocumentsActivity.this, lesson);
                                pd.setPoPMoreListener(new PopDocument.PopDocumentListener() {
                                    boolean flags;

                                    @Override
                                    public void PopView() {
//                                        getTempLesson(lesson);
                                        GoToVIew(lesson);
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
                                            getWindow().getDecorView().setAlpha(1.0f);
                                    }
                                });
                                pd.StartPop(view);
                                getWindow().getDecorView().setAlpha(0.5f);

                            }

                            @Override
                            public void share(int s, TeamSpaceBeanFile teamSpaceBeanFile) {
                                ShareKloudSync(teamSpaceBeanFile, s);

                            }

                            @Override
                            public void dismiss() {
                                getWindow().getDecorView().setAlpha(1.0f);
                            }

                            @Override
                            public void open() {
                                getWindow().getDecorView().setAlpha(0.5f);
                            }

                            @Override
                            public void deleteRefresh() {
                                getSpaceList();
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
                    for (int j = 0; j < sl.size(); j++) {
                        Space sp = sl.get(j);
                        if(sp.getItemID() == itemID){
                            sl.remove(j);
                            break;
                        }
                    }
                }

                SpaceDeletePopup spaceDeletePopup = new SpaceDeletePopup();
                spaceDeletePopup.getPopwindow(SpaceDocumentsActivity.this);
                spaceDeletePopup.setSP(cuslist, itemID);
                spaceDeletePopup.ChangeMove(lesson);
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

                    }

                    @Override
                    public void refresh() {
                        getTeamItem();
                    }
                });
                spaceDeletePopup.StartPop(mTeamRecyclerView);

            }
        });
        loginget.GetTeamSpace(this);
    }

    private void ShareKloudSync(final TeamSpaceBeanFile lesson, final int id) {
        final PopShareKloudSync psk = new PopShareKloudSync();
        psk.getPopwindow(SpaceDocumentsActivity.this, lesson, id);
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
                getWindow().getDecorView().setAlpha(1.0f);
            }
        });
        psk.StartPop(img_back);
        getWindow().getDecorView().setAlpha(0.5f);
    }

    private void EditLesson(TeamSpaceBeanFile lesson) {
        PopEditDocument ped = new PopEditDocument();
        ped.setPopEditDocumentListener(new PopEditDocument.PopEditDocumentListener() {
            @Override
            public void PopEdit() {
                getSpaceList();
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
        ped.getPopwindow(this, lesson);
        ped.StartPop(teamRl);
    }

    TeamSpaceBeanFile lesson2;

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
        ddd.EditCancel(SpaceDocumentsActivity.this);

    }

    private void DeleteLesson(final TeamSpaceBeanFile lesson) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = ConnectService.getIncidentDataattachment(
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
                    if (!NetWorkHelp.checkNetWorkStatus(getApplicationContext())) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
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
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "Lesson/AddTempLesson?attachmentID=" + fa.getAttachmentID()
                                    + "&Title=" + LoginGet.getBase64Password(fa.getTitle()), jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "  " + responsedata.toString());
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddTempLesson;
                        msg.obj = responsedata.getString("RetData");
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


    public static final int REQUEST_CODE_CAPTURE_ALBUM = 0;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTURE_ALBUM && resultCode == Activity.RESULT_OK
                && data != null) {
            String path = FileUtils.getPath(this, data.getData());
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
                        JSONObject responsedata = ConnectService
                                .submitDataByJson(finalUrl, jsonobject);
                        Log.e("UploadFileWithHash", responsedata.toString() + "   " + finalUrl);
                        String retcode = responsedata.getString("RetCode");
                        Message msg = new Message();
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {  //刷新
//                            msg.what = AppConfig.DELETESUCCESS;
                            getSpaceList();
                            EventBus.getDefault().post(new TeamSpaceBean());
                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                            uploadFile2(attachmentBean);

                        } else if (retcode.equals(AppConfig.Upload_Exist + "")) { //不要重复上传
                            msg.what = AppConfig.FAILED;
                            final String ErrorMessage = responsedata
                                    .getString("ErrorMessage");
                            msg.obj = ErrorMessage;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SpaceDocumentsActivity.this, ErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
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
                            puo.getPopwindow(SpaceDocumentsActivity.this, "");
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
                            puo.StartPop(mTeamRecyclerView);
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
                            Toast.makeText(getApplicationContext(),
                                    msg,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adddocument:
                AddDocument();
                break;
            case R.id.teamrl:
                Intent intent2 = new Intent(this, SpacePropertyActivity.class);
                intent2.putExtra("ItemID", itemID);
                startActivity(intent2);
                break;
            case R.id.img_notice:
                finish();
                break;
        }

    }

    private void AddDocument() {
        PopAlbums pa = new PopAlbums();
        pa.getPopwindow(getApplicationContext());
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
                    daf.EditCancel(SpaceDocumentsActivity.this);
                }
            }

            @Override
            public void PopDismissPhoto(boolean isAdd) {
                if (isAdd) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_CAPTURE_ALBUM);
                }
            }

            @Override
            public void PopBack() {
                getWindow().getDecorView().setAlpha(1.0f);
            }
        });
        pa.StartPop(adddocument);
        getWindow().getDecorView().setAlpha(0.5f);
    }

    private void AddFavorite(final Favorite fa) {
        final JSONObject jsonObject = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "SpaceAttachment/UploadFromFavorite?spaceID=" + itemID
                                    + "&itemIDs=" + fa.getItemID(), jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        getSpaceList();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(TeamSpaceBean teamSpaceBean) {
        flagr = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventFinish(Customer customer) {
        finish();
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
