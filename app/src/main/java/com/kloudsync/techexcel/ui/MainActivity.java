package com.kloudsync.techexcel.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.CustomizeMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.FriendMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.GroupMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.KnowledgeMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.SendFileMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.ShareMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.SystemMessageItemProvider;
import com.kloudsync.techexcel.docment.WeiXinApi;
import com.kloudsync.techexcel.frgment.ContactFragment;
import com.kloudsync.techexcel.frgment.DocumentsFragment;
import com.kloudsync.techexcel.frgment.PersonalCenterFragment;
import com.kloudsync.techexcel.frgment.ServiceFragment;
import com.kloudsync.techexcel.frgment.TopicFragment;
import com.kloudsync.techexcel.frgment.TwoToOneFragment;
import com.kloudsync.techexcel.help.ContactHelpInterface;
import com.kloudsync.techexcel.info.School;
import com.kloudsync.techexcel.personal.PersanalCollectionActivity;
import com.kloudsync.techexcel.service.UploadService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.DensityUtil;
import com.kloudsync.techexcel.view.CustomViewPager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ub.kloudsync.activity.SpaceDocumentsActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.tools.FileUtils;
import com.umeng.analytics.MobclickAgent;
import com.wuyr.rippleanimation.RippleAnimation;

import org.java_websocket.client.WebSocketClient;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectCallback;
import io.rong.imlib.RongIMClient.ErrorCode;


public class MainActivity extends FragmentActivity {

    private List<TextView> tvs = new ArrayList<TextView>();
    private TextView tv_redcontact;
    private CustomViewPager vp;
    private RelativeLayout rl_update;
    private FragmentPagerAdapter mAdapter;

    private List<Fragment> mTabs = new ArrayList<Fragment>();

    private int tvIDs[] = {R.id.tv_service, R.id.tv_dialogue, R.id.tv_contact,
            R.id.tv_community, R.id.tv_personal_center};
    private int drawIDs[] = {R.drawable.tab_d_02, R.drawable.tab_d_01,
            R.drawable.tab_d_03, R.drawable.tab_d_04,
            R.drawable.tab_d_05};
    private int draw_selectIDs[] = {R.drawable.tab_c_02, R.drawable.tab_c_01,
            R.drawable.tab_c_03, R.drawable.tab_c_04, R.drawable.tab_c_05};

    float density;

    public static RongIMClient mRongIMClient;

    public static MainActivity instance = null;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private WebSocketClient mWebSocketClient;

    public static boolean RESUME = false;

    private boolean flag_update;

    private boolean flag_dialog;

    private boolean flag_jinhua;

    Intent service;

    private static ContactHelpInterface chi;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.RONGCONNECT_ERROR:
                    sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                            MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("isLogIn", false);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), (String) msg.obj,
                            Toast.LENGTH_LONG).show();
                    RongIM.getInstance().disconnect();
                    final Intent intent = getPackageManager()
                            .getLaunchIntentForPackage(getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    break;
                case AppConfig.OnlineGunDan:
                    if (1 == vp.getCurrentItem()) {
                        GoTOTab(0);
                    }
                    RongIM.getInstance().disconnect();
                    AppConfig.Online = 1;
                    flag_dialog = false;
                    Toast.makeText(MainActivity.this,
                            getResources().getString(R.string.Dialog_GunDan), Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.UPLOADHEAD:
                    rl_update.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.uploadsuccess),
                            Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), PersanalCollectionActivity.class);
                    startActivity(i);
                    break;
                case AppConfig.UPLOADFAILD:
                    rl_update.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.uploadfailure),
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        PgyUpdateManager.register(this);
        initView();
        GetSchoolInfo();
//		initUpdate();
        StartWBServiceHAHA();
        GetMaZhang();


    }

    private void GetSchoolInfo() {
        LoginGet lg = new LoginGet();
        lg.setSchoolTeamGetListener(new LoginGet.SchoolTeamGetListener() {
            @Override
            public void getST(School school) {
                sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                        MODE_PRIVATE);
                editor = sharedPreferences.edit();
                if(school != null){
                    TeamSpaceBean teamSpaceBean = school.getTeamSpaceBean();
                    AppConfig.SchoolID = school.getSchoolID();
                    editor.putInt("SchoolID", school.getSchoolID());
                    editor.putString("SchoolName", school.getSchoolName());
                    editor.putString("teamname", teamSpaceBean.getName());
                    editor.putInt("teamid", teamSpaceBean.getItemID());
                    editor.commit();
                } else {
                    editor.putString("SchoolName", null);
                    editor.putString("teamname", "");
                    editor.putInt("teamid", 0);
                    editor.commit();
                }
                initDatas();
            }
        });
        lg.GetUserPreference(this,10001 + "");
    }

    private IWXAPI api;

    private void GetMaZhang() {
        WeiXinApi.getInstance().init(this);

        api = WXAPIFactory.createWXAPI(MainActivity.this,
                AppConfig.WX_APP_ID, true);
        api.registerApp(AppConfig.WX_APP_ID);
        api.handleIntent(getIntent(), WeiXinApi.getInstance());
        WeiXinApi.getInstance().setApi(api);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, WeiXinApi.getInstance());
    }


    private void UpOrNoOutInfo() {
        /*Intent intent = getIntent();
        Uri uri = intent.getData();
        Log.e("null", (uri == null) + ":");
        if (uri != null) {
            Log.e("uri", uri.getPath() + ":");
            AppConfig.OUTSIDE_PATH = uri.getPath();
        }*/

        /*sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        final boolean isLogIn = sharedPreferences.getBoolean("isLogIn", false);
        if (!isLogIn) {
            final Intent i = getPackageManager()
                    .getLaunchIntentForPackage(getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(i);
            finish();
            return;
        }*/
        if (!TextUtils.isEmpty(AppConfig.OUTSIDE_PATH)) {
            /*rl_update.setVisibility(View.VISIBLE);
            final PopUpdateOut puo = new PopUpdateOut();
            puo.getPopwindow(getApplicationContext());
            puo.setPoPDismissListener(new PopUpdateOut.PopUpdateOutDismissListener() {
                @Override
                public void PopDismiss(boolean isUpdate) {
                    if (isUpdate) {
//                        UpdateOutData();
                        Intent intent = new Intent(MainActivity.this, PersanalCollectionActivity.class);
                        intent.putExtra("path", AppConfig.OUTSIDE_PATH);
                        startActivity(intent);
                    } else {
                        rl_update.setVisibility(View.GONE);
                    }
                    AppConfig.OUTSIDE_PATH = "";
                }
            });
            puo.StartPop(rl_update);*/

            Intent service2 = new Intent(MainActivity.this, UploadService.class);
            service2.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startService(service2);

            /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
            builder.setTitle("")
                    .setMessage(getString(R.string.ask_update_out))
                    .setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            if (PersanalCollectionActivity.instance != null && !PersanalCollectionActivity.instance.isFinishing()) {
                                PersanalCollectionActivity.instance.finish();
                            }
                            Intent intent = new Intent(MainActivity.this, PersanalCollectionActivity.class);
                            intent.putExtra("path", AppConfig.OUTSIDE_PATH);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            dialog.dismiss();
                            AppConfig.OUTSIDE_PATH = "";
                        }
                    })
                    .setNegativeButton(getString(R.string.No),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    dialog.dismiss();
                                    AppConfig.OUTSIDE_PATH = "";
                                }
                            });
            final AlertDialog dialog = builder.create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    AppConfig.OUTSIDE_PATH = "";
                }
            });
            dialog.show();*/
        } else {
            return;
        }
    }

    private void StartWBServiceHAHA() {
        service = new Intent(getApplicationContext(), SocketService.class);
        startService(service);
    }


    private void initUpdate() {
        // TODO Auto-generated method stub
        PgyUpdateManager.register(MainActivity.this,
                new UpdateManagerListener() {

                    @Override
                    public void onUpdateAvailable(final String result) {
                        Log.e("onUpdateAvailable", result + ":");
                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getResources().getString(R.string.update))
                                .setMessage(getResources().getString(R.string.update_message))
                                .setPositiveButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }

                                })
                                .setNegativeButton(
                                        getResources().getString(R.string.update),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                startDownloadTask(
                                                        MainActivity.this,
                                                        appBean.getDownloadURL());
                                            }
                                        }).show();
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                    }
                });
    }

    private void initView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;

        rl_update = (RelativeLayout) findViewById(R.id.rl_update);
        vp = (CustomViewPager) findViewById(R.id.vp);
        tv_redcontact = (TextView) findViewById(R.id.tv_redcontact);
        GetTvShow();
        GetTabName();

        RongConnect();

    }

    private void GetTabName() {
        String[] tab = getResources().getStringArray(R.array.tabname);
        for (int i = 0; i < tvIDs.length; i++) {
            tvs.get(i).setText(tab[i]);
        }
    }

    private void GetTabName2() {
        String[] tab = getResources().getStringArray(R.array.tabname2);
        for (int i = 0; i < tvIDs.length; i++) {
            tvs.get(i).setText(tab[i]);
        }
    }

    private void GetTvShow() {
        for (int i = 0; i < tvIDs.length; i++) {
            TextView tv = (TextView) findViewById(tvIDs[i]);

            if (i == 0) {
                Drawable d = getResources().getDrawable(draw_selectIDs[i]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 25), DensityUtil.dp2px(getApplicationContext(), 25)); // 必须设置图片大小，否则不显示
                tv.setTextColor(getResources().getColor(R.color.green));
                tv.setCompoundDrawables(null, d, null, null);

            } else {
                Drawable d = getResources().getDrawable(drawIDs[i]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 25), DensityUtil.dp2px(getApplicationContext(), 25));  // 必须设置图片大小，否则不显示
                tv.setTextColor(getResources().getColor(R.color.darkgrey));
                tv.setCompoundDrawables(null, d, null, null);
            }

            tv.setOnClickListener(new myOnClick());
            tvs.add(tv);
        }
    }

    private void RongConnect() {
        RongIM.connect(AppConfig.RongUserToken, new ConnectCallback() {

            @Override
            public void onError(ErrorCode arg0) {
                Toast.makeText(getApplicationContext(), "connect onError",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String arg0) {
                /*Toast.makeText(getApplicationContext(), "connect onSuccess",
                        Toast.LENGTH_SHORT).show();*/
                AppConfig.RongUserID = arg0;

                RongIM.registerMessageTemplate(new CustomizeMessageItemProvider());
                RongIM.registerMessageTemplate(new KnowledgeMessageItemProvider());
                RongIM.registerMessageTemplate(new SystemMessageItemProvider());
                RongIM.registerMessageTemplate(new FriendMessageItemProvider());
                RongIM.registerMessageTemplate(new GroupMessageItemProvider());
                RongIM.registerMessageTemplate(new SendFileMessageItemProvider());
//				RongIM.registerMessageTemplate(new CourseMessageItemProvider());
                RongIM.registerMessageTemplate(new ShareMessageItemProvider());

//                initDatas();
                flag_dialog = true;
                if (RongIM.getInstance() != null
                        && RongIM.getInstance().getRongIMClient() != null) {
                    /**
                     * 设置连接状态变化的监听器.
                     */
                    RongIM.getInstance()
                            .getRongIMClient()
                            .setConnectionStatusListener(
                                    new MyConnectionStatusListener());
                }
                AppConfig.Online = 0;
            }


            @Override
            public void onTokenIncorrect() {
                Log.e("Token Incorrect", "Token Incorrect");
                Message msg = new Message();
                msg.what = AppConfig.RONGCONNECT_ERROR;
                msg.obj = "连接错误";
                handler.sendMessage(msg);
            }
        });
//		getToken();
    }

    private class MyConnectionStatusListener implements
            RongIMClient.ConnectionStatusListener {

        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {

                case CONNECTED:// 连接成功。

                    break;
                case DISCONNECTED:// 断开连接。

                    break;
                case CONNECTING:// 连接中。

                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。

                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    if (!instance.isFinishing()) {
                        Message msg = new Message();
                        msg.what = AppConfig.OnlineGunDan;
//                        msg.obj = "该账号已在其他设备上登录";
                        handler.sendMessage(msg);
                    }

                    break;
                default:
                    break;
            }
        }
    }

    private void initDatas() {
        DocumentsFragment documentsFragment = new DocumentsFragment();
        TwoToOneFragment twoToOneFragment = new TwoToOneFragment();
        ServiceFragment serviceFragment = new ServiceFragment();
//        UpgradeFragment upgradeFragment = new UpgradeFragment();
//        CommunityFragment communityFragment = new CommunityFragment();
        PersonalCenterFragment personalCenterFragment = new PersonalCenterFragment();
        TopicFragment topicFragment = new TopicFragment();

        mTabs = new ArrayList<Fragment>();
        mTabs.add(documentsFragment);
        mTabs.add(twoToOneFragment);
        mTabs.add(serviceFragment);
//        mTabs.add(upgradeFragment);
        mTabs.add(topicFragment);
        mTabs.add(personalCenterFragment);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };
        vp.setAdapter(mAdapter);
        vp.setOffscreenPageLimit(5);
        GoTOTab(0);

    }

    private void initDatas2() {
        DocumentsFragment documentsFragment = new DocumentsFragment();
        TwoToOneFragment twoToOneFragment = new TwoToOneFragment();
        ServiceFragment serviceFragment = new ServiceFragment();
        TopicFragment topicFragment = new TopicFragment();
        PersonalCenterFragment personalCenterFragment = new PersonalCenterFragment();

        mTabs = new ArrayList<Fragment>();
        mTabs.add(documentsFragment);
        mTabs.add(twoToOneFragment);
        mTabs.add(serviceFragment);
        mTabs.add(topicFragment);
        mTabs.add(personalCenterFragment);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };
        vp.setAdapter(mAdapter);
        vp.setOffscreenPageLimit(4);
//        GoTOTab(0);

    }

    protected class myOnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            /*if(AppConfig.SOCKET!=null){
                Log.e("lalala",AppConfig.SOCKET.isClosed() + ";" + AppConfig.SOCKET.isConnected());
			}else{
				Log.e("lalala","Socket null");
			}*/
            switch (v.getId()) {
                case R.id.tv_dialogue:
                    if (!flag_dialog) {
                        RongConnect();
                    }
                    GoTOTab(1);
                    break;
                case R.id.tv_service:
                    GoTOTab(0);
                    break;
                case R.id.tv_contact:
                    GoTOTab(2);
                    break;
                case R.id.tv_community:
                    GoTOTab(3);
                    break;
                case R.id.tv_personal_center:
                    GoTOTab(4);
                    break;
                default:
                    break;
            }

        }

    }

    public void GoTOTab(int s) {
        for (int i = 0; i < tvs.size(); i++) {
            if (i == s) {
                Drawable d = getResources().getDrawable(draw_selectIDs[s]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 25), DensityUtil.dp2px(getApplicationContext(), 25)); // 必须设置图片大小，否则不显示
                tvs.get(s).setTextColor(getResources().getColor(R.color.green));
                tvs.get(s).setCompoundDrawables(null, d, null, null);
            } else {
                Drawable d = getResources().getDrawable(drawIDs[i]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 25), DensityUtil.dp2px(getApplicationContext(), 25));  // 必须设置图片大小，否则不显示
                tvs.get(i).setTextColor(getResources().getColor(R.color.darkgrey));
                tvs.get(i).setCompoundDrawables(null, d, null, null);
            }
        }
        if ((3 == s) && flag_jinhua) {
            vp.setCurrentItem(5, false);
        } else {
            vp.setCurrentItem(s, false);
        }
    }

    private void UpdateOutData() {
        RequestParams params = new RequestParams();
        params.setHeader("UserToken", AppConfig.UserToken);

        params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
        // params.setContentType("application/octet-stream");
        File file = new File(AppConfig.OUTSIDE_PATH);
        if (file.exists()) {
            int lastSlash = 0;
            lastSlash = AppConfig.OUTSIDE_PATH.lastIndexOf("/");
            String name = AppConfig.OUTSIDE_PATH.substring(lastSlash + 1, AppConfig.OUTSIDE_PATH.length());
            params.addBodyParameter(name, file);
            String url = null;
            try {
                url = AppConfig.URL_PUBLIC + "EventAttachment/AddNewFavoriteMultipart?FileName="
                        + URLEncoder.encode(LoginGet.getBase64Password(name), "UTF-8")
                        + "&Guid=" + AppConfig.DEVICE_ID + System.currentTimeMillis()
                        + "&Total=1&Index=1";
                Log.e("hahaha", url + ":" + name);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.e("url", url);
            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("UTF-8");
            http.send(HttpRequest.HttpMethod.POST, url, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.upload),
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {

                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Log.e("hahaha", responseInfo + "");
                            Message message = new Message();
                            message.what = AppConfig.UPLOADHEAD;
                            handler.sendEmptyMessage(message.what);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Log.e("error", msg.toString());
                            Message message = new Message();
                            message.what = AppConfig.UPLOADFAILD;
                            handler.sendEmptyMessage(message.what);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }

        AppConfig.OUTSIDE_PATH = "";
    }

    public void DisplayRed(boolean flag_r) {
        tv_redcontact.setVisibility(flag_r ? View.VISIBLE : View.GONE);
        if (chi != null) {
            chi.RefreshRed(flag_r);
        }
    }

    public void kloudChaoJinhua() {
        flag_jinhua = true;
        /*FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (int i = 0; i < mTabs.size(); i++) {
            Fragment fragment = (Fragment) mTabs.get(i);
            ft.remove(fragment);
        }

//            ft.commit();
        //华为用ft.commit()，会引发异常(Can not perform this action after onSaveInstanceState)，改用这个就不会了，而且效果一样
        ft.commitAllowingStateLoss();
        ft = null;
        fm.executePendingTransactions();*/


        RippleAnimation.create(tvs.get(3)).setDuration(500).start();
        GetTabName2();
//        initDatas2();
        GoTOTab(3);
    }

    private void KillFile() {
        FileUtils fileUtils = new FileUtils(getApplicationContext());
        fileUtils.deleteFile();
        fileUtils.deleteFile2();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SpaceDocumentsActivity.REQUEST_CODE_CAPTURE_ALBUM == requestCode) {
            mTabs.get(0).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment.getClass().equals(ContactFragment.class)) {
            chi = (ContactHelpInterface) fragment;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /*if (!flag_update) {
            UpOrNoOutInfo();
            flag_update = true;
        }*/
        flag_update = !flag_update;
        if (!TextUtils.isEmpty(AppConfig.OUTSIDE_PATH) && flag_update) {
            UpOrNoOutInfo();
        }
    }


    public void onResume() {
        super.onResume();

        if (AppConfig.isToPersonalCenter) {
            GoTOTab(4);
        }
        if (RESUME) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            for (int i = 0; i < mTabs.size(); i++) {
                Fragment fragment = (Fragment) mTabs.get(i);
                ft.remove(fragment);
            }
//            ft.commit();
            //华为用ft.commit()，会引发异常(Can not perform this action after onSaveInstanceState)，改用这个就不会了，而且效果一样
            ft.commitAllowingStateLoss();
            ft = null;
            fm.executePendingTransactions();

            if (flag_jinhua) {
                GetTabName2();
//                initDatas2();
            } else {
                GetTabName();
                initDatas();
            }
            GoTOTab(4);
        }
        if (AppConfig.isRefreshRed) {
            DisplayRed(false);
        }
        RESUME = false;
        AppConfig.isToPersonalCenter = false;
        AppConfig.isRefreshRed = false;

        MobclickAgent.onPageStart("MainActivity");
        MobclickAgent.onResume(this);       //统计时长

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainActivity");
        MobclickAgent.onPause(this);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        AppConfig.isUpdateCustomer = false;
        AppConfig.isUpdateDialogue = false;
        AppConfig.HASUPDATAINFO = false;

        stopService(service);
        KillFile();
    }


}
