package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.WeiXinApi;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.ub.techexcel.adapter.YinXiangAdapter;
import com.ub.techexcel.adapter.YinXiangAdapter2;
import com.ub.techexcel.bean.SoundtrackBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class YinxiangPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView close;
    private ImageView backimg;
    private RecyclerView recycleview;
    private RecyclerView allrecycleview;

    private RelativeLayout ll1;
    private RelativeLayout ll2;
    private TextView selectmore;
    private TextView ok;

    private LinearLayout createyinxiang;
    private YinXiangAdapter yinXiangAdapter;
    private YinXiangAdapter2 yinXiangAdapter2;
    private List<SoundtrackBean> mlist = new ArrayList<>();
    private List<SoundtrackBean> allList = new ArrayList<>();
    private static FavoritePoPListener mFavoritePoPListener;
    private String attachmentid;
    private String lessonid;
    private ClipboardManager myClipboard;

    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void createYinxiang();

        void editYinxiang(SoundtrackBean soundtrackBean);

        void playYinxiang(SoundtrackBean soundtrackBean);

    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        myClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.yinxiang_popup, null);
        close = (ImageView) view.findViewById(R.id.close);
        backimg = (ImageView) view.findViewById(R.id.backimg);

        ll1 = (RelativeLayout) view.findViewById(R.id.ll1);
        ll2 = (RelativeLayout) view.findViewById(R.id.ll2);
        selectmore = (TextView) view.findViewById(R.id.selectmore);
        selectmore.setOnClickListener(this);
        ok = (TextView) view.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        backimg.setOnClickListener(this);

        recycleview = (RecyclerView) view.findViewById(R.id.recycleview);
        allrecycleview = (RecyclerView) view.findViewById(R.id.allrecycleview);
        createyinxiang = (LinearLayout) view.findViewById(R.id.createyinxiang);
        createyinxiang.setOnClickListener(this);

        recycleview.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        allrecycleview.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

        yinXiangAdapter = new YinXiangAdapter(mContext, mlist);
        yinXiangAdapter.setFavoritePoPListener(new YinXiangAdapter.FavoritePoPListener() {
            @Override
            public void editYinxiang(SoundtrackBean soundtrackBean) {
                dismiss();
                getgetSoundtrackItem(soundtrackBean, 1);
            }

            @Override
            public void deleteYinxiang(SoundtrackBean soundtrackBean) {
                deleteYinxiang2(soundtrackBean);
            }

            @Override
            public void playYinxiang(SoundtrackBean soundtrackBean) {
                dismiss();
                getgetSoundtrackItem(soundtrackBean, 0);
            }

            @Override
            public void shareYinxiang(SoundtrackBean soundtrackBean) {
                dismiss();
                shareDocument(soundtrackBean.getSoundtrackID() + "");
            }

            @Override
            public void copyUrl(SoundtrackBean soundtrackBean) {
                CopyLink(soundtrackBean);
            }

            @Override
            public void shareInApp(SoundtrackBean soundtrackBean) {

            }
        });
        recycleview.setAdapter(yinXiangAdapter);
        close.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFavoritePoPListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.anination3);

    }

    private ClipboardManager mClipboard = null;

    private void CopyLink(SoundtrackBean soundtrackBean) {
        String url = AppConfig.SHARE_DOCUMENT + soundtrackBean.getSoundtrackID();
        if (soundtrackBean.getSoundtrackID() > 0) {
            url = AppConfig.SHARE_SYNC + soundtrackBean.getSoundtrackID();
        }
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        mClipboard.setPrimaryClip(ClipData.newPlainText(null, url));
        Toast.makeText(mContext, "Copy link success!", Toast.LENGTH_LONG).show();
    }


    public void getSoundtrack(final String attachmentid) {
        this.attachmentid = attachmentid;
        if (TextUtils.isEmpty(attachmentid)) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "Soundtrack/List?attachmentID=" + attachmentid;
        ServiceInterfaceTools.getinstance().getSoundList(url, ServiceInterfaceTools.GETSOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SoundtrackBean> oo = (List<SoundtrackBean>) object;
                        mlist.clear();
                        mlist.addAll(oo);
                        yinXiangAdapter.notifyDataSetChanged();
                    }
                }, isHidden, ishavepresenter);
    }


    public void getSoundtrack(final String attachmentid, final String lessonId) {
        this.attachmentid = attachmentid;
        this.lessonid = lessonId;
        if (TextUtils.isEmpty(attachmentid)) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "LessonSoundtrack/List?lessonID=" + lessonId + "&attachmentID=" + attachmentid;
        ServiceInterfaceTools.getinstance().getSoundList(url, ServiceInterfaceTools.GETSOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SoundtrackBean> oo = (List<SoundtrackBean>) object;
                        mlist.clear();
                        mlist.addAll(oo);
                        yinXiangAdapter.notifyDataSetChanged();
                    }
                }, isHidden, ishavepresenter);
    }


    public void getSoundtrack2(final String attachmentid) {
        this.attachmentid = attachmentid;
        if (TextUtils.isEmpty(attachmentid)) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "Soundtrack/List?attachmentID=" + attachmentid;
        ServiceInterfaceTools.getinstance().getSoundList(url, ServiceInterfaceTools.GETSOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SoundtrackBean> oo = (List<SoundtrackBean>) object;
                        allList.clear();
                        allList.addAll(oo);
                        yinXiangAdapter2 = new YinXiangAdapter2(mContext, mlist, allList);
                        allrecycleview.setAdapter(yinXiangAdapter2);
                    }
                }, isHidden, ishavepresenter);

    }


    private void deleteYinxiang2(SoundtrackBean soundtrackBean) {
        final int soundtrackID = soundtrackBean.getSoundtrackID();
        String url = AppConfig.URL_PUBLIC + "Soundtrack/Delete?soundtrackID=" + soundtrackID;
        ServiceInterfaceTools.getinstance().deleteSound(url, ServiceInterfaceTools.DELETESOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        if (TextUtils.isEmpty(lessonid)) {
                            getSoundtrack(attachmentid);
                        } else {
                            getSoundtrack(attachmentid, lessonid);
                        }
                    }
                });
    }


    private boolean isHidden;
    private boolean ishavepresenter = true;

    @SuppressLint("NewApi")
    public void StartPop(View v, String attachmentid, String lessonid, boolean isHidden, boolean ishavepresenter) {
        if (mPopupWindow != null) {
            this.isHidden = isHidden;
            this.ishavepresenter = ishavepresenter;
            Log.e("eeeee", attachmentid + "   " + lessonid + "  " + isHidden + "  " + ishavepresenter);

            if (TextUtils.isEmpty(attachmentid)) {
                return;
            }

            if (attachmentid.equals("0")) {
                selectmore.setVisibility(View.GONE);
            }
            mFavoritePoPListener.open();
            mPopupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0);
            if (TextUtils.isEmpty(lessonid)) {
                getSoundtrack(attachmentid);
            } else {
                getSoundtrack(attachmentid, lessonid);
            }


            if (isHidden) {
                createyinxiang.setVisibility(View.GONE);
            } else {
                createyinxiang.setVisibility(View.VISIBLE);
            }
            if (!ishavepresenter) {
                createyinxiang.setVisibility(View.GONE);
            }
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();
                break;
            case R.id.createyinxiang:
                dismiss();
                mPopupWindow = null;
                mFavoritePoPListener.createYinxiang();
                break;
            case R.id.selectmore:
                ll1.setVisibility(View.GONE);
                ll2.setVisibility(View.VISIBLE);
                getSoundtrack2(attachmentid);

                break;
            case R.id.ok:
                ll1.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.GONE);
                String soundids = "";
                for (int i = 0; i < allList.size(); i++) {
                    SoundtrackBean soundtrackBean = allList.get(i);
                    if (soundtrackBean.isCheck()) {
                        soundids = soundids + soundtrackBean.getSoundtrackID() + ",";
                    }
                }
                if (!TextUtils.isEmpty(soundids)) {
                    addsoundtolesson(soundids.substring(0, soundids.length() - 1));
                }
                break;
            case R.id.backimg:
                ll1.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    private void addsoundtolesson(final String soundtrackIDs) {
        String url = AppConfig.URL_PUBLIC + "LessonSoundtrack?lessonID=" + lessonid + "&soundtrackIDs=" + soundtrackIDs;
        ServiceInterfaceTools.getinstance().addSoundToLesson(url, ServiceInterfaceTools.ADDSOUNDTOLESSON,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        if (TextUtils.isEmpty(lessonid)) {
                            getSoundtrack(attachmentid);
                        } else {
                            getSoundtrack(attachmentid, lessonid);
                        }
                    }
                });
    }

    private void shareDocument(final String pdfAttachmentId) {
//        String url = AppConfig.URL_PUBLIC
//                + "ShareDocument/Share?attachmentID=" + pdfAttachmentId
//                + "&toShareUserID=" + 0
//                + "&title=";
//        ServiceInterfaceTools.getinstance().shareDocument(url, ServiceInterfaceTools.SHARESOUNDTOLESSON,
//                new ServiceInterfaceListener() {
//                    @Override
//                    public void getServiceReturnData(Object object) {
//                        String data = (String) object;
//                        String htmlUrl = "https://peertime.cn/sync/" + data;
//                        //复制到剪贴板
//                        ClipData myClip;
//                        String text = htmlUrl.toString();
//                        myClip = ClipData.newPlainText("text", text);
//                        myClipboard.setPrimaryClip(myClip);
//                        Toast.makeText(mContext, htmlUrl + " Link Copied", Toast.LENGTH_SHORT).show();
//                    }
//                });

        String htmlUrl = "https://kloudsync.peertime.cn/sync/" + pdfAttachmentId;
        //复制到剪贴板
        /*ClipData myClip;
        String text = htmlUrl.toString();
        myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(mContext, htmlUrl + " Link Copied", Toast.LENGTH_SHORT).show();*/

        weixinshare(htmlUrl);
    }


    /**
     *
     */
    private void weixinshare(String url) {
        if (isWXAppInstalledAndSupported(WeiXinApi.getInstance().GetApi())) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = "";
            msg.description = "请点击此框跳转至" + url;
            Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.ubaoapph);
            msg.thumbData = Util.bmpToByteArray(thumb, true);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            WeiXinApi.getInstance().GetApi().sendReq(req);
        } else {
            Toast.makeText(mContext, "微信客户端未安装，请确认",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isWXAppInstalledAndSupported(IWXAPI api) {
        return api.isWXAppInstalled() && api.isWXAppSupportAPI();
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }


    private void getgetSoundtrackItem(final SoundtrackBean soundtrackBean, final int type) {
        final int soundtrackID = soundtrackBean.getSoundtrackID();
        ServiceInterfaceTools.getinstance().getSoundItem(AppConfig.URL_PUBLIC + "Soundtrack/Item?soundtrackID=" + soundtrackID,
                ServiceInterfaceTools.GETSOUNDITEM,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        if (type == 0) {
                            SoundtrackBean sou = (SoundtrackBean) object;
                            mFavoritePoPListener.playYinxiang(sou);
                        } else if (type == 1) {
                            SoundtrackBean sou2 = (SoundtrackBean) object;
                            mFavoritePoPListener.editYinxiang(sou2);
                        }
                    }
                });

    }


}
