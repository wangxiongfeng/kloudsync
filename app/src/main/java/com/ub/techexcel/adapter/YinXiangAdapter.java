package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.Tools;
import com.kloudsync.techexcel.R;

import java.util.List;

/**
 * Created by pingfan on 2017/12/11.
 */

public class YinXiangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SoundtrackBean> mlist;
    private Context mContext;
    private static FavoritePoPListener mFavoritePoPListener;
    private Uri defaultImageUri;

    public YinXiangAdapter(Context context, List<SoundtrackBean> mlist) {
        this.mContext = context;
        this.mlist = mlist;
        defaultImageUri = Tools.getUriFromDrawableRes(context, R.drawable.hello);
    }


    public interface FavoritePoPListener {

        void editYinxiang(SoundtrackBean soundtrackBean);

        void deleteYinxiang(SoundtrackBean soundtrackBean);

        void playYinxiang(SoundtrackBean soundtrackBean);

        void shareYinxiang(SoundtrackBean soundtrackBean);

        void copyUrl(SoundtrackBean soundtrackBean);

        void shareInApp(SoundtrackBean soundtrackBean);
    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yinxiang_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        final SoundtrackBean soundtrackBean = mlist.get(position);
        holder.title.setText(soundtrackBean.getTitle());
        holder.username.setText(soundtrackBean.getUserName());
        holder.duration.setText(soundtrackBean.getDuration());
        if (soundtrackBean.isHidden()) {
            holder.yinxiangedit.setVisibility(View.GONE);
        } else {
            holder.yinxiangedit.setVisibility(View.VISIBLE);
        }
        holder.ll.setVisibility(View.GONE);
        holder.shareInApp.setVisibility(View.VISIBLE);
        holder.copyUrl.setVisibility(View.VISIBLE);

        holder.operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.ll.getVisibility() == View.GONE) {
                    holder.ll.setVisibility(View.VISIBLE);
                } else {
                    holder.ll.setVisibility(View.GONE);
                }
            }
        });
        holder.yinxiangedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ll.setVisibility(View.GONE);
                mFavoritePoPListener.editYinxiang(soundtrackBean);
            }
        });
        holder.yinxiangdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ll.setVisibility(View.GONE);
                mFavoritePoPListener.deleteYinxiang(soundtrackBean);
            }
        });
        holder.yinxiangplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ll.setVisibility(View.GONE);
                mFavoritePoPListener.playYinxiang(soundtrackBean);
            }
        });
        holder.yinxiangshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ll.setVisibility(View.GONE);
                mFavoritePoPListener.shareYinxiang(soundtrackBean);
            }
        });
        holder.copyUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ll.setVisibility(View.GONE);
                mFavoritePoPListener.copyUrl(soundtrackBean);
            }
        });
        holder.shareInApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ll.setVisibility(View.GONE);
                mFavoritePoPListener.shareInApp(soundtrackBean);
            }
        });

        String url2 = soundtrackBean.getAvatarUrl();
        Uri imageUri2;
        if (!TextUtils.isEmpty(url2)) {
            imageUri2 = Uri.parse(url2);
        } else {
            imageUri2 = defaultImageUri;
        }
        holder.image.setImageURI(imageUri2);

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RelativeLayout operation;
        TextView username;
        TextView duration;
        LinearLayout ll;
        LinearLayout yinxiangedit;
        LinearLayout yinxiangdelete;
        LinearLayout yinxiangplay;
        LinearLayout yinxiangshare;
        LinearLayout copyUrl;
        LinearLayout shareInApp;
        SimpleDraweeView image;

        ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            username = (TextView) view.findViewById(R.id.username);
            duration = (TextView) view.findViewById(R.id.duration);
            operation = (RelativeLayout) view.findViewById(R.id.operation);

            ll = (LinearLayout) view.findViewById(R.id.ll);
            yinxiangedit = (LinearLayout) view.findViewById(R.id.yinxiangedit);
            yinxiangdelete = (LinearLayout) view.findViewById(R.id.yinxiangdelete);
            yinxiangplay = (LinearLayout) view.findViewById(R.id.yinxiangplay);
            yinxiangshare = (LinearLayout) view.findViewById(R.id.yinxiangshare);
            copyUrl = (LinearLayout) view.findViewById(R.id.copyurl);
            shareInApp = (LinearLayout) view.findViewById(R.id.shareinapp);
            image = (SimpleDraweeView) view.findViewById(R.id.image);
        }
    }
}
