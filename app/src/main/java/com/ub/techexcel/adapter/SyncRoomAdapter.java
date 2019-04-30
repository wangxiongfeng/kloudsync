package com.ub.techexcel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DialogDeleteDocument;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Space;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.ub.kloudsync.activity.DocumentEditYinXiangPopup;
import com.ub.kloudsync.activity.DocumentYinXiangPopup;
import com.ub.kloudsync.activity.SpaceDeletePopup;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceBeanFile;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.bean.SyncRoomBean;
import com.ub.techexcel.tools.SyncRoomOperatePopup;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SyncRoomAdapter extends RecyclerView.Adapter<SyncRoomAdapter.RecycleHolder> {

    private Context context;

    private List<SyncRoomBean> list;

    public SyncRoomAdapter(Context context, List<SyncRoomBean> list) {
        this.context = context;
        this.list = list;
    }

    public interface OnItemLectureListener {
        void view(SyncRoomBean syncRoomBean);

        void deleteSuccess();

        void switchSuccess();

        void dismiss();

        void open();


    }

    public void setOnItemLectureListener(SyncRoomAdapter.OnItemLectureListener onItemLectureListener) {
        this.onItemLectureListener = onItemLectureListener;
    }

    private SyncRoomAdapter.OnItemLectureListener onItemLectureListener;


    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.syncroom_item, parent, false);
        RecycleHolder holder = new RecycleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecycleHolder holder, int position) {
        final SyncRoomBean syncRoomBean = list.get(position);
        holder.title.setText(syncRoomBean.getName());
        int ii = position % 3;
        if (ii == 1) {
            holder.tv1.setBackgroundColor(Color.parseColor("#34AA44"));
        } else if (ii == 2) {
            holder.tv1.setBackgroundColor(Color.parseColor("#1665D8"));
        } else {
            holder.tv1.setBackgroundColor(Color.parseColor("#F6AB2F"));
        }
        if (!TextUtils.isEmpty(syncRoomBean.getName())) {
            holder.tv1.setText(syncRoomBean.getName().substring(0, 1));
        }

        holder.membercount.setText(syncRoomBean.getMemberCount()+"");
        holder.documentcount.setText(syncRoomBean.getDocumentCount()+"");
        holder.meetingcount.setText(syncRoomBean.getMeetingCount()+"");

        holder.kk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncRoomOperatePopup syncRoomOperatePopup = new SyncRoomOperatePopup();
                syncRoomOperatePopup.getPopwindow(context);
                syncRoomOperatePopup.setFavoritePoPListener(new SyncRoomOperatePopup.FavoritePoPListener() {

                    @Override
                    public void view() {
                        onItemLectureListener.view(syncRoomBean);
                    }

                    @Override
                    public void move() {
                        MoveDocument(syncRoomBean, holder.kk);
                    }

                    @Override
                    public void delete() {
                        dialogDelete(syncRoomBean);
                    }
                });
                syncRoomOperatePopup.StartPop(holder.kk);

            }
        });
    }


    private void dialogDelete(final SyncRoomBean syncRoomBean) {
        DialogDeleteDocument ddd = new DialogDeleteDocument();
        ddd.setPoPDismissListener(new DialogDeleteDocument.DialogDismissListener() {
            @Override
            public void PopDelete(boolean isdelete) {
                if (isdelete) {
                    deleteLesson(syncRoomBean);
                }
            }
        });
        ddd.EditCancel(context);
        ddd.change();

    }

    private void deleteLesson(final SyncRoomBean syncRoomBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "Topic/DeleteTopic?topicID=" +
                                    syncRoomBean.getItemID());
                    Log.e("RemoveDocument", responsedata.toString());
                    int retcode = (Integer) responsedata.get("RetCode");
                    msg = new Message();
                    if (0 == retcode) {
                        msg.what = AppConfig.DELETESUCCESS;
                        String result = responsedata.toString();
                        msg.obj = result;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("errorMessage");
                        msg.obj = ErrorMessage;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(context)) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(context,
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.DELETESUCCESS:
                    onItemLectureListener.deleteSuccess();
                    break;
                default:
                    break;
            }
        }
    };

    private ArrayList<Customer> cuslist = new ArrayList<Customer>();

    private void MoveDocument(final SyncRoomBean syncRoomBean, final LinearLayout kk) {

        LoginGet loginget = new LoginGet();
        loginget.setTeamSpaceGetListener(new LoginGet.TeamSpaceGetListener() {
            @Override
            public void getTS(ArrayList<Customer> list) {
                cuslist = new ArrayList<Customer>();
                cuslist.addAll(list);
                for (int i = 0; i < cuslist.size(); i++) {
                    Customer customer = cuslist.get(i);
                    ArrayList<Space> sl = customer.getSpaceList();
                    for (int j = 0; j < sl.size(); j++) {
                        Space sp = sl.get(j);
                        if (sp.getItemID() == syncRoomBean.getParentID()) {
                            sl.remove(j);
                            break;
                        }
                    }
                }
                final SpaceDeletePopup spaceDeletePopup = new SpaceDeletePopup();
                spaceDeletePopup.getPopwindow(context);
                spaceDeletePopup.setSP(cuslist, syncRoomBean.getParentID());
                spaceDeletePopup.ChangeMove(new TeamSpaceBeanFile());
                spaceDeletePopup.ChangeMove2();
                spaceDeletePopup.setFavoritePoPListener(new SpaceDeletePopup.FavoritePoPListener() {
                    @Override
                    public void dismiss() {
                        onItemLectureListener.dismiss();
                    }

                    @Override
                    public void open() {
                        onItemLectureListener.open();
                    }

                    @Override
                    public void delete(int spaceid) {

                        TeamSpaceInterfaceTools.getinstance().switchSpace(AppConfig.URL_PUBLIC + "SyncRoom/SwitchSpace?syncRoomID=" + syncRoomBean.getItemID() + "&spaceID=" + spaceid,
                                TeamSpaceInterfaceTools.SWITCHSPACE, new TeamSpaceInterfaceListener() {
                                    @Override
                                    public void getServiceReturnData(Object object) {
                                        onItemLectureListener.switchSuccess();
                                    }
                                });
                    }

                    @Override
                    public void refresh() {

                    }
                });
                spaceDeletePopup.StartPop(kk);

            }
        });
        loginget.GetTeamSpace(context);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class RecycleHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView tv1,membercount,documentcount,meetingcount;
        LinearLayout kk;

        public RecycleHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            tv1 = (TextView) itemView.findViewById(R.id.tv1);
            membercount = (TextView) itemView.findViewById(R.id.membercount);
            documentcount = (TextView) itemView.findViewById(R.id.documentcount);
            meetingcount = (TextView) itemView.findViewById(R.id.meetingcount);
            kk = (LinearLayout) itemView.findViewById(R.id.kk);
        }
    }
}
