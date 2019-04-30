package com.kloudsync.techexcel.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.PopMoreFavorite;
import com.kloudsync.techexcel.info.Favorite;
import com.kloudsync.techexcel.view.RoundProgressBar;
import com.ub.kloudsync.activity.DocumentEditYinXiangPopup;
import com.ub.kloudsync.activity.DocumentYinXiangPopup;
import com.ub.kloudsync.activity.TeamSpaceBeanFile;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingfan on 2017/12/11.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnLongClickListener, View.OnClickListener {


    private List<Favorite> mlist = new ArrayList<>();

    private DeleteItemClickListener deleteItemClickListener = null;

    public static interface DeleteItemClickListener {
        void AddTempLesson(int position);

        void deleteClick(View view, int position);

        void shareLesson(TeamSpaceBeanFile lesson, int id);
    }

    public void setDeleteItemClickListener(DeleteItemClickListener deleteItemClickListener) {
        this.deleteItemClickListener = deleteItemClickListener;
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(final View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;

    public static interface OnRecyclerViewItemLongClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemLongClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemLongClickListener.onItemClick(v, (Integer) v.getTag());
        }
        return false;
    }

    public FavouriteAdapter(List<Favorite> mlist) {
        this.mlist = mlist;
    }

    public void UpdateRV(List<Favorite> mlist) {
        this.mlist = mlist;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favour, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Favorite favorite = mlist.get(position);
        holder.tv_favour.setText(favorite.getTitle());
        holder.tv_synccount.setText(favorite.getSyncCount() + "");
        if (0 == favorite.getFlag()) {
            holder.lin_favour.setOnLongClickListener(this);
            holder.lin_favour.setOnClickListener(this);
        } else if (1 == favorite.getFlag()) {
            holder.rpb_update.setCricleProgressColor(holder.itemView.getContext().getResources().getColor(R.color.skyblue));
            holder.rpb_update.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.skyblue));
            holder.rpb_update.setProgress(favorite.getProgressbar());
            holder.tv_status.setText("Uploading...");
        } else if (2 == favorite.getFlag()) {
            holder.rpb_update.setCricleProgressColor(holder.itemView.getContext().getResources().getColor(R.color.green));
            holder.rpb_update.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
            holder.rpb_update.setProgress(favorite.getProgressbar());
            holder.tv_status.setText("Converting...");
        }
        holder.lin_pb.setVisibility((0 == favorite.getFlag()) ? View.GONE : View.VISIBLE);
        holder.lin_sync.setVisibility((0 == favorite.getSyncCount()) ? View.GONE : View.VISIBLE);
        holder.itemView.setTag(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopMoreFavorite pmf = new PopMoreFavorite();
                pmf.getPopwindow(holder.itemView.getContext(), favorite);
                pmf.setPoPMoreListener(new PopMoreFavorite.PopMoreFavoriteListener() {
                    @Override
                    public void PopView() {
                        deleteItemClickListener.AddTempLesson(position);
                    }

                    @Override
                    public void PopDelete() {
                        deleteItemClickListener.deleteClick(holder.img_more, position);
                    }

                    @Override
                    public void PopShare() {
                        TeamSpaceBeanFile lesson = new TeamSpaceBeanFile();
                        lesson.setItemID(favorite.getItemID());
                        lesson.setSourceFileUrl(favorite.getAttachmentUrl());
                        lesson.setTitle(favorite.getTitle());
                        deleteItemClickListener.shareLesson(lesson, -1);
                    }
                });
                pmf.StartPop(v);
            }
        });

        holder.lin_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite.setExpand(!favorite.isExpand());
                getSoundtrack(favorite);
            }
        });

        holder.lin_expand.removeAllViews();
        ShowSync(holder, favorite);

    }

    private void ShowSync(final ViewHolder holder, final Favorite favorite) {
        if (favorite.isExpand()) {
            for (int i = 0; i < favorite.getSlist().size(); i++) {
                final SoundtrackBean soundtrackBean = favorite.getSlist().get(i);
                RelativeLayout view = (RelativeLayout) LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.yinxiang_item, null);
                final TextView title = (TextView) view.findViewById(R.id.title);
                TextView username = (TextView) view.findViewById(R.id.username);
                TextView duration = (TextView) view.findViewById(R.id.duration);
                final RelativeLayout operation = (RelativeLayout) view.findViewById(R.id.operation);
                SimpleDraweeView image = (SimpleDraweeView) view.findViewById(R.id.image);
                LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll);
                ll.setVisibility(View.GONE);
                title.setText(soundtrackBean.getTitle());
                username.setText(soundtrackBean.getUserName());
                duration.setText(soundtrackBean.getDuration());
                operation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentYinXiangPopup documentYinXiangPopup = new DocumentYinXiangPopup();
                        documentYinXiangPopup.getPopwindow(holder.itemView.getContext());
                        documentYinXiangPopup.setFavoritePoPListener(new DocumentYinXiangPopup.FavoritePoPListener() {
                            @Override
                            public void share() {
                                TeamSpaceBeanFile lesson = new TeamSpaceBeanFile();
                                lesson.setItemID(favorite.getItemID());
                                lesson.setSourceFileUrl(favorite.getAttachmentUrl());
                                lesson.setTitle(favorite.getTitle());
                                deleteItemClickListener.shareLesson(lesson, soundtrackBean.getSoundtrackID());
                            }

                            @Override
                            public void edit() {
                                DocumentEditYinXiangPopup documentEditYinXiangPopup = new DocumentEditYinXiangPopup();
                                documentEditYinXiangPopup.getPopwindow(holder.itemView.getContext());
                                documentEditYinXiangPopup.setFavoritePoPListener(new DocumentEditYinXiangPopup.FavoritePoPListener() {

                                    @Override
                                    public void dismiss() {

                                    }

                                    @Override
                                    public void editSuccess() {
                                        getSoundtrack(favorite);
                                    }

                                    @Override
                                    public void open() {

                                    }
                                });
                                documentEditYinXiangPopup.StartPop(operation, soundtrackBean);

                            }

                            @Override
                            public void delete() {
                                deleteYinxiang2(soundtrackBean, favorite);
                            }
                        });
                        documentYinXiangPopup.StartPop(title);
                    }
                });
                String url2 = soundtrackBean.getAvatarUrl();
                Uri imageUri2;
                if (!TextUtils.isEmpty(url2)) {
                    imageUri2 = Uri.parse(url2);
                } else {
                    imageUri2 = Tools.getUriFromDrawableRes(holder.itemView.getContext(), R.drawable.hello);
                }
                image.setImageURI(imageUri2);


                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 10, 0, 10);

                view.setLayoutParams(lp);

                holder.lin_expand.addView(view);
            }

        }
    }

    private void getSoundtrack(final Favorite favorite) {
        String attachmentid = favorite.getAttachmentID() + "";
        if (TextUtils.isEmpty(attachmentid)) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "Soundtrack/List?attachmentID=" + attachmentid;
        ServiceInterfaceTools.getinstance().getSoundList(url, ServiceInterfaceTools.GETSOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SoundtrackBean> ll = (List<SoundtrackBean>) object;
                        favorite.setSlist(ll);
                        UpdateRV(mlist);
                    }
                }, false, true);
    }


    private void deleteYinxiang2(SoundtrackBean soundtrackBean, final Favorite favorite) {
        final int soundtrackID = soundtrackBean.getSoundtrackID();
        String url = AppConfig.URL_PUBLIC + "Soundtrack/Delete?soundtrackID=" + soundtrackID;
        ServiceInterfaceTools.getinstance().deleteSound(url, ServiceInterfaceTools.DELETESOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        getSoundtrack(favorite);
                    }
                });
    }

    public void SetMyProgress(long total, long current, Favorite fa) {
        int pb = (int) (current * 100 / total);
        fa.setProgressbar(pb);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_favour;
        TextView tv_status;
        TextView tv_synccount;
        RelativeLayout lin_favour;
        LinearLayout lin_pb;
        LinearLayout lin_sync;
        LinearLayout lin_expand;
        RoundProgressBar rpb_update;
        ImageView img_more;

        ViewHolder(View view) {
            super(view);
            tv_favour = (TextView) view.findViewById(R.id.tv_favour);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_synccount = (TextView) view.findViewById(R.id.tv_synccount);
            lin_favour = (RelativeLayout) view.findViewById(R.id.lin_favour);
            lin_pb = (LinearLayout) view.findViewById(R.id.lin_pb);
            lin_sync = (LinearLayout) view.findViewById(R.id.lin_sync);
            lin_expand = (LinearLayout) view.findViewById(R.id.lin_expand);
            rpb_update = (RoundProgressBar) view.findViewById(R.id.rpb_update);
            img_more = (ImageView) view.findViewById(R.id.img_more);
        }
    }
}
