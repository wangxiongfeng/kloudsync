package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Favorite;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.SyncRoomBean;

import java.util.ArrayList;
import java.util.List;


public class SyncRoomDocumentPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView adddocument;
    private RecyclerView recycleview;
    private SyncRoomTeamAdapter syncRoomTeamAdapter;

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

    LinearLayout upload_linearlayout;
    LinearLayout morell;

    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.syncroom_document_popup, null);

        recycleview = (RecyclerView) view.findViewById(R.id.recycleview);
        adddocument = (ImageView) view.findViewById(R.id.adddocument);
        adddocument.setOnClickListener(this);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        upload_linearlayout = (LinearLayout) view.findViewById(R.id.upload_linearlayout);
        morell = (LinearLayout) view.findViewById(R.id.morell);

        RelativeLayout fromTeamDocument = (RelativeLayout) view.findViewById(R.id.fromteamdocument);
        RelativeLayout take_photo = (RelativeLayout) view.findViewById(R.id.take_photo);
        RelativeLayout file_library = (RelativeLayout) view.findViewById(R.id.file_library);
        RelativeLayout save_file = (RelativeLayout) view.findViewById(R.id.save_file);

        fromTeamDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.teamDocument();
            }
        });
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.takePhoto();
            }
        });
        file_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.importFromLibrary();
            }
        });
        save_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.savedFile();
            }
        });


        RelativeLayout moreshare = (RelativeLayout) view.findViewById(R.id.moreshare);
        RelativeLayout moreedit = (RelativeLayout) view.findViewById(R.id.moreedit);
        RelativeLayout moredelete = (RelativeLayout) view.findViewById(R.id.moredelete);
        moreshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morell.setVisibility(View.GONE);

            }
        });
        moreedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morell.setVisibility(View.GONE);
                webCamPopupListener.edit(selectLineItem);

            }
        });
        moredelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morell.setVisibility(View.GONE);
                webCamPopupListener.delete(selectLineItem);

            }
        });

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                webCamPopupListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.update();
        mPopupWindow.setAnimationStyle(R.style.anination3);

    }


    @SuppressLint("NewApi")
    public void StartPop(View v, List<LineItem> list) {
        if (mPopupWindow != null) {
            webCamPopupListener.open();
            mPopupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0);
            syncRoomTeamAdapter = new SyncRoomTeamAdapter(mContext, list);
            recycleview.setAdapter(syncRoomTeamAdapter);

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


    public interface WebCamPopupListener {

        void changeOptions(LineItem syncRoomBean);

        void teamDocument();

        void takePhoto();

        void importFromLibrary();

        void savedFile();

        void dismiss();

        void open();

        void delete(LineItem selectLineItem);

        void edit(LineItem selectLineItem);


    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closebnt:
                mPopupWindow.dismiss();
                break;
            case R.id.adddocument:
                if (upload_linearlayout.getVisibility() == View.VISIBLE) {
                    upload_linearlayout.setVisibility(View.GONE);
                } else {
                    upload_linearlayout.setVisibility(View.VISIBLE);
                }
                morell.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    private LineItem selectLineItem = new LineItem();

    public class SyncRoomTeamAdapter extends RecyclerView.Adapter<SyncRoomTeamAdapter.RecycleHolder2> {

        private Context context;

        private List<LineItem> list = new ArrayList<>();

        public SyncRoomTeamAdapter(Context context, List<LineItem> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecycleHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.syncroom_document_popup_item, parent, false);
            RecycleHolder2 holder = new RecycleHolder2(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(RecycleHolder2 holder, int position) {
            final LineItem lineItem = list.get(position);
            holder.title.setText(lineItem.getFileName());

            holder.synccount.setText("Sync:" + lineItem.getSyncRoomCount());

            holder.fileicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webCamPopupListener.changeOptions(lineItem);
                }
            });
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectLineItem = lineItem;
                    if (morell.getVisibility() == View.VISIBLE) {
                        morell.setVisibility(View.GONE);
                    } else {
                        morell.setVisibility(View.VISIBLE);
                    }
                    upload_linearlayout.setVisibility(View.GONE);
                }
            });
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    morell.setVisibility(View.GONE);
                    upload_linearlayout.setVisibility(View.GONE);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class RecycleHolder2 extends RecyclerView.ViewHolder {
            TextView title;
            TextView fileicon;
            TextView synccount;
            ImageView more;
            LinearLayout ll;

            public RecycleHolder2(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
                fileicon = (TextView) itemView.findViewById(R.id.fileicon);
                synccount = (TextView) itemView.findViewById(R.id.synccount);
                more = (ImageView) itemView.findViewById(R.id.more);
                ll = (LinearLayout) itemView.findViewById(R.id.ll);
            }
        }
    }


}
