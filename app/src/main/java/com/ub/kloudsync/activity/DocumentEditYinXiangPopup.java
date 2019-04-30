package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.bean.SoundtrackBean;

import org.json.JSONObject;

/**
 * Created by wang on 2017/9/18.
 */

public class DocumentEditYinXiangPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private TextView cancel, ok;
    private SoundtrackBean soundtrackBean;
    private TextView syncTitle;
    private EditText edittext;

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void open();

        void dismiss();

        void editSuccess();

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
        view = layoutInflater.inflate(R.layout.document_yinxiang_popup, null);
        cancel = (TextView) view.findViewById(R.id.cancel);
        syncTitle = (TextView) view.findViewById(R.id.synctitle);
        edittext = (EditText) view.findViewById(R.id.edittext);
        ok = (TextView) view.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
                mFavoritePoPListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

    }


    @SuppressLint("NewApi")
    public void StartPop(View v, SoundtrackBean soundtrackBean) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            this.soundtrackBean = soundtrackBean;
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            syncTitle.setText(soundtrackBean.getTitle() + "");
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                dismiss();
                editSoundtrack();
                break;
            default:
                break;
        }
    }


    private void editSoundtrack() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("SoundtrackID", soundtrackBean.getSoundtrackID());
                    jsonObject.put("Title", edittext.getText().toString());
                    JSONObject returnjson = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Soundtrack/UpdateSoundtrack", jsonObject);
                    Log.e("hhh", jsonObject.toString() + "      " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        mFavoritePoPListener.editSuccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
