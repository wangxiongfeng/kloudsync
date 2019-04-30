package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.tool.PopupWindowUtil;

/**
 * Created by wang on 2017/9/18.
 */

public class MeetingMoreOperationPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    private LinearLayout meetingproperty;
    private LinearLayout meetingdelete;
    private LinearLayout meetingshare;


    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void share();

        void property();

        void delete();

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
        view = layoutInflater.inflate(R.layout.meetingmorepopup, null);

        meetingproperty = (LinearLayout) view.findViewById(R.id.meetingproperty);
        meetingshare = (LinearLayout) view.findViewById(R.id.meetingshare);
        meetingdelete = (LinearLayout) view.findViewById(R.id.meetingdelete);

        meetingproperty.setOnClickListener(this);
        meetingshare.setOnClickListener(this);
        meetingdelete.setOnClickListener(this);

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    @SuppressLint("NewApi")
    public void StartPop(View v, int isShowStatus) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(v);
//            int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, view, 100);
//            mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
            if (isShowStatus == 0) {
                meetingproperty.setVisibility(View.GONE);
                meetingshare.setVisibility(View.GONE);
            } else if (isShowStatus == 1) {
                meetingshare.setVisibility(View.GONE);
            }
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
            case R.id.meetingproperty:
                dismiss();
                mFavoritePoPListener.property();
                break;
            case R.id.meetingshare:
                dismiss();
                mFavoritePoPListener.share();
                break;
            case R.id.meetingdelete:
                dismiss();
                mFavoritePoPListener.delete();
                break;

            default:
                break;
        }
    }

}
