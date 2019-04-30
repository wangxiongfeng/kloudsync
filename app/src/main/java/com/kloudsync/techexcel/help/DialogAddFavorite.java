package com.kloudsync.techexcel.help;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.Favourite2Adapter;
import com.kloudsync.techexcel.info.Favorite;
import com.kloudsync.techexcel.start.LoginGet;

import java.util.ArrayList;

public class DialogAddFavorite {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private RecyclerView rv_pc;
    public Context mContext;
    private ArrayList<Favorite> mlist = new ArrayList<Favorite>();
    private Favourite2Adapter fAdapter;

    private static DialogDismissListener dialogdismissListener;

    public interface DialogDismissListener {
        void DialogDismiss(Favorite fav);
    }

    public void setPoPDismissListener(
            DialogDismissListener dialogdismissListener) {
        DialogAddFavorite.dialogdismissListener = dialogdismissListener;
    }

    public void EditCancel(Context context) {
        this.mContext = context;

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        dlgGetWindow = new AlertDialog.Builder(context).create();
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        window.setContentView(R.layout.sendfile);

        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
        layoutParams.width = width * 5 / 7;
//        layoutParams.height = height * 2 / 3;
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        rv_pc = (RecyclerView) window.findViewById(R.id.rv_pc);

        GetData();

    }

    private void GetData() {

        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rv_pc.setLayoutManager(manager);
        fAdapter = new Favourite2Adapter(mlist);
        fAdapter.setOnItemClickListener(new Favourite2Adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Favorite fav = mlist.get(position);
                dialogdismissListener.DialogDismiss(fav);
                dlgGetWindow.dismiss();
            }
        });
        rv_pc.setAdapter(fAdapter);

        LoginGet lg = new LoginGet();
        lg.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
            @Override
            public void getFavorite(ArrayList<Favorite> list) {
                mlist = new ArrayList<Favorite>();
                mlist.addAll(list);
                fAdapter.UpdateRV(mlist);
            }
        });
        lg.MyFavoriteRequest(mContext,1);
    }



}
