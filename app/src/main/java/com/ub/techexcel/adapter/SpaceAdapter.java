package com.ub.techexcel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.TeamSpaceBean;

import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class SpaceAdapter extends RecyclerView.Adapter<SpaceAdapter.RecycleHolder> {

    private List<TeamSpaceBean> list;
    private Context context;
    private boolean isSyncRoom;
    private boolean isSwitch;

    public SpaceAdapter(Context context, List<TeamSpaceBean> list, boolean isSyncRoom, boolean isSwitch) {
        this.context = context;
        this.isSwitch = isSwitch;
        this.list = list;
        this.isSyncRoom = isSyncRoom;
    }

    public interface OnItemLectureListener {
        void onItem(TeamSpaceBean teamSpaceBean);

        void select(TeamSpaceBean teamSpaceBean);
    }

    public void setOnItemLectureListener(OnItemLectureListener onItemLectureListener) {
        this.onItemLectureListener = onItemLectureListener;
    }

    private OnItemLectureListener onItemLectureListener;


    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.space_item, parent, false);
        RecycleHolder holder = new RecycleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {

        final TeamSpaceBean item = list.get(position);
        holder.documetname.setText(item.getName());
        if (item.getName().length() > 0) {
            holder.tv_sort.setText(item.getName().substring(0, 1).toUpperCase());
        } else {
            holder.tv_sort.setText("");
        }

        int ii = position % 3;
        if (ii == 1) {
            holder.tv_sort.setBackgroundResource(R.drawable.orange_cicle);
        } else if (ii == 2) {
            holder.tv_sort.setBackgroundResource(R.drawable.circle_expand);
        } else {
            holder.tv_sort.setBackgroundResource(R.drawable.blue_circle);
        }

        holder.attachmentcount.setText(item.getAttachmentCount() == 0 ? "" : item.getAttachmentCount() + " documents");
        holder.syncroomcount.setText(item.getSyncRoomCount() == 0 ? "" : item.getSyncRoomCount() + " SyncRooms");

        if (isSyncRoom) {

            holder.attachmentcount.setVisibility(View.GONE);
            if(item.getSyncRoomCount()==0){
                holder.syncroomcount.setVisibility(View.GONE);
            }else{
                holder.syncroomcount.setVisibility(View.VISIBLE);
            }
        } else {
            holder.syncroomcount.setVisibility(View.GONE);
            if(item.getAttachmentCount()==0){
                holder.attachmentcount.setVisibility(View.GONE);
            }else{
                holder.attachmentcount.setVisibility(View.VISIBLE);
            }
        }


        holder.spacerelativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSwitch) {
                    onItemLectureListener.select(item);
                } else {
                    onItemLectureListener.onItem(item);
                }
            }
        });

        if (item.isSelect()) {
            holder.selectimage.setVisibility(View.VISIBLE);
        } else {
            holder.selectimage.setVisibility(View.GONE);
        }

        if (!isSwitch) {
            holder.selectimage.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecycleHolder extends RecyclerView.ViewHolder {

        TextView documetname, tv_sort, attachmentcount, syncroomcount;

        RelativeLayout spacerelativelayout, countrl;
        ImageView selectimage;

        public RecycleHolder(View itemView) {
            super(itemView);
            documetname = (TextView) itemView.findViewById(R.id.name);
            tv_sort = (TextView) itemView.findViewById(R.id.tv_sort);
            attachmentcount = (TextView) itemView.findViewById(R.id.attachmentcount);
            syncroomcount = (TextView) itemView.findViewById(R.id.syncroomcount);
            selectimage = (ImageView) itemView.findViewById(R.id.selectimage);
            spacerelativelayout = (RelativeLayout) itemView.findViewById(R.id.spacerelativelayout);
            countrl = (RelativeLayout) itemView.findViewById(R.id.countrl);
        }

    }

}


