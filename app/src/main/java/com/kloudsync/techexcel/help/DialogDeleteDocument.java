package com.kloudsync.techexcel.help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class DialogDeleteDocument {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private Button btn_cancel;
    private Button btn_ok;
    private Context mContext;
    private TextView title,content;

    private boolean flag;

    private static DialogDismissListener dialogdismissListener;

    public interface DialogDismissListener {
        void PopDelete(boolean isdelete);
    }

    public void setPoPDismissListener(
            DialogDismissListener dialogdismissListener) {
        DialogDeleteDocument.dialogdismissListener = dialogdismissListener;
    }

    public void change(){
        title.setText("Delete SyncRoom");
        content.setText("Are you sure you want to delete the selected syncroom?");

    }

    public void EditCancel(Context context) {
        this.mContext = context;


        dlgGetWindow = new AlertDialog.Builder(context).create();
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation3);
        window.setContentView(R.layout.dialog_delete_document);



        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        dlgGetWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(dialogdismissListener != null){
                    dialogdismissListener.PopDelete(flag);
                }
            }
        });

        ShowSchoolInfo();
    }

    private void ShowSchoolInfo() {
        btn_ok = (Button) window.findViewById(R.id.btn_ok);
        btn_cancel = (Button) window.findViewById(R.id.btn_cancel);

        title= (TextView) window.findViewById(R.id.title);
        content= (TextView) window.findViewById(R.id.content);


        btn_cancel.setOnClickListener(new myOnClick());
        btn_ok.setOnClickListener(new myOnClick());
    }

    private class myOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    DissmissPop(false);
                    break;
                case R.id.btn_ok:
                    DissmissPop(true);
                    break;

                default:
                    break;
            }

        }


    }

    private void DissmissPop(boolean isupdate) {
        flag = isupdate;
        dlgGetWindow.dismiss();
    }


}
