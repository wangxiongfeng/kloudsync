package com.ub.techexcel.adapter;

import java.util.List;

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

public class ServiceAdapter extends BaseAdapter {
    private Context context;
    private List<ServiceBean> serviceList;
    public ImageLoader imageLoader;
    private boolean isPublic;

    //isPublic 判断是否公开
    public ServiceAdapter(Context context, List<ServiceBean> serviceList, boolean isPublic) {
        this.context = context;
        this.serviceList = serviceList;
        imageLoader = new ImageLoader(context.getApplicationContext());
        this.isPublic = isPublic;
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
        ServiceBean bean = serviceList.get(position);
        holder.confirmfinish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onModifyServiceListener.onBeginStudy(position);
            }
        });

        holder.modifyservice.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onModifyServiceListener.viewCourse(position);
            }
        });

        holder.entercustomer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onModifyServiceListener.enterCustomerDetail(position);
            }
        });
        holder.sendsms.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onModifyServiceListener.sendSMS(position);
            }
        });

        if (isPublic) { //  公开课程
            if (bean.getStatusID() == 1) { // 已結束
                holder.sendcourse.setText(context.getString(R.string.message));
                holder.smsicon.setVisibility(View.VISIBLE);
                holder.modifyservice.setVisibility(View.GONE);
                holder.confirmfinish.setVisibility(View.GONE);
            } else { // 322 进行中
                holder.sendcourse.setText(context.getString(R.string.message));
                holder.smsicon.setVisibility(View.VISIBLE);
                holder.modifyservice.setText(context.getString(R.string.viewcourse));
                holder.modifyservice.setVisibility(View.VISIBLE);
                if (bean.getRoleinlesson() == 2) {  //老师 开始上课
                    holder.confirmfinish.setText(context.getString(R.string.start));
                } else {  //学生  加入课程
                    holder.confirmfinish.setText(context.getString(R.string.join));
                }
                holder.confirmfinish.setVisibility(View.VISIBLE);
            }
        } else {
            holder.smsicon.setVisibility(View.GONE);
            holder.sendcourse.setText(context.getString(R.string.publish));
            holder.sendcourse.setVisibility(View.VISIBLE);
            holder.modifyservice.setText(context.getString(R.string.modify));
            holder.modifyservice.setVisibility(View.VISIBLE);
            holder.confirmfinish.setText(context.getString(R.string.details));
            holder.confirmfinish.setVisibility(View.VISIBLE);
        }

        holder.name.setText(bean.getCustomer().getName());
        holder.kename.setText(bean.getName());
        holder.status.setVisibility(View.GONE);
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


    private OnModifyServiceListener onModifyServiceListener;

    public void setOnModifyServiceListener(
            OnModifyServiceListener onModifyServiceListener) {
        this.onModifyServiceListener = onModifyServiceListener;
    }

    public interface OnModifyServiceListener {
        void onBeginStudy(int position);

        void viewCourse(int position);

        void enterCustomerDetail(int position);

        void sendSMS(int position);

    }


}
class ViewHolder {
    TextView name, status, concern, num, confirmfinish, modifyservice, kename;
    LinearLayout lineItems, entercustomer, sendsms, issmslinear;
    LinearLayout ifclose;
    ImageView image;
    ImageView smsicon;
    TextView sendcourse;

}


