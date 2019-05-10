package com.ub.techexcel.tools;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ub.techexcel.bean.PageActionBean;
import com.ub.techexcel.bean.SoundtrackBean;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Favorite;
import com.kloudsync.techexcel.service.ConnectService;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceInterfaceTools {

    public static final int GETSOUNDITEM = 0x1101;
    public static final int GETSOUNDLIST = 0x1102;
    public static final int DELETESOUNDLIST = 0x1103;
    public static final int ADDSOUNDTOLESSON = 0x1104;
    public static final int ERRORMESSAGE = 0x1105;
    public static final int SHARESOUNDTOLESSON = 0x1106;
    public static final int CREATESOUNDTOLESSON = 0x1107;
    public static final int GETPAGEACTIONS = 0x1108;
    public static final int GETPAGEACTIONSTARTTIME = 0x1109;

    private ConcurrentHashMap<Integer, ServiceInterfaceListener> hashMap = new ConcurrentHashMap<>();

    private static ServiceInterfaceTools serviceInterfaceTools;

//    public enum ServiceInterfaceToolsEnum {
//        GETSOUNDLIST(0);
//
//        public  final int VALUE;
//
//        ServiceInterfaceToolsEnum(int value) {
//            this.VALUE = value;
//        }
//    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int code = msg.what;
            if (code == ERRORMESSAGE) {

            } else {
                ServiceInterfaceListener serviceInterfaceListener = hashMap.get(code);
                if (serviceInterfaceListener != null) {
                    serviceInterfaceListener.getServiceReturnData(msg.obj);
                    hashMap.remove(code);
                    Log.e("---------HashMap", hashMap.size() + "");
                }
            }
        }
    };


    public static ServiceInterfaceTools getinstance() {
        if (serviceInterfaceTools == null) {
            synchronized (ServiceInterfaceTools.class) {
                if (serviceInterfaceTools == null) {
                    serviceInterfaceTools = new ServiceInterfaceTools();
                }
            }
        }
        return serviceInterfaceTools;
    }


    public void getSoundItem(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("hhh", url + "  获取音响item    " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject retdata = returnjson.getJSONObject("RetData");
                        SoundtrackBean soundtrackBean1 = new SoundtrackBean();
                        soundtrackBean1.setSoundtrackID(retdata.getInt("SoundtrackID"));
                        soundtrackBean1.setTitle(retdata.getString("Title"));
                        soundtrackBean1.setAttachmentId(retdata.getString("AttachmentID"));
                        soundtrackBean1.setCreatedDate(retdata.getString("CreatedDate"));
                        soundtrackBean1.setBackgroudMusicAttachmentID(retdata.getInt("BackgroudMusicAttachmentID"));
                        soundtrackBean1.setNewAudioAttachmentID(retdata.getInt("NewAudioAttachmentID"));
                        soundtrackBean1.setSelectedAudioAttachmentID(retdata.getInt("SelectedAudioAttachmentID"));

                        if (soundtrackBean1.getBackgroudMusicAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject = retdata.getJSONObject("BackgroudMusicInfo");
                                Favorite favoriteAudio = new Favorite();
                                favoriteAudio.setFileDownloadURL(jsonObject.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject.getInt("ItemID"));
                                favoriteAudio.setTitle(jsonObject.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject.getInt("AttachmentID"));
                                favoriteAudio.setDuration(jsonObject.getString("VideoDuration"));
                                soundtrackBean1.setBackgroudMusicInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean1.setBackgroudMusicInfo(new Favorite());
                                e.printStackTrace();
                            }
                        }
                        if (soundtrackBean1.getNewAudioAttachmentID() != 0) {
                            try {
                                if (!retdata.isNull("NewAudioInfo")) {

                                }
                                JSONObject jsonObject = retdata.getJSONObject("NewAudioInfo");
                                Favorite favoriteAudio = new Favorite();
                                favoriteAudio.setFileDownloadURL(jsonObject.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject.getInt("ItemID"));
                                favoriteAudio.setTitle(jsonObject.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject.getInt("AttachmentID"));
                                favoriteAudio.setDuration(jsonObject.getString("VideoDuration"));
                                soundtrackBean1.setNewAudioInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean1.setNewAudioInfo(new Favorite());
                                e.printStackTrace();
                            }
                        }
                        if (soundtrackBean1.getSelectedAudioAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject = retdata.getJSONObject("SelectedAudioInfo");
                                Favorite favoriteAudio = new Favorite();
                                favoriteAudio.setFileDownloadURL(jsonObject.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject.getInt("ItemID"));
                                favoriteAudio.setTitle(jsonObject.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject.getInt("AttachmentID"));
                                favoriteAudio.setDuration(jsonObject.getString("VideoDuration"));
                                soundtrackBean1.setSelectedAudioInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean1.setSelectedAudioInfo(new Favorite());
                                e.printStackTrace();
                            }
                        }
                        soundtrackBean1.setBackgroudMusicTitle(retdata.getString("BackgroudMusicTitle"));
                        soundtrackBean1.setSelectedAudioTitle(retdata.getString("SelectedAudioTitle"));
                        soundtrackBean1.setNewAudioTitle(retdata.getString("NewAudioTitle"));

                        soundtrackBean1.setDuration(retdata.getString("Duration"));

                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = soundtrackBean1;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void getSoundList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener, final boolean isHidden, final boolean ishavepresenter) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        final JSONArray array = returnjson.getJSONArray("RetData");
                        List<SoundtrackBean> mlist = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            SoundtrackBean soundtrackBean = new SoundtrackBean();
                            soundtrackBean.setSoundtrackID(jsonObject.getInt("SoundtrackID"));
                            soundtrackBean.setTitle(jsonObject.getString("Title"));
                            soundtrackBean.setUserID(jsonObject.getString("UserID"));
                            soundtrackBean.setUserName(jsonObject.getString("UserName"));
                            soundtrackBean.setAvatarUrl(jsonObject.getString("AvatarUrl"));
                            soundtrackBean.setDuration(jsonObject.getString("Duration"));
                            soundtrackBean.setCreatedDate(jsonObject.getString("CreatedDate"));
                            soundtrackBean.setHidden(isHidden);
                            soundtrackBean.setHavePresenter(ishavepresenter);
                            mlist.add(soundtrackBean);
                        }
                        Message msg3 = Message.obtain();
                        msg3.obj = mlist;
                        msg3.what = code;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void deleteSound(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentDataattachment(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void addSoundToLesson(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, null);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void shareDocument(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, null);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getString("RetData");
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public void getPageActions(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {

        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {

                        PageActionBean pageActionBean = new PageActionBean();
                        JSONObject retdata = returnjson.getJSONObject("RetData");
                        pageActionBean.setPageNumber(retdata.getString("PageNumber"));
                        JSONArray jsonArray = retdata.getJSONArray("Actions");

                        String mmm = "";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String ddd = jsonObject.getString("Data");
                            if (!TextUtil.isEmpty(ddd)) {
                                String dd = "'" + Tools.getFromBase64(ddd) + "'";
                                if (i == 0) {
                                    mmm += "[" + dd;
                                } else {
                                    mmm += "," + dd;
                                }
                                if (i == jsonArray.length() - 1) {
                                    mmm += "]";
                                }
                            }
                        }
                        pageActionBean.setActions(mmm);

                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = pageActionBean;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    public void getPageActionStartTime(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {

        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {

                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getInt("RetData");
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private void putInterface(int code, ServiceInterfaceListener serviceInterfaceListener) {
        ServiceInterfaceListener serviceInterfaceListener2 = hashMap.get(code);
        if (serviceInterfaceListener2 != null) {
            hashMap.remove(code);
        }
        hashMap.put(code, serviceInterfaceListener);
    }


    //创建音响
    public void createYinxiang(final String url, final int code, final String attachmentId, final String recordingId, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("AttachmentID", Integer.parseInt(attachmentId));
                    String time = new SimpleDateFormat("yyyyMMdd_hh:mm").format(new Date());
                    jsonObject.put("Title", AppConfig.UserName + "_" + time);
                    jsonObject.put("EnableBackgroud", 1);
                    jsonObject.put("EnableSelectVoice", 1);
                    jsonObject.put("EnableRecordNewVoice", 1);
                    jsonObject.put("Type", 1);
                    jsonObject.put("RecordingID", recordingId);

                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);

                    Log.e("Agora", jsonObject.toString() + "      " + returnjson.toString());

                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject jsonObject1 = returnjson.getJSONObject("RetData");
                        SoundtrackBean soundtrackBean = new SoundtrackBean();
                        soundtrackBean.setSoundtrackID(jsonObject1.getInt("SoundtrackID"));
                        soundtrackBean.setTitle(jsonObject1.getString("Title"));
                        soundtrackBean.setUserID(jsonObject1.getString("UserID"));
                        soundtrackBean.setUserName(jsonObject1.getString("UserName"));
                        soundtrackBean.setAvatarUrl(jsonObject1.getString("AvatarUrl"));
                        soundtrackBean.setDuration(jsonObject1.getString("Duration"));
                        soundtrackBean.setCreatedDate(jsonObject1.getString("CreatedDate"));

                        soundtrackBean.setBackgroudMusicAttachmentID(jsonObject1.getInt("BackgroudMusicAttachmentID"));
                        soundtrackBean.setNewAudioAttachmentID(jsonObject1.getInt("NewAudioAttachmentID"));
                        soundtrackBean.setSelectedAudioAttachmentID(jsonObject1.getInt("SelectedAudioAttachmentID"));

                        if (soundtrackBean.getBackgroudMusicAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("BackgroudMusicInfo");
                                Favorite favoriteAudio = new Favorite();
                                favoriteAudio.setFileDownloadURL(jsonObject2.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject2.getInt("ItemID"));
                                favoriteAudio.setTitle(jsonObject2.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject2.getInt("AttachmentID"));
                                favoriteAudio.setDuration(jsonObject2.getString("VideoDuration"));
                                soundtrackBean.setBackgroudMusicInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean.setBackgroudMusicInfo(new Favorite());
                                e.printStackTrace();
                            }
                        }
                        if (soundtrackBean.getSelectedAudioAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject3 = jsonObject1.getJSONObject("SelectedAudioInfo");
                                Favorite favoriteAudio = new Favorite();
                                favoriteAudio.setFileDownloadURL(jsonObject3.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject3.getInt("ItemID"));
                                favoriteAudio.setTitle(jsonObject3.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject3.getInt("AttachmentID"));
                                favoriteAudio.setDuration(jsonObject3.getString("VideoDuration"));
                                soundtrackBean.setSelectedAudioInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean.setSelectedAudioInfo(new Favorite());
                                e.printStackTrace();
                            }
                        }
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = soundtrackBean;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
