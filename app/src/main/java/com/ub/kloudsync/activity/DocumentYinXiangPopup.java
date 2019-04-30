package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.tool.PopupWindowUtil;

/**
 * Created by wang on 2017/9/18.
 */

public class DocumentYinXiangPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    private LinearLayout yinxiangedit;
    private LinearLayout yinxiangdelete;
    private LinearLayout yinxiangplay;
    private LinearLayout yinxiangshare;


    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void share();

        void edit();

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
        view = layoutInflater.inflate(R.layout.yinxiangoperationpopup, null);

        yinxiangedit = (LinearLayout) view.findViewById(R.id.yinxiangedit);
        yinxiangdelete = (LinearLayout) view.findViewById(R.id.yinxiangdelete);
        yinxiangplay = (LinearLayout) view.findViewById(R.id.yinxiangplay);
        yinxiangshare = (LinearLayout) view.findViewById(R.id.yinxiangshare);

        yinxiangedit.setOnClickListener(this);
        yinxiangdelete.setOnClickListener(this);
        yinxiangplay.setOnClickListener(this);
        yinxiangshare.setOnClickListener(this);

        yinxiangplay.setVisibility(View.GONE);

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
//            mPopupWindow.showAsDropDown(v);
            int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, view, 100);
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
            case R.id.yinxiangshare:
                dismiss();
                mFavoritePoPListener.share();
                break;
            case R.id.yinxiangplay:
                dismiss();
                break;
            case R.id.yinxiangdelete:
                dismiss();
                mFavoritePoPListener.delete();
                break;
            case R.id.yinxiangedit:
                dismiss();
                mFavoritePoPListener.edit();
                break;

            default:
                break;
        }
    }

}
