package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.TeamSpaceBean;

import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.RecycleHolder> {
    private List<TeamSpaceBean> list;
    private Context context;

    public TeamAdapter(Context context, List<TeamSpaceBean> list) {
        this.context = context;
        this.list = list;
    }

    public interface OnItemLectureListener {
        void onItem(TeamSpaceBean teamSpaceBean);
    }
    

    public void setOnItemLectureListener(OnItemLectureListener onItemLectureListener) {
        this.onItemLectureListener = onItemLectureListener;
    }

    private OnItemLectureListener onItemLectureListener;

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.team_item, parent, false);
        RecycleHolder holder = new RecycleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
        final TeamSpaceBean item = list.get(position);
        holder.documetname.setText(item.getName());
        if(item.getName().length() > 0) {
            holder.tv_sort.setText(item.getName().substring(0, 1));
        }else{
            holder.tv_sort.setText("");
        }

        holder.lin_favour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemLectureListener.onItem(item);
            }
        });
        if (item.isSelect()) {
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecycleHolder extends RecyclerView.ViewHolder {

        TextView documetname;
        TextView tv_sort;
        RelativeLayout lin_favour;
        ImageView select;

        public RecycleHolder(View itemView) {
            super(itemView);
            documetname = (TextView) itemView.findViewById(R.id.documetname);
            tv_sort = (TextView) itemView.findViewById(R.id.tv_sort);
            lin_favour = (RelativeLayout) itemView.findViewById(R.id.lin_favour);
            select = (ImageView) itemView.findViewById(R.id.selectimage);
        }

    }

}


