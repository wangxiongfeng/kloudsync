package com.kloudsync.techexcel.help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.tool.Jianbuderen;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.adapter.SpaceAdapter;

import java.util.ArrayList;
import java.util.List;

public class DialogSDadd {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private RecyclerView rv_sp;
    private TextView tv_cancel;
    private Context mContext;

    private List<TeamSpaceBean> spacesList = new ArrayList<>();
    private SpaceAdapter spaceAdapter;

    private boolean flagad;

    private int width;

    private static DialogDismissListener dialogdismissListener;

    public interface DialogDismissListener {
        void PopSelect(TeamSpaceBean tsb);
    }

    public void setPoPDismissListener(
            DialogDismissListener dialogdismissListener) {
        DialogSDadd.dialogdismissListener = dialogdismissListener;
    }

    public void EditCancel(Context context, List<TeamSpaceBean> spacesList) {
        this.mContext = context;
        this.spacesList = spacesList;

        width = context.getResources().getDisplayMetrics().widthPixels;

        dlgGetWindow = new AlertDialog.Builder(context).create();
        dlgGetWindow.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dlgGetWindow.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation3);
        window.setContentView(R.layout.dialog_tsd);

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
//        layoutParams.width = width * 2 / 3;
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        ShowTSInfo();
    }

    private void ShowTSInfo() {
        rv_sp = (RecyclerView) window.findViewById(R.id.rv_sp);
        tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(new MyOnClick());

        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rv_sp.setLayoutManager(manager);
        spaceAdapter = new SpaceAdapter(mContext, spacesList,false);

        spaceAdapter.setOnItemLectureListener(new SpaceAdapter.OnItemLectureListener() {
            @Override
            public void onItem(TeamSpaceBean teamSpaceBean) {
                dialogdismissListener.PopSelect(teamSpaceBean);
                flagad = true;
                dlgGetWindow.dismiss();
            }
        });
        rv_sp.setAdapter(spaceAdapter);


    }

    protected class MyOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_cancel:
                    dlgGetWindow.dismiss();
                    break;

            }
        }
    }


}
