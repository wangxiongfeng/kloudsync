package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.ub.kloudsync.activity.TeamSpaceBeanFile;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class PopEditDocument {

    public Context mContext;

    private static PopEditDocumentListener popEditDocumentListener;

    TeamSpaceBeanFile lesson;

    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(mContext,
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.RenameAttachment:
                    popEditDocumentListener.PopEdit();
                    mPopupWindow.dismiss();
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            mContext,
                            mContext.getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();
                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            mContext,
                            mContext.getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;
            }
        }
    };


    public interface PopEditDocumentListener {
        void PopEdit();
        void dismiss();
        void open();
    }

    public void setPopEditDocumentListener(PopEditDocumentListener popEditDocumentListener) {
        PopEditDocument.popEditDocumentListener = popEditDocumentListener;
    }

    public void getPopwindow(Context context,TeamSpaceBeanFile lesson) {
        this.mContext = context;
        this.lesson = lesson;

        getPopupWindowInstance();
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation3);
    }


    public PopupWindow mPopupWindow;

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    private TextView cancel, ok;
    private TextView tv_Date;
    private TextView tv_name;
    private EditText et_title;
    private EditText et_tag;
    private View popupWindow;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.document_edit_popup, null);

        cancel = (TextView) popupWindow.findViewById(R.id.cancel);
        ok = (TextView) popupWindow.findViewById(R.id.ok);
        tv_Date = (TextView) popupWindow.findViewById(R.id.tv_Date);
        tv_name = (TextView) popupWindow.findViewById(R.id.tv_name);
        et_tag = (EditText) popupWindow.findViewById(R.id.et_tag);
        et_title = (EditText) popupWindow.findViewById(R.id.et_title);

        showInfo();

        mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);

        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        cancel.setOnClickListener(new myOnClick());
        ok.setOnClickListener(new myOnClick());

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popEditDocumentListener.dismiss();
            }
        });

        // 使其聚焦
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void showInfo() {
        tv_name.setText(lesson.getFilename());
        et_title.setText(lesson.getTitle());

        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss");
        Long datetime = Long.parseLong(lesson.getCreatedDate());
        String date = sdf.format(datetime);
        tv_Date.setText(date);

    }


    private class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cancel:
                    mPopupWindow.dismiss();
                    break;
                case R.id.ok:
                    ChangeDuang();
                    break;

                default:
                    break;
            }

        }

    }

    private void ChangeDuang() {
        final JSONObject jsonObject = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "SpaceAttachment/RenameAttachment?itemID=" + lesson.getItemID()
                                    + "&title=" + LoginGet.getBase64Password(et_title.getText().toString()), jsonObject);
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.RenameAttachment;
                        msg.obj = responsedata.getString("RetData");
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(mContext)) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    public void StartPop(View v) {
        popEditDocumentListener.open();
        mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }


}
