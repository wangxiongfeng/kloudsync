package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ub.techexcel.bean.SoundtrackBean;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Favorite;
import com.kloudsync.techexcel.service.ConnectService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wang on 2017/9/18.
 */

public class YinxiangCreatePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView close;
    private TextView addaudio, addrecord;
    //    private CheckBox checkBox1, checkBox2;
    private EditText edittext;
    private ImageView delete1, delete2;

    private TextView recordsync, cancel;
    private Favorite favorite = new Favorite();
    private Favorite recordfavorite = new Favorite();
    private CheckBox checkBox;
    private String attachmentId;
    private static FavoritePoPListener mFavoritePoPListener;
    private TextView recordname, recordtime;
    private TextView bgname, bgtime;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1001:
                    mFavoritePoPListener.syncorrecord(checkBox.isChecked(), soundtrackBean);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void addrecord(int isrecord);

        void addaudio(int isrecord);

        void syncorrecord(boolean checked, SoundtrackBean soundtrackBean);
    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }


    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.yinxiang_create_popup, null);
        close = (ImageView) view.findViewById(R.id.close);
        cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        addaudio = (TextView) view.findViewById(R.id.addaudio);
        addrecord = (TextView) view.findViewById(R.id.addrecord);
        recordname = (TextView) view.findViewById(R.id.recordname);
        recordtime = (TextView) view.findViewById(R.id.recordtime);
        bgname = (TextView) view.findViewById(R.id.bgname);
        bgtime = (TextView) view.findViewById(R.id.bgtime);

        edittext = (EditText) view.findViewById(R.id.edittext);
        String time = new SimpleDateFormat("yyyyMMdd_hh:mm").format(new Date());
        edittext.setText(AppConfig.UserName + "_" + time);
        checkBox = (CheckBox) view.findViewById(R.id.checkboxx);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                delete2.setVisibility(View.GONE);
                recordname.setVisibility(View.GONE);
                recordtime.setVisibility(View.GONE);
                recordfavorite = new Favorite();

                if (isChecked) {
                    addrecord.setVisibility(View.GONE);
                    recordsync.setText("Record & Sync");
                } else {
                    addrecord.setVisibility(View.VISIBLE);
                    recordsync.setText("Sync");
                }
            }
        });

        delete1 = (ImageView) view.findViewById(R.id.delete1);
        delete2 = (ImageView) view.findViewById(R.id.delete2);
        delete1.setOnClickListener(this);
        delete2.setOnClickListener(this);

        recordsync = (TextView) view.findViewById(R.id.recordsync);
        recordsync.setOnClickListener(this);
        close.setOnClickListener(this);
        addaudio.setOnClickListener(this);
        addrecord.setOnClickListener(this);
        recordsync.setText("Sync");
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFavoritePoPListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
    }


    public void setAudioBean(Favorite favorite) {
        this.favorite = favorite;
        delete1.setVisibility(View.VISIBLE);
        addaudio.setVisibility(View.GONE);
        if (favorite != null) {
            bgname.setVisibility(View.VISIBLE);
            bgtime.setVisibility(View.VISIBLE);
            bgname.setText("Audio: " + favorite.getTitle());
        }
    }

    public void setRecordBean(Favorite favorite) {

        this.recordfavorite = favorite;
        delete2.setVisibility(View.VISIBLE);
        addrecord.setVisibility(View.GONE);
        checkBox.setVisibility(View.GONE);
        checkBox.setChecked(false);
        recordsync.setText("Sync");
        if (recordfavorite != null) {
            recordname.setVisibility(View.VISIBLE);
            recordtime.setVisibility(View.VISIBLE);
            recordname.setText("Voice: " + recordfavorite.getTitle());
        }
    }


    @SuppressLint("NewApi")
    public void StartPop(View v, String attachmentId) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            this.attachmentId = attachmentId;
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    private SoundtrackBean soundtrackBean = new SoundtrackBean();

    private void createSoundtrack() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("AttachmentID", Integer.parseInt(attachmentId));

                    if (recordfavorite == null) {
                        recordfavorite = new Favorite();
                        recordfavorite.setAttachmentID(0);
                    }
                    jsonObject.put("SelectedAudioAttachmentID", recordfavorite.getAttachmentID());
                    jsonObject.put("SelectedAudioTitle", recordfavorite.getAttachmentID() == 0 ? "" : recordfavorite.getTitle());
                    if (favorite == null) {
                        favorite = new Favorite();
                        favorite.setAttachmentID(0);
                    }

                    jsonObject.put("BackgroudMusicAttachmentID", favorite.getAttachmentID());

                    jsonObject.put("Title", edittext.getText().toString());
                    jsonObject.put("EnableBackgroud", 1);
                    jsonObject.put("EnableSelectVoice", 1);
                    jsonObject.put("EnableRecordNewVoice", checkBox.isChecked() ? 1 : 0);
                    jsonObject.put("SelectedAudioTitle", recordfavorite.getAttachmentID() == 0 ? "" : recordfavorite.getTitle());
                    jsonObject.put("BackgroudMusicTitle", favorite.getAttachmentID() == 0 ? "" : favorite.getTitle());
                    JSONObject returnjson = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Soundtrack/CreateSoundtrack", jsonObject);
                    Log.e("hhh", jsonObject.toString() + "      " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject jsonObject1 = returnjson.getJSONObject("RetData");
                        soundtrackBean = new SoundtrackBean();
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
                        msg3.obj = soundtrackBean;
                        msg3.what = 0x1001;
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();
                break;
            case R.id.addaudio:
                mFavoritePoPListener.addaudio(0);
                break;
            case R.id.addrecord:
                mFavoritePoPListener.addrecord(1);
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.recordsync:
                dismiss();
                createSoundtrack();

                break;
            case R.id.delete1:
                favorite = new Favorite();
                delete1.setVisibility(View.GONE);
                addaudio.setVisibility(View.VISIBLE);
                bgname.setVisibility(View.GONE);
                bgtime.setVisibility(View.GONE);
                break;
            case R.id.delete2:
                recordfavorite = new Favorite();
                delete2.setVisibility(View.GONE);
                addrecord.setVisibility(View.VISIBLE);
                recordname.setVisibility(View.GONE);
                recordtime.setVisibility(View.GONE);

                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(false);
                recordsync.setText("Sync");
                break;
            default:
                break;
        }
    }

}
