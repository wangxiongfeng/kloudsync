package com.ub.techexcel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ub.techexcel.bean.ServiceBean;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;

import java.util.List;

public class NotifyAdapter extends BaseAdapter {
    private Context context;
    private List<ServiceBean> serviceList;
    public ImageLoader imageLoader;

    //isPublic 判断是否公开
    public NotifyAdapter(Context context, List<ServiceBean> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return serviceList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return serviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.service_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.lineItems = (LinearLayout) convertView
                    .findViewById(R.id.lineitems);
            holder.concern = (TextView) convertView
                    .findViewById(R.id.concerValue);
            holder.num = (TextView) convertView.findViewById(R.id.num);
            holder.confirmfinish = (TextView) convertView
                    .findViewById(R.id.confirmfinish);
            holder.modifyservice = (TextView) convertView
                    .findViewById(R.id.modifyservice);
            holder.ifclose = (LinearLayout) convertView
                    .findViewById(R.id.ifclose);
            holder.entercustomer = (LinearLayout) convertView
                    .findViewById(R.id.entercustomer);
            holder.sendsms = (LinearLayout) convertView
                    .findViewById(R.id.sendsms);
            holder.kename = (TextView) convertView
                    .findViewById(R.id.kename);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.issmslinear = (LinearLayout) convertView
                    .findViewById(R.id.issmslinear);
            holder.sendcourse = (TextView) convertView.findViewById(R.id.sendcourse);
            holder.smsicon = (ImageView) convertView.findViewById(R.id.smsicon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.sendsms.setVisibility(View.GONE);
        holder.modifyservice.setText(context.getResources().getString(R.string.clear));
        holder.confirmfinish.setText(context.getResources().getString(R.string.join));
        holder.confirmfinish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onModifyServiceListener.join(position);
            }
        });
        holder.modifyservice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onModifyServiceListener.leave(position);
            }
        });

        ServiceBean bean = serviceList.get(position);

        holder.name.setText(bean.getCustomer().getName());
        holder.kename.setText(bean.getName());
        holder.concern.setText(bean.getConcernName());
        String url = bean.getCustomer().getUrl();
        if (null == url || url.length() < 1) {
            holder.image.setImageResource(R.drawable.hello);
        } else {
            imageLoader.DisplayImage(url, holder.image);
        }
        holder.lineItems.setVisibility(View.GONE);
        holder.num.setVisibility(View.GONE);
        return convertView;

    }

    private OnModifyCourseListener onModifyServiceListener;

    public void setOnModifyCourseListener(
            OnModifyCourseListener onModifyServiceListener) {
        this.onModifyServiceListener = onModifyServiceListener;
    }

    public interface OnModifyCourseListener {

        void join(int position);

        void leave(int position);

    }


}

