package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DialogTSDelete;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Space;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wang on 2017/9/18.
 */

public class SpaceDeletePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    private TextView ok, cancel;
    private TextView tv_space, tv_team;
    private TextView tv_note, tv_title;
    private RelativeLayout rl_space, rl_team;

    private static FavoritePoPListener mFavoritePoPListener;

    private ArrayList<Customer> cuslist = new ArrayList<Customer>();
    private int spaceid;

    private int tid = -1;
    private int sid = -1;

    private boolean flagM;
    private TeamSpaceBeanFile lesson;


    @SuppressLint("HandlerLeak")
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
                case AppConfig.SwitchSpace:
                    mFavoritePoPListener.refresh();
                    EventBus.getDefault().post(new TeamSpaceBean());
                    dismiss();
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



    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void delete(int spaceid);

        void refresh();
    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }


    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void setSP(ArrayList<Customer> cuslist, int spaceid){
        this.cuslist = cuslist;
        this.spaceid = spaceid;
    }

    public void ChangeMove(TeamSpaceBeanFile lesson){
        flagM = true;
        this.lesson = lesson;
        if(tv_note!=null){
            tv_note.setVisibility(View.GONE);
            tv_title.setText("Move Document");
        }
    }
    public void ChangeMove2(){
        flagM = false;
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
        view = layoutInflater.inflate(R.layout.space_delete_popup, null);

        cancel = (TextView) view.findViewById(R.id.cancel);
        ok = (TextView) view.findViewById(R.id.ok);
        tv_space = (TextView) view.findViewById(R.id.tv_space);
        tv_team = (TextView) view.findViewById(R.id.tv_team);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_note = (TextView) view.findViewById(R.id.tv_note);
        rl_space = (RelativeLayout) view.findViewById(R.id.rl_space);
        rl_team = (RelativeLayout) view.findViewById(R.id.rl_team);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        rl_team.setOnClickListener(this);
        rl_space.setOnClickListener(this);

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
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                BoyNextDoor();
                break;
            case R.id.rl_space:
                SelectID(1);
                break;
            case R.id.rl_team:
                SelectID(0);
                break;

            default:
                break;
        }
    }

    private void BoyNextDoor() {
        if(sid < 0){
            Toast.makeText(mContext,"Please select space first",Toast.LENGTH_LONG).show();
            return;
        }
        mFavoritePoPListener.delete(sid);
        if(flagM){
            SwitchSpace();
        }else {
            dismiss();
        }

    }

    private void SwitchSpace() {
        final JSONObject jsonObject = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "SpaceAttachment/SwitchSpace?itemID=" + lesson.getItemID()
                                    + "&spaceID=" + sid, jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.SwitchSpace;
//                        JSONObject RetData = responsedata.getJSONObject("RetData");
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

    private void SelectID(int type) {
        DialogTSDelete dts = new DialogTSDelete();
        dts.setPoPDismissListener(new DialogTSDelete.DialogDismissListener() {
            @Override
            public void PopSelect(Space sp, int type) {
                if(0 == type){
                    tv_team.setText(sp.getName());
                    if(tid != sp.getItemID()){
                        sid = -1;
                    }
                    tid = sp.getItemID();
                } else if(1 == type){
                    tv_space.setText(sp.getName());
                    sid = sp.getItemID();
                }
            }
        });
        if(0 == type) {
            dts.SetType(type, spaceid);
        }else if(1 == type) {
            if(tid < 0){
                Toast.makeText(mContext,"Please select team first",Toast.LENGTH_LONG).show();
                return;
            }
            dts.SetType(type, tid);
        }
        dts.EditCancel(mContext, cuslist);
    }

}
