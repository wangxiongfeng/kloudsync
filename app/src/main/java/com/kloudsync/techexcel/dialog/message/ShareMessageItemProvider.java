package com.kloudsync.techexcel.dialog.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.ui.MainActivity;
import com.ub.kloudsync.activity.TeamSpaceBeanFile;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = ShareMessage.class)
public class ShareMessageItemProvider extends IContainerItemProvider.MessageProvider<ShareMessage> {

    public static Context mContext;
    class ViewHolder {
        TextView tv_title;
        TextView tv_name;
        TextView tv_time;
		SimpleDraweeView sw_avatar;
		SimpleDraweeView sw_doc;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_share, null);
        mContext = context.getApplicationContext();
        ViewHolder holder = new ViewHolder();
        holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
        holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
        holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
        holder.sw_avatar = (SimpleDraweeView) view.findViewById(R.id.sw_avatar);
        holder.sw_doc = (SimpleDraweeView) view.findViewById(R.id.sw_doc);
        view.setTag(holder);
        return view;
    }

	@Override
	public void bindView(View v, int position, ShareMessage content,
			UIMessage arg3) {
		ViewHolder holder = (ViewHolder) v.getTag();
		Log.e("duang", content.getShareDocUsername() + ":" + content.getAttachmentID()
				+ ":" + content.getShareDocTitle() + ":" + content.getShareDocAvatarUrl()
				+ ":" + content.getShareDocThumbnailUrl() + ":" + content.getShareDocUrl()
				+ ":" + content.getShareDocTime());
		holder.tv_title.setText(content.getShareDocTitle());
		holder.tv_name.setText(content.getShareDocUsername());
		holder.tv_time.setText(content.getShareDocTime());
		Uri imageUri = Uri.parse(content.getShareDocAvatarUrl());
		holder.sw_avatar.setImageURI(imageUri);
		Uri imageUri2 = Uri.parse(content.getShareDocThumbnailUrl());
		holder.sw_doc.setImageURI(imageUri2);

	}

	@Override
	public Spannable getContentSummary(ShareMessage content) {
        return new SpannableString(MainActivity.instance.getResources().getString(R.string.Share_invite));
	}

	@Override
	public void onItemClick(final View arg0, int arg1, ShareMessage cc,
                            UIMessage arg3) {
        mContext = arg0.getContext();
        Log.e("biang2", (null == mContext) + "");
        final TeamSpaceBeanFile fa = new TeamSpaceBeanFile();
        fa.setTitle(cc.getShareDocTitle());
        fa.setAttachmentID(Integer.parseInt(cc.getAttachmentID()));
        getTempLesson(fa);
        Log.e("biang", fa.getAttachmentID() + ":" + fa.getTitle());
		
	}

    private void getTempLesson(final TeamSpaceBeanFile fa) {
        final JSONObject jsonObject = null;
        Log.e("biang", fa.getAttachmentID() + ":" + fa.getTitle());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "Lesson/AddTempLesson?attachmentID=" + fa.getAttachmentID()
                                    + "&Title=" + LoginGet.getBase64Password(fa.getTitle()), jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "  " + responsedata.toString());
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddTempLesson;
                        fa.setLessonId(responsedata.getString("RetData"));
                        msg.obj = fa;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

	@Override
	public void onItemLongClick(View arg0, int arg1, ShareMessage arg2,
			UIMessage arg3) {
	}


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    break;
                case AppConfig.AddTempLesson:
//                    result = (String) msg.obj;
//                    ViewdoHaha(result);
                    GoToVIew((TeamSpaceBeanFile) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };


    private void GoToVIew(TeamSpaceBeanFile lesson) {
        Log.e("biang", (null == mContext) + "");
        if(!(null == mContext)) {
            Intent intent = new Intent(mContext, WatchCourseActivity3.class);
            intent.putExtra("userid", AppConfig.UserID);
            intent.putExtra("meetingId", lesson.getAttachmentID() + "," + AppConfig.UserID);
            intent.putExtra("isTeamspace", true);
            intent.putExtra("yinxiangmode", 0);
            intent.putExtra("identity", 2);
            intent.putExtra("lessionId", "");
            intent.putExtra("isInstantMeeting", 0);
            intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
            intent.putExtra("isStartCourse", true);
            mContext.startActivity(intent);
        }
    }


}
