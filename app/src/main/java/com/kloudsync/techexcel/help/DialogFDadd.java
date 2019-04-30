package com.kloudsync.techexcel.help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.personal.PersanalCollectionActivity;
import com.kloudsync.techexcel.tool.Jianbuderen;

public class DialogFDadd {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private TextView tv_favour;
    private TextView tv_document;
    private TextView tv_cancel;
    private Context mContext;

    private boolean flagad;

    private int height;

    private static DialogDismissListener dialogdismissListener;

    public interface DialogDismissListener {
        void AddDocument();
    }

    public void setDismissListener(
            DialogDismissListener dialogdismissListener) {
        DialogFDadd.dialogdismissListener = dialogdismissListener;
    }

    public void EditCancel(Context context) {
        this.mContext = context;

        height = context.getResources().getDisplayMetrics().heightPixels;

        dlgGetWindow = new AlertDialog.Builder(context).create();

        dlgGetWindow.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dlgGetWindow.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation5);
        window.setContentView(R.layout.dialog_adddf);

        dlgGetWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(!flagad) {
                    Jianbuderen.Heihei();
                }
            }
        });

        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
        layoutParams.y = height - layoutParams.height - 50;
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        initView();
    }

    private void initView() {
        tv_favour = (TextView) window.findViewById(R.id.tv_favour);
        tv_document = (TextView) window.findViewById(R.id.tv_document);
        tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(new MyOnClick());
        tv_document.setOnClickListener(new MyOnClick());
        tv_favour.setOnClickListener(new MyOnClick());

    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_favour:
                    GoToFavour();
                    break;
                case R.id.tv_document:
                    flagad = true;
                    dialogdismissListener.AddDocument();
                    dlgGetWindow.dismiss();
                    break;
                case R.id.tv_cancel:
                    dlgGetWindow.dismiss();
                    break;

                default:
                    break;
            }
        }
    }

    private void GoToFavour() {

        if (PersanalCollectionActivity.instance != null && !PersanalCollectionActivity.instance.isFinishing()) {
            PersanalCollectionActivity.instance.finish();
        }
        Intent intent = new Intent(mContext, PersanalCollectionActivity.class);
        intent.putExtra("path", AppConfig.OUTSIDE_PATH);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        dlgGetWindow.dismiss();
    }


}
