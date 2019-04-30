package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.Favorite;
import com.kloudsync.techexcel.start.LoginGet;

import java.util.ArrayList;
import java.util.List;

import static com.kloudsync.techexcel.R.id.addsavefile;

/**
 * Created by wang on 2017/9/18.
 */
public class FavoriteVideoPopup {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private List<Favorite> list = new ArrayList<Favorite>();
    private DocumentAdapter mDocumentAdapter;
    private ListView listView;
    private TextView savevideo, saveaudio;
    private View view;
    private ImageView cancel;
    private TextView uploadfile;
    private int type = 0;
    private boolean isYinxiang=false;

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
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


    public void setData(int type,boolean isYinxiang) {  // 2   video  3   audio
        selectPosition=-1;
        this.type = type;
        this.isYinxiang=isYinxiang;
        savevideo.setVisibility(View.VISIBLE);

        if(type==2){
            savevideo.setTextColor(mContext.getResources().getColor(R.color.blue));
            saveaudio.setTextColor(mContext.getResources().getColor(R.color.black));
            saveaudio.setVisibility(View.GONE);
            savevideo.setVisibility(View.VISIBLE);
            uploadfile.setVisibility(View.VISIBLE);
        }else if(type==3){
            uploadfile.setVisibility(View.INVISIBLE);
            savevideo.setTextColor(mContext.getResources().getColor(R.color.black));
            saveaudio.setTextColor(mContext.getResources().getColor(R.color.blue));
            saveaudio.setVisibility(View.VISIBLE);
            savevideo.setVisibility(View.GONE);
        }

        LoginGet loginGet = new LoginGet();
        loginGet.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
            @Override
            public void getFavorite(ArrayList<Favorite> list2) {
                list.clear();
                list.addAll(list2);
                mDocumentAdapter.notifyDataSetChanged();
            }
        });
        loginGet.MyFavoriteRequest(mContext, type);

    }

    public List<Favorite> getData() {
        return list;
    }


    private static FavoriteVideoPoPListener mFavoritePoPListener;

    public interface FavoriteVideoPoPListener {

        void selectFavorite(int position);

        void cancel();

        void save(int type,boolean isYinxiang);

        void uploadFile();

        void dismiss();

        void open();
    }

    public void setFavoritePoPListener(FavoriteVideoPoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    int selectPosition = -1;

    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.popup_save_video, null);
        listView = (ListView) view.findViewById(R.id.listview);
        cancel = (ImageView) view.findViewById(addsavefile);

        savevideo = (TextView) view.findViewById(R.id.savevideo);
        saveaudio = (TextView) view.findViewById(R.id.saveaudio);

        savevideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData(2,false);
            }
        });

        saveaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData(3,false);
            }
        });


        uploadfile = (TextView) view.findViewById(R.id.uploadfile);
        mDocumentAdapter = new DocumentAdapter(mContext, list,
                R.layout.popup_video_item);
        listView.setAdapter(mDocumentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mFavoritePoPListener.selectFavorite(position);
                selectPosition = position;
                mDocumentAdapter.notifyDataSetChanged();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFavoritePoPListener.cancel();
                mPopupWindow.dismiss();
            }
        });
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFavoritePoPListener.save(type,isYinxiang);
                mPopupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFavoritePoPListener.cancel();
                mPopupWindow.dismiss();
            }
        });
        uploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFavoritePoPListener.uploadFile();
            }
        });

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.getWidth();
        mPopupWindow.getHeight();
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFavoritePoPListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
    }


    public void dismiss() {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            mPopupWindow.dismiss();
        }
    }

    public class DocumentAdapter extends BaseAdapter {
        private Context context;
        private List<Favorite> mDatas;
        private int itemLayoutId;

        public DocumentAdapter(Context context, List<Favorite> mDatas,
                               int itemLayoutId) {
            this.context = context;
            this.mDatas = mDatas;
            this.itemLayoutId = itemLayoutId;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        itemLayoutId, null);
                holder.name = (TextView) convertView
                        .findViewById(R.id.name);
                holder.size = (TextView) convertView
                        .findViewById(R.id.filesize);
                holder.time = (TextView) convertView
                        .findViewById(R.id.totalTime);
                holder.imageview = (ImageView) convertView
                        .findViewById(R.id.imageview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(mDatas.get(position).getTitle());
            holder.time.setText(mDatas.get(position).getDuration());
            holder.size.setText(mDatas.get(position).getSize());
            if (selectPosition == position) {
                holder.imageview.setImageResource(R.drawable.finish_a);
            } else {
                holder.imageview.setImageResource(R.drawable.finish_d);
            }
            return convertView;
        }

        class ViewHolder {
            TextView name;
            TextView time;
            TextView size;
            ImageView imageview;
        }
    }

}
