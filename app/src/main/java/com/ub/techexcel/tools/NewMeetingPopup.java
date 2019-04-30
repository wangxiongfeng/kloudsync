package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.tool.PopupWindowUtil;

/**
 * Created by wang on 2017/9/18.
 */

public class NewMeetingPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    private RelativeLayout schedulecoursetype;
    private RelativeLayout startanewmeeting;


    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void scheduleMeeting();

        void startNewMeeting();

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
        view = layoutInflater.inflate(R.layout.newmeeting, null);

        schedulecoursetype = (RelativeLayout) view.findViewById(R.id.schedulecoursetype);
        startanewmeeting = (RelativeLayout) view.findViewById(R.id.startanewmeeting);

        schedulecoursetype.setOnClickListener(this);
        startanewmeeting.setOnClickListener(this);

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
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            float dpdpx = dp2px(mContext, 180);
            float widths = width / 3;
            float hh = 0;
            if (dpdpx > widths) {
                hh = dpdpx - widths;
            }
            Log.e("ssss", dpdpx+"   "+widths+"   "+hh);
            mPopupWindow.showAsDropDown(v, -(dp2px(mContext, 0) + (int) hh), 0);
//            int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, view, 100);
//            mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
        }
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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
            case R.id.startanewmeeting:
                dismiss();
                mFavoritePoPListener.startNewMeeting();
                break;
            case R.id.schedulecoursetype:
                dismiss();
                mFavoritePoPListener.scheduleMeeting();
                break;

            default:
                break;
        }
    }

}
