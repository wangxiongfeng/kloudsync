package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.InviteNewMorePopup;
import com.ub.kloudsync.activity.TeamUser;
import com.ub.techexcel.tools.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingfan on 2019/1/30.
 */

public class SpacePropertyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<TeamUser> mlist = new ArrayList<>();
    private Context context;

    public SpacePropertyAdapter(Context context, List<TeamUser> mlist) {
        this.mlist = mlist;
        this.context = context;
    }

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void sendMeaage(TeamUser user);

        void setAdmin(TeamUser user);

        void removeTeam(TeamUser user);

    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    public void UpdateRV(List<TeamUser> mlist) {
        this.mlist = mlist;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suibian, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final TeamUser teamUser = mlist.get(position);
        holder.tv_name.setText(teamUser.getMemberName());

        String url2 = teamUser.getMemberAvatar();
        Uri imageUri2;
        if (!TextUtils.isEmpty(url2)) {
            imageUri2 = Uri.parse(url2);
        } else {
            imageUri2 = Tools.getUriFromDrawableRes(context, R.drawable.hello);
        }
        holder.simpledraweeview.setImageURI(imageUri2);

        holder.moreOpation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InviteNewMorePopup teamMorePopup = new InviteNewMorePopup();
                teamMorePopup.getPopwindow(context);
                teamMorePopup.setFavoritePoPListener(new InviteNewMorePopup.FavoritePoPListener() {
                    @Override
                    public void dismiss() {
                        mFavoritePoPListener.dismiss();
                    }

                    @Override
                    public void open() {
                        mFavoritePoPListener.open();
                    }

                    @Override
                    public void sendMeaage() {
                        mFavoritePoPListener.sendMeaage(teamUser);
                    }

                    @Override
                    public void setAdmin() {
                        mFavoritePoPListener.setAdmin(teamUser);
                    }

                    @Override
                    public void removeTeam() {
                        mFavoritePoPListener.removeTeam(teamUser);

                    }
                });
                teamMorePopup.StartPop(holder.moreOpation);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        SimpleDraweeView simpledraweeview;
        ImageView moreOpation;

        ViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            moreOpation = (ImageView) view.findViewById(R.id.moreOpation);
            simpledraweeview = (SimpleDraweeView) view.findViewById(R.id.simpledraweeview);
        }
    }
}
