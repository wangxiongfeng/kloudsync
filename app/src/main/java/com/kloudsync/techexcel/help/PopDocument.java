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
import com.kloudsync.techexcel.tool.PopupWindowUtil;
import com.ub.kloudsync.activity.TeamSpaceBeanFile;

public class PopDocument {

    public Context mContext;

    private static PopDocumentListener popDocumentListener;

    TeamSpaceBeanFile lesson;


    public interface PopDocumentListener {
        void PopView();
        void PopDelete();
        void PopEdit();
        void PopShare();
        void PopMove();
        void PopBack();
    }

    public void setPoPMoreListener(PopDocumentListener popDocumentListener) {
        PopDocument.popDocumentListener = popDocumentListener;
    }

    public void getPopwindow(Context context, TeamSpaceBeanFile lesson) {
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

    private TextView tv_view, tv_edit;
    private TextView tv_delete;
    private TextView tv_move;
    private TextView tv_share;
    private View popupWindow;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.pop_document, null);

        tv_view = (TextView) popupWindow.findViewById(R.id.tv_view);
        tv_edit = (TextView) popupWindow.findViewById(R.id.tv_edit);
        tv_delete = (TextView) popupWindow.findViewById(R.id.tv_delete);
        tv_share = (TextView) popupWindow.findViewById(R.id.tv_share);
        tv_move = (TextView) popupWindow.findViewById(R.id.tv_move);

        mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);

        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popDocumentListener != null) {
                    popDocumentListener.PopBack();
                }
            }
        });

        tv_view.setOnClickListener(new myOnClick());
        tv_edit.setOnClickListener(new myOnClick());
        tv_delete.setOnClickListener(new myOnClick());
        tv_move.setOnClickListener(new myOnClick());
        tv_share.setOnClickListener(new myOnClick());


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
                case R.id.tv_view:
                    popDocumentListener.PopView();
                    mPopupWindow.dismiss();
                    break;
                case R.id.tv_edit:
                    popDocumentListener.PopEdit();
                    mPopupWindow.dismiss();
                    break;
                case R.id.tv_delete:
                    popDocumentListener.PopDelete();
                    mPopupWindow.dismiss();
                    break;
                case R.id.tv_share:
                    popDocumentListener.PopShare();
                    mPopupWindow.dismiss();
                    break;
                case R.id.tv_move:
                    popDocumentListener.PopMove();
                    mPopupWindow.dismiss();
                    break;

                default:
                    break;
            }

        }

    }

    public void StartPop(View v) {
        int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, popupWindow , 100);
        mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }


}
