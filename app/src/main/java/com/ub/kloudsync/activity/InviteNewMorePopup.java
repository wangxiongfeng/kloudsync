package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.tool.PopupWindowUtil;

/**
 * Created by wang on 2017/9/18.
 */

public class InviteNewMorePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    private TextView deleteTeam;
    private TextView rename;
    private TextView quitteam;


    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void sendMeaage();

        void setAdmin();

        void removeTeam();

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
        view = layoutInflater.inflate(R.layout.team_more_popup, null);

        deleteTeam = (TextView) view.findViewById(R.id.deleteTeam);
        rename = (TextView) view.findViewById(R.id.rename);
        quitteam = (TextView) view.findViewById(R.id.quitteam);
        deleteTeam.setOnClickListener(this);
        rename.setOnClickListener(this);
        quitteam.setOnClickListener(this);

        rename.setText("Send message");
        deleteTeam.setText("Set as team admin");
        quitteam.setText( "Remove from team");

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

            int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, view , 100);

            mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
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
            case R.id.deleteTeam:
                dismiss();
                mFavoritePoPListener.sendMeaage();
                break;
            case R.id.rename:
                dismiss();
                mFavoritePoPListener.setAdmin();
                break;
            case R.id.quitteam:
                dismiss();
                mFavoritePoPListener.removeTeam();
                break;

            default:
                break;
        }
    }

}
