package com.kloudsync.techexcel.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HelpCenterAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.bean.LineItem;

import java.util.ArrayList;

public class HelpCenterActivity extends AppCompatActivity {
    private TextView tv_back;
    private TextView tv_title;
    private RecyclerView rv_hc;

    private HelpCenterAdapter hadapter;

    ArrayList<LineItem> mlist = new ArrayList();

    public static final String 汗 = "Σ( ° △ °|||)︴";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        findView();
        initView();
    }

    private void findView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rv_hc = (RecyclerView) findViewById(R.id.rv_hc);
        ViewCompat.setTransitionName(tv_title, 汗);

        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        rv_hc.setLayoutManager(manager);
        hadapter = new HelpCenterAdapter(mlist);
        hadapter.setOnItemClickListener(new HelpCenterAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LineItem item = mlist.get(position);
                Intent intent = new Intent(HelpCenterActivity.this, WatchCourseActivity3.class);
                intent.putExtra("userid", AppConfig.UserID);
                intent.putExtra("meetingId", item.getAttachmentID2() + "," + AppConfig.UserID);
                intent.putExtra("teacherid", AppConfig.UserID);
                intent.putExtra("isTeamspace", true);
                intent.putExtra("identity", 2);
                intent.putExtra("isStartCourse", true);
                intent.putExtra("isPrepare", true);
                intent.putExtra("isInstantMeeting", 0);
                intent.putExtra("yinxiangmode", 0);
                startActivity(intent);
            }
        });
        rv_hc.setAdapter(hadapter);

        getInfo();
    }

    private void getInfo() {
        LoginGet lg = new LoginGet();
        lg.setSpaceAttachmentGetListener(new LoginGet.SpaceAttachmentGetListener() {
            @Override
            public void getSA(ArrayList<LineItem> items) {
                mlist = items;
                hadapter.UpdateRV(mlist);
            }
        });
        lg.GetSpaceAttachment(this, 0);
    }

    private void initView() {
        tv_back.setOnClickListener(new myOnClick());
    }

    protected class myOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
//                    finish();
                    ActivityCompat.finishAfterTransition(HelpCenterActivity.this);
                    break;
                default:
                    break;
            }
        }
    }

}
