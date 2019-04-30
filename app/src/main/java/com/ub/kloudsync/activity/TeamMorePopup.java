package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

/**
 * Created by wang on 2017/9/18.
 */

public class TeamMorePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    private TextView deleteTeam;
    private TextView rename;
    private TextView quitteam;
    private TextView tv_edit;

    private boolean isTeam;

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void delete();

        void rename();

        void quit();

        void edit();


    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    public void setIsTeam(boolean isTeam) {
        this.isTeam = isTeam;
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
        tv_edit = (TextView) view.findViewById(R.id.tv_edit);
        deleteTeam.setOnClickListener(this);
        rename.setOnClickListener(this);
        quitteam.setOnClickListener(this);
        tv_edit.setOnClickListener(this);

        deleteTeam.setText(isTeam ? "Delete team" : "Delete space");
        quitteam.setText(isTeam ? "Quit team" : "Quit space");

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
            mPopupWindow.showAsDropDown(v);
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
                mFavoritePoPListener.delete();
                break;
            case R.id.rename:
                dismiss();
                mFavoritePoPListener.rename();
                break;
            case R.id.quitteam:
                dismiss();
                mFavoritePoPListener.quit();
                break;
            case R.id.tv_edit:
                dismiss();
                mFavoritePoPListener.edit();
                break;

            default:
                break;
        }
    }

}
