package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class PopDeleteDocument {

    public Context mContext;

    private static PopDeleteDismissListener popDeleteDismissListener;

    public interface PopDeleteDismissListener {
        void PopDelete();
        void Open();
        void Close();
    }

    public void setPoPDismissListener(PopDeleteDismissListener popDeleteDismissListener) {
        PopDeleteDocument.popDeleteDismissListener = popDeleteDismissListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;

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

    private TextView tv_delete, tv_cancel;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_delete_ask, null);

        tv_delete = (TextView) popupWindow.findViewById(R.id.tv_delete);
        tv_cancel = (TextView) popupWindow.findViewById(R.id.tv_cancel);

        mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);

        mPopupWindow.getWidth();
        mPopupWindow.getHeight();


        tv_delete.setOnClickListener(new myOnClick());
        tv_cancel.setOnClickListener(new myOnClick());

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popDeleteDismissListener.Close();
            }
        });

        // 使其聚焦
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    private class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_cancel:
                    mPopupWindow.dismiss();
                    break;
                case R.id.tv_delete:
                    popDeleteDismissListener.PopDelete();
                    mPopupWindow.dismiss();
                    break;

                default:
                    break;
            }

        }


    }

    public void StartPop(View v) {
        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        popDeleteDismissListener.Open();
    }


}
