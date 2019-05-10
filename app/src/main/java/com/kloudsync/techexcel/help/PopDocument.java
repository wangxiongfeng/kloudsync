package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
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

    private LinearLayout lin_share;
    private LinearLayout lin_move;
    private LinearLayout lin_edit;
    private LinearLayout lin_delete;
    private ImageView img_close;
    private TextView tv_name;
    private View popupWindow;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.pop_document, null);

        lin_share = (LinearLayout) popupWindow.findViewById(R.id.lin_share);
        lin_move = (LinearLayout) popupWindow.findViewById(R.id.lin_move);
        lin_edit = (LinearLayout) popupWindow.findViewById(R.id.lin_edit);
        lin_delete = (LinearLayout) popupWindow.findViewById(R.id.lin_delete);
        img_close = (ImageView) popupWindow.findViewById(R.id.img_close);
        tv_name = (TextView) popupWindow.findViewById(R.id.tv_name);

        tv_name.setText(lesson.getTitle());

        mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.MATCH_PARENT,
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

        lin_share.setOnClickListener(new myOnClick());
        lin_move.setOnClickListener(new myOnClick());
        lin_edit.setOnClickListener(new myOnClick());
        lin_delete.setOnClickListener(new myOnClick());
        img_close.setOnClickListener(new myOnClick());


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
                /*case R.id.tv_view:
                    popDocumentListener.PopView();
                    mPopupWindow.dismiss();
                    break;*/
                case R.id.lin_edit:
                    popDocumentListener.PopEdit();
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_delete:
                    popDocumentListener.PopDelete();
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_share:
                    popDocumentListener.PopShare();
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_move:
                    popDocumentListener.PopMove();
                    mPopupWindow.dismiss();
                    break;
                case R.id.img_close:
                    mPopupWindow.dismiss();
                    break;

                default:
                    break;
            }

        }

    }

    public void StartPop(View v) {
//        int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, popupWindow , 100);
//        mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }


}
