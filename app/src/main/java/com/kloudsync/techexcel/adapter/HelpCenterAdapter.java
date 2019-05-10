package com.kloudsync.techexcel.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.ub.techexcel.bean.LineItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingfan on 2017/12/11.
 */

public class HelpCenterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements  View.OnClickListener {


    private List<LineItem> mlist = new ArrayList<>();

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

    public HelpCenterAdapter(List<LineItem> mlist) {
        this.mlist = mlist;
    }

    public void UpdateRV(List<LineItem> mlist) {
        this.mlist = mlist;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hc_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final LineItem item = mlist.get(position);
        holder.tv_title.setText(item.getFileName());
        String url = item.getUrl();
        /*if (url.contains("<") && url.contains(">")) {
            url = url.substring(0, url.lastIndexOf("<")) + "1_thumbnail" + url.substring(url.lastIndexOf("."), url.length());
        } else {
            url = url.substring(0, url.lastIndexOf(".")) + "_1_thumbnail" + url.substring(url.lastIndexOf("."), url.length());
        }*/
        url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
        Uri imageUri = null;
        if (!TextUtils.isEmpty(url)) {
            imageUri = Uri.parse(url);
        }
        holder.img_url.setImageURI(imageUri);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);

    }


    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView img_url;
        TextView tv_title;

        ViewHolder(View view) {
            super(view);
            img_url = (SimpleDraweeView) view.findViewById(R.id.img_url);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
        }
    }
}
