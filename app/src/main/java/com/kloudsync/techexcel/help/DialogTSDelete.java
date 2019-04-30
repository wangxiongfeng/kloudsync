package com.kloudsync.techexcel.help;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.DeleteSpaceAdapter;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Space;

import java.util.ArrayList;

public class DialogTSDelete {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private RecyclerView rv_sp;
    private Context mContext;
    private TextView tv_cancel;

    private ArrayList<Customer> cuslist = new ArrayList<Customer>();
    private ArrayList<Space> slist = new ArrayList<Space>();
    private DeleteSpaceAdapter dAdapter;

    int type;//0:team 1:space
    int spaceid;

    private int width;

    private static DialogDismissListener dialogdismissListener;

    public interface DialogDismissListener {
        void PopSelect(Space sp, int type);
    }

    public void setPoPDismissListener(
            DialogDismissListener dialogdismissListener) {
        DialogTSDelete.dialogdismissListener = dialogdismissListener;
    }

    public void SetType(int type, int spaceid) {
        this.type = type;
        this.spaceid = spaceid;
    }

    public void EditCancel(Context context, ArrayList<Customer> cuslist) {
        this.mContext = context;
        this.cuslist = cuslist;

        width = context.getResources().getDisplayMetrics().widthPixels;

        dlgGetWindow = new AlertDialog.Builder(context).create();
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation3);
        window.setContentView(R.layout.dialog_tsd);


        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
//        layoutParams.width = width / 2;
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        ShowTSInfo();
    }

    private void ShowTSInfo() {
        rv_sp = (RecyclerView) window.findViewById(R.id.rv_sp);
        tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);
        slist = new ArrayList<Space>();
        if (0 == type) {
            for (int i = 0; i < cuslist.size(); i++) {
                Space sp = cuslist.get(i).getSpace();
                slist.add(sp);
            }
        } else if (1 == type) {
            for (int i = 0; i < cuslist.size(); i++) {
                Customer cus = cuslist.get(i);
                if (cus.getSpace().getItemID() == spaceid) {
                    slist = cus.getSpaceList();
                }
            }
        }

        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rv_sp.setLayoutManager(manager);
        dAdapter = new DeleteSpaceAdapter(slist);
        dAdapter.setOnItemClickListener(new DeleteSpaceAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Space sp = slist.get(position);
                dialogdismissListener.PopSelect(sp, type);
                dlgGetWindow.dismiss();
            }
        });
        rv_sp.setAdapter(dAdapter);

        tv_cancel.setOnClickListener(new MyOnClick());
    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_cancel:
                    dlgGetWindow.dismiss();
                    break;
                default:
                    break;
            }
        }
    }


}
