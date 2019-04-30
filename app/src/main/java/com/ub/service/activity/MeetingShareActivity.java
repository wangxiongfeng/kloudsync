package com.ub.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.kloudsync.techexcel.R;

public class MeetingShareActivity extends Activity implements View.OnClickListener {

    private ImageView mBack;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetingshareactivity);
        initView();


    }


    private void initView() {

        mBack = (ImageView) findViewById(R.id.back_iv);
        mBack.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:


                break;

        }
    }
}
