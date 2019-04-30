package com.ub.service.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kloudsync.techexcel.tool.Md5Tool;
import com.ub.techexcel.bean.NotifyBean;
import com.ub.techexcel.tools.SpliteSocket;
import com.ub.techexcel.tools.Tools;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.plugin.SingleCallActivity2;
import com.kloudsync.techexcel.info.Customer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;

/**
 * Created by wang on 2017/9/1.
 * //    private String uri = "ws://123.127.97.142:9733/MeetingServer/websocket";
 * //    private String uri = "ws://ub.servicewise.net.cn:8080/MeetingServer/websocket";
 * //    private String uri = "wss://pt.techexcel.com:8443/MeetingServer/websocket";
 * //    private String uri = "ws://pt.techexcel.com:8080/MeetingServer/websocket";
 * //    private String uri = "wss://peertime.cn:8443/MeetingServer/websocket";
 * //    private String uri = "ws://wss.peertime.cn:8080/MeetingServer/websocket";
 * //    uri = "ws://wss.peertime.cn:8080/MeetingServer/websocket";
 * uri = "wss://wss.peertime.cn:8443/MeetingServer/websocket";
 */

public class SocketService extends Service {
    private String uri;
    private WebSocketClient mWebSocketClient;
    private Timer timer;
    private int lastactiontime = 0;
    private String changeNumber = "0";

    private BroadcastReceiver changeNumberBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            changeNumber = intent.getStringExtra("changeNumber");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initSocket();
        registerNumberReceiver();
    }

    private void registerNumberReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn.changenumber");
        registerReceiver(changeNumberBroadcast, intentFilter);
    }


    private String tooooo;

    private void initSocket() {
        try {
            tooooo = AppConfig.UserToken + "";
            uri = AppConfig.COURSE_SOCKET + File.separator + AppConfig.UserToken
                    + File.separator + "2" + File.separator + Md5Tool.getUUID();
            Log.e("socket服务器返回结果--------2", uri + "");
            mWebSocketClient = new WebSocketClient(new URI(uri)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.e("socket服务器返回结果--------2", "连接成功");
                    AppConfig.webSocketClient = mWebSocketClient;
                    if (timer2 != null) {
                        timer2.cancel();
                        timer2 = null;
                    }
                    if (timer == null) {
                        timer = new Timer();
                        TimerTask task = new TimerTask() {   // 每隔5秒发心跳
                            @Override
                            public void run() {
                                try {
                                    if (!tooooo.equals(AppConfig.UserToken)) {
                                        flag = false;
                                        reconnect();
                                    } else {
                                        JSONObject loginjson = new JSONObject();
                                        loginjson.put("action", "HELLO");
                                        loginjson.put("sessionId", AppConfig.UserToken);
                                        loginjson.put("changeNumber", changeNumber);
                                        if (AppConfig.isPresenter) {
                                            loginjson.put("status", AppConfig.status);
                                            loginjson.put("currentLine", AppConfig.currentLine);
                                            loginjson.put("currentMode", AppConfig.currentMode);
                                            loginjson.put("currentPageNumber", AppConfig.currentPageNumber);
                                            loginjson.put("currentItemId", AppConfig.currentDocId);
                                        }

                                        String ss = loginjson.toString();
                                        SpliteSocket.sendMesageBySocket(ss);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        timer.schedule(task, 1000, 5000);  // 开启计时器
                    }
                }

                @Override
                public void onMessage(String message) {
                    String msg = Tools.getFromBase64(message);
                    Log.e("socket服务器返回结果--------2", msg);
                    String actionString = getRetCodeByReturnData2("action", msg);
                    if (TextUtils.isEmpty(actionString)) {
                        return;
                    }
                    if (actionString.equals("LOGIN")) { // login success
                        String tojoinmeeting = getRetCodeByReturnData2("toJoinMeeting", msg);
                        if (!TextUtils.isEmpty(tojoinmeeting)) {
                            String[] ss = tojoinmeeting.split(",");
                            for (String s : ss) {
                                notifyleave(s);
                            }
                        }
                    }
                    if (actionString.equals("REMOVE_JOIN_MEETING_NOTICE")) {
                        String meetingids = getRetCodeByReturnData2("meetingIds", msg);
                        String[] meetingArray = meetingids.split(",");
                        for (String s : meetingArray) {
                            for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
                                if (AppConfig.progressCourse.get(i).getMeetingId().equals(s)) {
                                    AppConfig.progressCourse.remove(i);
                                    i--;
                                }
                            }
                        }
                        Intent intent = new Intent();
                        intent.setAction(getResources().getString(R.string.Receive_Course));
                        sendBroadcast(intent);
                    }
                    if (actionString.equals("UPDATE_TO_JOIN_MEETING_READ_STATUS")) {
                        String meetingids = getRetCodeByReturnData2("meetingIds", msg);
                        String[] meetingid = meetingids.split(",");
                        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
                            NotifyBean notifyBean = AppConfig.progressCourse.get(i);
                            for (String ss : meetingid) {
                                if (notifyBean.getMeetingId().equals(ss)) {
                                    notifyBean.setStatus(true);
                                    break;
                                }
                            }
                        }
                        Intent intent = new Intent();
                        intent.setAction(getResources().getString(R.string.Receive_Course));
                        sendBroadcast(intent);
                    }
                    //老师 结束课程  所有人离开
                    if (actionString.equals("END_MEETING")) {
                        notifyend(getRetCodeByReturnData2("meetingId", msg));
                    }
                    /**
                     /* 消息的类型 /
                     typedef NS_ENUM(NSInteger, ComponentModelActionType) {
                     /* 老师给学生发送上课消息 /
                     CMTeacherSendMessageToStudent = 1,
                     /* 发送群体消息让在meeting里的人全屏并且旋转 /
                     CMTeacherSendFullScreenMessageToAllParticipant = 2,
                     /* 邀请旁听者的消息 /
                     CMSendMessageToAuditor = 3,
                     /* 邀请对方语音 /
                     CMSendMessageToInviteAudio = 4,
                     /* 邀请对方视频 /
                     CMSendMessageToInviteVedio = 5,
                     /* 告诉对方我已经接受通话 /
                     CMSendMessageToTellOtherIHaveAccpet = 6,
                     /* 告诉对方我已经挂断 /
                     CMSendMessageToTellOtherIHaveHangUp = 7,
                     /* 告诉所有人切换文档了 /
                     CMSendMessageToTellOtherToSwitchDocument = 8
                     };
                     */
                    if (actionString.equals("SEND_MESSAGE")) {
                        String d = getRetCodeByReturnData2("data", msg);
                        try {
                            final JSONObject jsonObject = new JSONObject(Tools.getFromBase64(d));
                            if (jsonObject.getInt("actionType") == 1 || jsonObject.getInt("actionType") == 3) { // 旧的课程 邀请学生上课
                                if ((!WatchCourseActivity2.watch2instance) && (!WatchCourseActivity3.watch3instance)) {
                                    Intent intent = new Intent(SocketService.this, AlertDialogActivity.class);
                                    intent.putExtra("jsonObject", jsonObject.toString());
                                    intent.putExtra("isNewCourse", 0);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } else if (jsonObject.getInt("actionType") == 10) { // 新的课程
                                if ((!WatchCourseActivity2.watch2instance) && (!WatchCourseActivity3.watch3instance)) {
                                    Intent intent = new Intent(SocketService.this, AlertDialogActivity.class);
                                    intent.putExtra("jsonObject", jsonObject.toString());
                                    intent.putExtra("isNewCourse", 1);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } else if (jsonObject.getInt("actionType") == 4) { //语音
                                Customer cus = new Customer();
                                cus.setUserID(jsonObject.getString("sourceUserId"));
                                cus.setName(jsonObject.getString("sourceUserName"));
                                cus.setUrl(jsonObject.getString("sourceAvatarUrl"));
                                String action = RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO2;
                                Intent intent = new Intent(action);
                                intent.putExtra("Customer", cus);
                                intent.putExtra("callAction", RongCallAction.ACTION_INCOMING_CALL.getName());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setPackage(getApplication().getPackageName());
                                startActivity(intent);
                            } else if (jsonObject.getInt("actionType") == 5) { //视频音
                                Customer cus = new Customer();
                                cus.setUserID(jsonObject.getString("sourceUserId"));
                                cus.setName(jsonObject.getString("sourceUserName"));
                                cus.setUrl(jsonObject.getString("sourceAvatarUrl"));
                                String action = RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO2;
                                Intent intent = new Intent(action);
                                intent.putExtra("Customer", cus);
                                intent.putExtra("callAction", RongCallAction.ACTION_INCOMING_CALL.getName());
                                intent.putExtra("checkPermissions", true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setPackage(getApplication().getPackageName());
                                startActivity(intent);
                            } else if (jsonObject.getInt("actionType") == 6 && jsonObject.getInt("mediaType") == 1) {
                                Intent intent = new Intent();
                                intent.setAction(getString(R.string.Receive_Spectator));
                                sendBroadcast(intent);
                            } else if (jsonObject.getInt("actionType") == 6 && jsonObject.getInt("mediaType") == 2) { // 视频音

                            } else if (jsonObject.getInt("actionType") == 7) {
                                if (SingleCallActivity2.instance != null) {
                                    SingleCallActivity2.instance.finish();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent();
                    intent.setAction("com.cn.socket");
                    intent.putExtra("message", message);
                    sendBroadcast(intent);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.e("socket服务器返回结果--------2", "onClose   " + code + "   " + reason);
                    reconnect();

                }

                @Override
                public void onError(Exception ex) {
                    Log.e("socket服务器返回结果--------2", "onerror   " + ex.getMessage());
                    Toast.makeText(getApplicationContext(), "WebSocketClient onError", Toast.LENGTH_LONG).show();
//                    reconnect();
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mWebSocketClient.connect();


                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Timer timer2;

    public synchronized void reconnect() {
        Log.e("socket服务器返回结果--------2", "socket  要不要 重连" + Tools.isNetworkConnected(this));
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (!flag) {
            timer2 = new Timer();
            timer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    initSocket();
                }
            }, 5000);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 获得socket返回的action
     */
    private String getRetCodeByReturnData2(String str, String returnData) {
        if (!TextUtils.isEmpty(returnData)) {
            try {
                JSONObject jsonObject = new JSONObject(returnData);
                if (jsonObject.has(str)) {
                    return jsonObject.getString(str) + "";
                } else {
                    return "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    boolean flag;

    @Override
    public void onDestroy() {
        flag = true;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timer2 != null) {
            timer2.cancel();
            timer2 = null;
        }
        mWebSocketClient.close();
        mWebSocketClient = null;
        if (changeNumberBroadcast != null) {
            unregisterReceiver(changeNumberBroadcast);
        }
        super.onDestroy();
    }

    /**
     * 登录判读是否已读
     *
     * @param meetingId
     */
    private void notifyleave(String meetingId) {
        String meetid = "";
        boolean status;
        if (!TextUtils.isEmpty(meetingId)) {
            int i = meetingId.indexOf("&");
            meetid = meetingId.substring(0, i);
            status = meetingId.substring(i + 1).equals("0") ? false : true;
        } else {
            return;
        }
        boolean isExist = false;
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (AppConfig.progressCourse.get(i).getMeetingId().equals(meetid)) {
                AppConfig.progressCourse.get(i).setStatus(status);
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            NotifyBean notifyBean = new NotifyBean();
            notifyBean.setMeetingId(meetid);
            notifyBean.setStatus(status);
            AppConfig.progressCourse.add(notifyBean);
        }
        Intent intent = new Intent();
        intent.setAction(getResources().getString(R.string.Receive_Course));
        sendBroadcast(intent);
    }

    private void notifyend(String meetingId) {
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (AppConfig.progressCourse.get(i).getMeetingId().equals(meetingId)) {
                AppConfig.progressCourse.remove(i);
                Intent removeintent = new Intent();
                removeintent.setAction(getResources().getString(R.string.Receive_Course));
                sendBroadcast(removeintent);
                break;
            }
        }
    }


}
