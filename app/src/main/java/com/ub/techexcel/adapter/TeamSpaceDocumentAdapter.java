package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.kloudsync.activity.DocumentEditYinXiangPopup;
import com.ub.kloudsync.activity.DocumentYinXiangPopup;
import com.ub.kloudsync.activity.TeamSpaceBeanFile;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.CalListviewHeight;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class TeamSpaceDocumentAdapter extends RecyclerView.Adapter<TeamSpaceDocumentAdapter.RecycleHolder> {

    private List<TeamSpaceBeanFile> list;
    private Context context;

    public TeamSpaceDocumentAdapter(Context context, List<TeamSpaceBeanFile> list) {
        this.context = context;
        this.list = list;
    }

    public interface OnItemLectureListener {
        void onItem(TeamSpaceBeanFile lesson, View view);

        void share(int s, TeamSpaceBeanFile teamSpaceBeanFile);

        void open();

        void dismiss();

        void deleteRefresh();
    }

    public void setOnItemLectureListener(OnItemLectureListener onItemLectureListener) {
        this.onItemLectureListener = onItemLectureListener;
    }

    private OnItemLectureListener onItemLectureListener;

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.currentteam_item, parent, false);
        RecycleHolder holder = new RecycleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecycleHolder holder, final int position) {
        final TeamSpaceBeanFile item = list.get(position);
        holder.documetname.setText(item.getTitle());
        holder.lin_favour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemLectureListener != null) {
                    onItemLectureListener.onItem(item, holder.tv_num);
                }
            }
        });
        String createData = new SimpleDateFormat("yyyy_MM_dd hh:mm:ss").format(Long.parseLong(item.getCreatedDate()));
        holder.createdata.setText("Create Data: " + createData);
        holder.listView.setVisibility(View.GONE);
        int syncCount = item.getSyncCount();
        holder.tv_num_value.setText(item.getSyncCount() + "");
        if (syncCount == 0) {
            holder.syncll.setVisibility(View.GONE);
        } else {
            holder.syncll.setVisibility(View.VISIBLE);
        }
        holder.syncll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.listView.getVisibility() == View.VISIBLE) {
                    holder.listView.setVisibility(View.GONE);
                } else {
                    holder.listView.setVisibility(View.VISIBLE);
                    getSoundtrack(item, holder.listView);
                }
            }
        });

    }


    public void getSoundtrack(final TeamSpaceBeanFile teamSpaceBeanFile, final ListView listView) {
        String attachmentid = teamSpaceBeanFile.getAttachmentID() + "";
        if (TextUtils.isEmpty(attachmentid)) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "Soundtrack/List?attachmentID=" + attachmentid;
        ServiceInterfaceTools.getinstance().getSoundList(url, ServiceInterfaceTools.GETSOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SoundtrackBean> ll = (List<SoundtrackBean>) object;
                        myBaseAdapter = new MyBaseAdapter(ll, teamSpaceBeanFile);
                        listView.setAdapter(myBaseAdapter);
                        CalListviewHeight.setListViewHeightBasedOnChildren(listView);
                    }
                }, false, true);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    private MyBaseAdapter myBaseAdapter;

    class RecycleHolder extends RecyclerView.ViewHolder {

        TextView documetname, createdata;
        ImageView tv_num;
        TextView tv_num_value;
        LinearLayout syncll;
        RelativeLayout lin_favour;
        ListView listView;

        public RecycleHolder(View itemView) {
            super(itemView);
            documetname = (TextView) itemView.findViewById(R.id.documetname);
            tv_num_value = (TextView) itemView.findViewById(R.id.tv_num_value);
            createdata = (TextView) itemView.findViewById(R.id.createdata);
            tv_num = (ImageView) itemView.findViewById(R.id.tv_num);
            listView = (ListView) itemView.findViewById(R.id.listview);
            lin_favour = (RelativeLayout) itemView.findViewById(R.id.lin_favour);
            syncll = (LinearLayout) itemView.findViewById(R.id.syncll);
        }
    }


    class MyBaseAdapter extends BaseAdapter {

        List<SoundtrackBean> list = new ArrayList<>();
        TeamSpaceBeanFile teamSpaceBeanFile;

        public MyBaseAdapter(List<SoundtrackBean> list, TeamSpaceBeanFile teamSpaceBeanFile) {
            this.list = list;
            this.teamSpaceBeanFile = teamSpaceBeanFile;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.yinxiang_item, null);
                viewHolder.title = (TextView) view.findViewById(R.id.title);
                viewHolder.username = (TextView) view.findViewById(R.id.username);
                viewHolder.duration = (TextView) view.findViewById(R.id.duration);
                viewHolder.operation = (RelativeLayout) view.findViewById(R.id.operation);
                viewHolder.image = (SimpleDraweeView) view.findViewById(R.id.image);
                viewHolder.ll = (LinearLayout) view.findViewById(R.id.ll);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.ll.setVisibility(View.GONE);
            final SoundtrackBean soundtrackBean = list.get(position);
            viewHolder.title.setText(soundtrackBean.getTitle());
            viewHolder.username.setText(soundtrackBean.getUserName());
            viewHolder.duration.setText(soundtrackBean.getDuration());
            viewHolder.operation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DocumentYinXiangPopup documentYinXiangPopup = new DocumentYinXiangPopup();
                    documentYinXiangPopup.getPopwindow(context);
                    documentYinXiangPopup.setFavoritePoPListener(new DocumentYinXiangPopup.FavoritePoPListener() {
                        @Override
                        public void share() {
                            onItemLectureListener.share(soundtrackBean.getSoundtrackID(), teamSpaceBeanFile);
                        }

                        @Override
                        public void edit() {
                            DocumentEditYinXiangPopup documentEditYinXiangPopup = new DocumentEditYinXiangPopup();
                            documentEditYinXiangPopup.getPopwindow(context);
                            documentEditYinXiangPopup.setFavoritePoPListener(new DocumentEditYinXiangPopup.FavoritePoPListener() {

                                @Override
                                public void dismiss() {
                                    onItemLectureListener.dismiss();
                                }

                                @Override
                                public void editSuccess() {
                                    onItemLectureListener.deleteRefresh();
                                }

                                @Override
                                public void open() {
                                    onItemLectureListener.open();
                                }
                            });
                            documentEditYinXiangPopup.StartPop(viewHolder.operation, soundtrackBean);
                        }

                        @Override
                        public void delete() {
                            deleteYinxiang2(soundtrackBean);
                        }
                    });
                    documentYinXiangPopup.StartPop(viewHolder.title);
                }
            });
            String url2 = soundtrackBean.getAvatarUrl();
            Uri imageUri2;
            if (!TextUtils.isEmpty(url2)) {
                imageUri2 = Uri.parse(url2);
            } else {
                imageUri2 = Tools.getUriFromDrawableRes(context, R.drawable.hello);
            }
            viewHolder.image.setImageURI(imageUri2);
            return view;
        }

        class ViewHolder {
            TextView title;
            RelativeLayout operation;
            TextView username;
            TextView duration;
            SimpleDraweeView image;
            LinearLayout ll;
        }

    }


    private void deleteYinxiang2(SoundtrackBean soundtrackBean) {
        final int soundtrackID = soundtrackBean.getSoundtrackID();
        String url = AppConfig.URL_PUBLIC + "Soundtrack/Delete?soundtrackID=" + soundtrackID;
        ServiceInterfaceTools.getinstance().deleteSound(url, ServiceInterfaceTools.DELETESOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        onItemLectureListener.deleteRefresh();
                    }
                });
    }


}


