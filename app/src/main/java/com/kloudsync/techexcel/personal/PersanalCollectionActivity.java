package com.kloudsync.techexcel.personal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.FavouriteAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.PopAlbums;
import com.kloudsync.techexcel.help.PopDeleteFavorite;
import com.kloudsync.techexcel.help.PopShareKloudSync;
import com.kloudsync.techexcel.help.RecyclerViewDivider;
import com.kloudsync.techexcel.info.Favorite;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.DensityUtil;
import com.kloudsync.techexcel.tool.Md5Tool;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ub.kloudsync.activity.TeamSpaceBeanFile;
import com.ub.service.activity.WatchCourseActivity2;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.tools.FileUtils;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by pingfan on 2017/7/5.
 */

public class PersanalCollectionActivity extends SwipeBackActivity {

    private TextView tv_back;
    private ImageView img_add;
    private RecyclerView rv_pc;
    private RelativeLayout rl_update;
    private LinearLayout lin_main;
    private ArrayList<Favorite> mlist = new ArrayList<Favorite>();
    private FavouriteAdapter fAdapter;

    public static PersanalCollectionActivity instance;

    private static final int REQUEST_CODE_CAPTURE_MEDIA = 2;
    private static final int REQUEST_CODE_CAPTURE_PHOTO = 3;

    private String outpath;

    Timer timer = new Timer();
    TimerTask timerTask;
    ArrayList<String> ua = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private static int SchoolID;

    private String mPath;
    private String mTitle;

    private ServiceBean bean = new ServiceBean();

    private String name;
    private String Title;
    private File mfile;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            rl_update.setVisibility(View.GONE);
            /*if (puo != null)
                puo.DissmissPop();*/

            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(),
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.DELETESUCCESS:
                    GetData();
                    break;
                case AppConfig.Upload_NoExist:
                    UploadFile(mPath, mTitle);
                    break;
                case AppConfig.AddTempLesson:
                    result = (String) msg.obj;
                    ViewdoHaha(result);
                    break;
                case AppConfig.LOAD_FINISH:
                    GoToVIew();
                    break;
                case AppConfig.AskConvert:
                    result = (String) msg.obj;
                    ConvertQuery(result);
                    break;
                case AppConfig.DoneStatus:
                    result = (String) msg.obj;
                    AskResult(result);
                    StopTimer();
                    break;
                case AppConfig.ConvertStatus:
                    double progress = (double) msg.obj;
                    Favorite favorite = mlist.get(mlist.size() - 1);
                    favorite.setFlag(2);
                    fAdapter.SetMyProgress(100, (long) progress, favorite);
                    break;
                case AppConfig.AskResult:
                    result = (String) msg.obj;
                    TellmyCompany(result);
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

    private void TellmyCompany(String token) {

        String url = null;
        try {
            url = AppConfig.URL_PUBLIC + "FavoriteAttachment/TransferOrConvertFile?Title="
                    + URLEncoder.encode(LoginGet.getBase64Password(Title), "UTF-8")
                    + "&Hash=" +
                    Md5Tool.getMd5ByFile(mfile)
                    + "&OssObjectName=temp/" + name;
            if(!TextUtil.isEmpty(token)){
                url += "&Token=" + token;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String finalUrl = url;
        Log.e("٩(ŏ﹏ŏ、)۶", url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService
                            .submitDataByJson(finalUrl, null);
                    Log.e("TransferOrConvertFile", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.DELETESUCCESS;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata
                                .getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                        HttpSend(AppConfig.DELETESUCCESS);
                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void AskResult(final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.STS_SERVER_URL
                        + "result?token=" + token);
                Log.e("result", jsonObject.toString());
                try {
                    String code = jsonObject.getString("code");
                    Message msg = new Message();
                    if (code.equals("10000")) {
//                        JSONObject result = jsonObject.getJSONObject("result");
//                        String imageurl = result.getString("imageurl");
//                        int count = result.getInt("count");
                        msg.obj = token;
                        msg.what = AppConfig.AskResult;
                    } else {
                        String errormessage = jsonObject.getString("msg");
                        msg.obj = errormessage;
                        msg.what = AppConfig.FAILED;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void ConvertQuery(final String token) {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.STS_SERVER_URL
                                + "query?token=" + token);
                        Log.e("query", jsonObject.toString());
                        try {
                            String code = jsonObject.getString("code");
                            Message msg = new Message();
                            if (code.equals("10000")) {
                                JSONObject result = jsonObject.getJSONObject("result");
                                double progress = result.getDouble("progress");
                                String status = result.getString("status");
                                if(status.equals("Done") || status.equals("Failed")){
                                    msg.what = AppConfig.DoneStatus;
                                    msg.obj = token;
                                    HttpSend(AppConfig.DELETESUCCESS);
                                } else {
                                    msg.what = AppConfig.ConvertStatus;
                                    msg.obj = progress * 100;
                                }
                            } else {
                                String errormessage = jsonObject.getString("msg");
                                msg.obj = errormessage;
                                msg.what = AppConfig.FAILED;
                            }
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };
        timer.schedule(timerTask, 0, 1000);

    }

    private void GoToVIew() {
        Intent intent = new Intent(PersanalCollectionActivity.this, WatchCourseActivity2.class);
        intent.putExtra("userid", bean.getUserId());
        intent.putExtra("meetingId", bean.getId() + "");
        intent.putExtra("teacherid", bean.getTeacherId());
        intent.putExtra("identity", bean.getRoleinlesson());
        intent.putExtra("isStartCourse", true);
        intent.putExtra("isPrepare", true);
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("yinxiangmode", 0);
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
        setContentView(R.layout.activity_personalcollection);
        instance = this;
        outpath = getIntent().getStringExtra("path");

        initOSS();

        GetSchoolID();
        initView();
        GetData();
        StartTimer();
    }

    private OSS oss;

    private void initOSS() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(AppConfig.OSS_ACCESS_KEY_ID,
                        AppConfig.OSS_ACCESS_KEY_SECRET, "");

//                OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(AppConfig.STS_SERVER_URL);

                // 该配置类如果不设置，会有默认配置，具体可看该类
                ClientConfiguration conf = new ClientConfiguration();
                conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
                conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
                conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
                conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次

                OSSLog.enableLog();

                oss = new OSSClient(getApplicationContext(), AppConfig.OSS_ENDPOINT, credentialProvider, conf);
            }
        }).start();

    }

    private void GetSchoolID() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        SchoolID = sharedPreferences.getInt("SchoolID", -1);
        if (-1 == SchoolID) {
            SchoolID = AppConfig.SchoolID;
        }
    }

    private void StartTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                convertingPercentage();
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void GetData() {
        LoginGet lg = new LoginGet();
        lg.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
            @Override
            public void getFavorite(ArrayList<Favorite> list) {
                mlist.clear();
                mlist.addAll(list);
                fAdapter.UpdateRV(mlist);
                if (!TextUtils.isEmpty(outpath)) {

                    String title = outpath.substring(outpath.lastIndexOf("/") + 1);
                    UpdateVideo(outpath, title);
                    outpath = "";
                }
            }
        });
        lg.MyFavoriteRequest(getApplicationContext(), 1);
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        img_add = (ImageView) findViewById(R.id.img_add);
        rl_update = (RelativeLayout) findViewById(R.id.rl_update);
        lin_main = (LinearLayout) findViewById(R.id.lin_main);
        rv_pc = (RecyclerView) findViewById(R.id.rv_pc);
        tv_back.setOnClickListener(new MyOnclick());
        img_add.setOnClickListener(new MyOnclick());

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_pc.setLayoutManager(manager);
        rv_pc.addItemDecoration(new RecyclerViewDivider(
                PersanalCollectionActivity.this, LinearLayout.HORIZONTAL,
                DensityUtil.dp2px(PersanalCollectionActivity.this,1),getResources().getColor(R.color.lightgrey)));
        fAdapter = new FavouriteAdapter(mlist);
        /*fAdapter.setOnItemLongClickListener(new FavouriteAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DeleteFav(view, position);
            }
        });*/
        fAdapter.setOnItemClickListener(new FavouriteAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                /*Intent intent = new Intent(view.getContext(), SaveFavoritesActivity.class);
                Favorite fav = mlist.get(position);
                SendFileMessage sfm = new SendFileMessage();
                sfm.setAttachmentID(fav.getAttachmentID() + "");
                sfm.setFileDownloadURL(fav.getFileDownloadURL() + "");
                sfm.setFileName(fav.getFileName() + "");
                intent.putExtra("sendFileMessage", (Parcelable) sfm);
                view.getContext().startActivity(intent);*/
            }
        });
        fAdapter.setDeleteItemClickListener(new FavouriteAdapter.DeleteItemClickListener() {
            @Override
            public void AddTempLesson(int position) {
                Favorite fa = mlist.get(position);
                GetTempLesson(fa);
            }

            @Override
            public void deleteClick(View view, int position) {
                DeleteFav(view, position);
            }

            @Override
            public void shareLesson(TeamSpaceBeanFile lesson, int id) {
                ShareKloudSync(lesson, id);
            }
        });
        rv_pc.setAdapter(fAdapter);
    }

    private void ShareKloudSync(final TeamSpaceBeanFile lesson, final int id) {
        final PopShareKloudSync psk = new PopShareKloudSync();
        psk.getPopwindow(getApplicationContext(), lesson, id);
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
                BackChange(1.0f);
            }
        });
        psk.StartPop(img_add);
        BackChange(0.5f);
    }

    private void GetTempLesson(final Favorite fa) {
        final JSONObject jsonObject = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "Lesson/AddTempLesson?attachmentID=" + fa.getAttachmentID()
                                    + "&Title=" + LoginGet.getBase64Password(fa.getTitle()), jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddTempLesson;
                        JSONObject RetData = responsedata.getJSONObject("RetData");
                        msg.obj = RetData.getString("LessonID");
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

    private void DeleteFav(View view, int position) {
        PopDeleteFavorite pdf = new PopDeleteFavorite();
        final Favorite fav = mlist.get(position);
        pdf.getPopwindow(getApplicationContext(), fav);
        pdf.setPoPDismissListener(new PopDeleteFavorite.PopUpdateOutDismissListener() {
            @Override
            public void PopDismiss(boolean isDelete) {
                if (isDelete) {
                    DeleteFavorite(fav);
                }
                BackChange(1.0f);
            }
        });
        pdf.StartPop(view);
        BackChange(0.5f);
    }

    private void BackChange(float value) {
        lin_main.animate().alpha(value);
        lin_main.animate().setDuration(500);
    }

    private void DeleteFavorite(final Favorite fav) {
        rl_update.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "FavoriteAttachment/RemoveFavorite?" +
                                    "itemIDs=" +
                                    fav.getItemID()
                                    /*+
                                    "&schoolID=" +
                                    SchoolID*/);
                    Log.e("Removeresponse", responsedata.toString());
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


    protected class MyOnclick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    FinishActivityanim();
                    break;
                case R.id.img_add:
                    AddAlbum();
                    break;
                default:
                    break;
            }
        }
    }

    private void AddAlbum() {
        PopAlbums pa = new PopAlbums();
        pa.getPopwindow(getApplicationContext());
        pa.setPoPDismissListener(new PopAlbums.PopAlbumsDismissListener() {
            @Override
            public void PopDismiss(boolean isAdd) {
                if (isAdd) {
                    GetVideo();
                }
            }

            @Override
            public void PopDismissPhoto(boolean isAdd) {
                if (isAdd) {
                    GetPhoto();
                }
            }

            @Override
            public void PopBack() {
                BackChange(1.0f);
            }
        });
        pa.StartPop(img_add);
        BackChange(0.5f);

    }

    private void GetVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_MEDIA);
    }

    private void GetPhoto() {
        /*Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);*/
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_PHOTO);
    }


    private void FinishActivityanim() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTURE_MEDIA && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
//            int videoId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            // 视频名称：MediaStore.Audio.Media.TITLE
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            // 视频路径：MediaStore.Audio.Media.DATA
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            // 视频时长：MediaStore.Audio.Media.DURATION
//            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            // 视频大小：MediaStore.Audio.Media.SIZE
//            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            cursor.close();
            title = path.substring(path.lastIndexOf("/") + 1);
            UpdateVideo(path, title);
        } else if (requestCode == REQUEST_CODE_CAPTURE_PHOTO && resultCode == Activity.RESULT_OK) {
            String path = FileUtils.getPath(this, data.getData());
            String title = path.substring(path.lastIndexOf("/") + 1);
            UpdateVideo(path, title);
        }
    }

    /*private void UpdateVideo(final String path, final String title) {
        Log.e("video", path + " : " + title);
        String name2 = AppConfig.UserID + Md5Tool.getUUID();
        name = Md5Tool.transformMD5(name2);
        Title = title;
        Log.e("video", path + "  ----  " + name);
        mfile = new File(path);


        final String finalName = "temp/" + name;

        final Favorite favorite = new Favorite();
        favorite.setFlag(1);
        favorite.setTitle(title);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PutObjectRequest put = new PutObjectRequest(AppConfig.BUCKET_NAME,
                        finalName, path);
                put.setCRC64(OSSRequest.CRC64Config.YES);

                mlist.add(favorite);
//                fAdapter.UpdateRV(mlist);


                // 异步上传时可以设置进度回调
                put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                    @Override
                    public void onProgress(PutObjectRequest request, final long currentSize, final long totalSize) {
                        Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                        int progress = (int) (100 * currentSize / totalSize);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fAdapter.SetMyProgress(totalSize, currentSize, favorite);
                            }
                        });
                    }
                });

                OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                    @Override
                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                        Log.d("PutObject", "UploadSuccess");

                        Log.d("ETag", result.getETag());
                        Log.d("RequestId", result.getRequestId());

                        String houzui = title.substring(title.lastIndexOf(".") + 1);
                        houzui = houzui.toLowerCase();
                        if (Md5Tool.checkType(houzui)) {
                            PleaseConvert(name, houzui);
                        } else {
                            TellmyCompany("");
                        }

                    }

                    @Override
                    public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                        // 请求异常
                        if (clientExcepion != null) {
                            // 本地异常如网络异常等
                            clientExcepion.printStackTrace();
                        }
                        if (serviceException != null) {
                            // 服务异常
                            Log.e("ErrorCode", serviceException.getErrorCode());
                            Log.e("RequestId", serviceException.getRequestId());
                            Log.e("HostId", serviceException.getHostId());
                            Log.e("RawMessage", serviceException.getRawMessage());
                        }
                    }
                });
            }

        }, 1000);

    }*/


    private void PleaseConvert(final String name, final String houzui) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.STS_SERVER_URL
                        + "convert?name=" + name + "&type=" + houzui);
                Log.e("convert", jsonObject.toString());
                try {
                    String code = jsonObject.getString("code");
                    Message msg = new Message();
                    if (code.equals("10000")) {
                        JSONObject result = jsonObject.getJSONObject("result");
                        String token = result.getString("token");
                        msg.obj = token;
                        msg.what = AppConfig.AskConvert;
                    } else {
                        String errormessage = jsonObject.getString("msg");
                        msg.obj = errormessage;
                        msg.what = AppConfig.FAILED;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//    private Popupdate puo;

    private void UpdateVideo(final String path, final String title) {
        Log.e("video", path + " : " + title);
        Message msg = new Message();

        final JSONObject jsonobject = null;
        String url = null;
        File file = new File(path);

        if (file.exists()) {
            try {
                url = AppConfig.URL_PUBLIC + "FavoriteAttachment/UploadFileWithHash?Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(title), "UTF-8") +
                        "&schoolID=" +
                        SchoolID +
                        "&Hash=" +
                        Md5Tool.getMd5ByFile(file);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String finalUrl = url;
            Log.e("٩(ŏ﹏ŏ、)۶", url);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject responsedata = ConnectService
                                .submitDataByJson(finalUrl, jsonobject);
                        Log.e("UploadFileWithHash", responsedata.toString() + "");
                        String retcode = responsedata.getString("RetCode");
                        Message msg = new Message();
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                            msg.what = AppConfig.DELETESUCCESS;
                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) {
                            msg.what = AppConfig.Upload_NoExist;
                            mPath = path;
                            mTitle = title;
                        } else if (retcode.equals(AppConfig.Upload_Exist + "")) {
                            msg.what = AppConfig.FAILED;
                            String ErrorMessage = responsedata
                                    .getString("ErrorMessage");
                            msg.obj = ErrorMessage;
                        }
                        handler.sendMessage(msg);
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

    private void UploadFile(String path, String title) {
        final Favorite favorite = new Favorite();
        favorite.setFlag(1);
        favorite.setTitle(title);
        /*puo = new Popupdate();
        puo.getPopwindow(PersanalCollectionActivity.this, title);
        puo.setPoPDismissListener(new Popupdate.PopDismissListener() {
            @Override
            public void PopDismiss() {
                rl_update.setVisibility(View.GONE);

            }
        });*/
        RequestParams params = new RequestParams();
        params.setHeader("UserToken", AppConfig.UserToken);

//        params.addBodyParameter("Content-Type", "video/mpeg4");// 设定传送的内容类型
        params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
        // params.setContentType("application/octet-stream");
        File file = new File(path);
        if (file.exists()) {
//            rl_update.setVisibility(View.VISIBLE);
            params.addBodyParameter(title, file);
            String url = null;
            try {
                url = AppConfig.URL_PUBLIC + "FavoriteAttachment/AddNewFavorite?Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(title), "UTF-8") +
                        /*"&schoolID=" +
                        SchoolID
                        +*/ "&Hash=" + Md5Tool.getMd5ByFile(file);
                Log.e("hahaha", url + ":" + title);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.e("url", url);
            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("UTF-8");
            final HttpHandler hh = http.send(HttpRequest.HttpMethod.POST, url, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            mlist.add(favorite);
                            fAdapter.UpdateRV(mlist);
//                            puo.StartPop(img_add);
                        }

                        @Override
                        public void onLoading(final long total, final long current,
                                              boolean isUploading) {
                            fAdapter.SetMyProgress(total, current, favorite);
//                            puo.setProgress(total, current);
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(responseInfo.result);
                                Log.e("hahaha", jsonObject.toString() + "");
                                int RetCode = jsonObject.getInt("RetCode");
                                if (0 == RetCode) {
                                    JSONObject js = jsonObject.getJSONObject("RetData");
                                    if (js.getInt("Status") == 10) { // 上传成功  开始转换
                                        int attachmentid = js.getInt("AttachmentID");
                                        favorite.setAttachmentID(attachmentid);
                                        favorite.setFlag(2);
                                        favorite.setProgressbar(0);
                                        ua.add(attachmentid + "");
                                    } else {
                                        HttpSend(AppConfig.DELETESUCCESS);
                                    }
                                } else {
                                    String ErrorMessage = jsonObject.getString("ErrorMessage");
                                    Toast.makeText(PersanalCollectionActivity.this, ErrorMessage, Toast.LENGTH_SHORT).show();
                                    HttpSend(AppConfig.DELETESUCCESS);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(HttpException error, String msgs) {
                            Log.e("error", msgs.toString());

                            Message msg = new Message();
                            msg.what = AppConfig.FAILED;
                            msg.obj = msgs.toString();
                            handler.sendMessage(msg);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void convertingPercentage() {
        if (ua.size() == 0) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String haha = "";
                for (int i = 0; i < ua.size(); i++) {
                    haha += ua.get(i);
                    if (i < (ua.size() - 1)) {
                        haha += ",";
                    }
                }
                JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                        + "Attachment/AttachmentConvertingPercentage?attachmentIDs=" + haha);
                Log.e("duang", jsonObject.toString());
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("RetData");
                    if (jsonArray == null) {
                        HttpSend(AppConfig.DELETESUCCESS);
                        return;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        int attachmentid = jsonObject1.getInt("AttachmentID");
                        int status = jsonObject1.getInt("Status");
                        int Percentage = jsonObject1.getInt("Percentage");
                        for (int j = 0; j < mlist.size(); j++) {
                            Favorite fa = mlist.get(j);
                            if (attachmentid == fa.getAttachmentID()) {
                                if (status == 0) {
                                    Log.e("hahaha", "cancel");
                                    mlist.get(j).setFlag(0);
                                    ua.remove(attachmentid + "");
                                } else {
                                    Log.e("hahaha", jsonObject.toString() + "");
                                    mlist.get(j).setProgressbar(Percentage);
                                }
                                break;
                            }
                        }
                        if (ua.size() == 0) {
                            HttpSend(AppConfig.DELETESUCCESS);
                            return;
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void HttpSend(int msgwhat) {
        handler.sendEmptyMessage(msgwhat);
    }

    private void StopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopTimer();
    }
}
