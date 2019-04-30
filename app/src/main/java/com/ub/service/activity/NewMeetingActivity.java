package com.ub.service.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AddGroupActivity2;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.adapter.NewMeetingContactAdapter;
import com.ub.techexcel.tools.CalListviewHeight;

import org.feezu.liuli.timeselector.TimeSelector;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2017/6/19.
 */
public class NewMeetingActivity extends Activity implements View.OnClickListener {

    private ImageView cancel;
    private TextView submit;
    private EditText meetingname;
    private TextView meetingstartdate;
    private TextView meetingstarttime;
    private EditText meetingduration;
    private LinearLayout starttimell, startdatell;
    private TextView invitecontact;
    private RecyclerView mRecyclerView;
    private NewMeetingContactAdapter newMeetingContactAdapter;
    private SharedPreferences sharedPreferences;
    private int schoolId;
    private List<Customer> customerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmeeting2);
        EventBus.getDefault().register(this);
        initView();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void post(List<Customer> ll) {
        if (ll != null && ll.size() > 0) {
            customerList.clear();
            customerList.addAll(ll);
            newMeetingContactAdapter = new NewMeetingContactAdapter(this, customerList);
            mRecyclerView.setAdapter(newMeetingContactAdapter);
        }
    }


    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    private void initView() {

        cancel = (ImageView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(this);
        meetingname = (EditText) findViewById(R.id.meetingname);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss");
        String time = simpleDateFormat.format(new Date());
        meetingname.setText(AppConfig.UserName + time);
        meetingname.setSelection((AppConfig.UserName + time).length());
        meetingduration = (EditText) findViewById(R.id.meetingduration);

        invitecontact = (TextView) findViewById(R.id.invitecontact);
        invitecontact.setOnClickListener(this);
        meetingstartdate = (TextView) findViewById(R.id.meetingstartdate);
        meetingstarttime = (TextView) findViewById(R.id.meetingstarttime);
        startdatell = (LinearLayout) findViewById(R.id.startdatell);
        starttimell = (LinearLayout) findViewById(R.id.starttimell);
        startdatell.setOnClickListener(this);
        starttimell.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        schoolId = sharedPreferences.getInt("SchoolID", -1);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
                break;
            case R.id.startdatell:
                selectDate();
                break;
            case R.id.starttimell:
                selectTime();
                break;
            case R.id.invitecontact:
                Intent intent3 = new Intent(this,
                        AddGroupActivity2.class);
                startActivity(intent3);
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    Long endsecond;

    private void selectTime() {
//        Calendar calendar = Calendar.getInstance();
//        new TimePickerDialog(NewMeetingActivity.this, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                meetingstarttime.setText(hourOfDay + ":" + minute);
//            }
//        }, calendar.get(Calendar.HOUR), Calendar.MINUTE, true).show();


        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        TimeSelector timeSelector = new TimeSelector(NewMeetingActivity.this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                try {
                    endsecond = formatter.parse(time).getTime();
                    meetingstarttime.setText(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, str, "2030-12-31 00:00");
        timeSelector.show();

    }


    Long startsecond;

    private void selectDate() {
//        Calendar calendar = Calendar.getInstance();
//        new DatePickerDialog(NewMeetingActivity.this,
//                new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year,
//                                          int monthOfYear, int dayOfMonth) {
//                        meetingstartdate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
//                    }
//                }
//                , calendar.get(Calendar.YEAR)
//                , calendar.get(Calendar.MONTH)
//                , calendar.get(Calendar.DAY_OF_MONTH)).show();

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        TimeSelector timeSelector = new TimeSelector(NewMeetingActivity.this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                try {
                    startsecond = formatter.parse(time).getTime();
                    meetingstartdate.setText(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, str, "2030-12-31 00:00");
        timeSelector.show();


    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1001:
                    AppConfig.newlesson = true;
                    finish();
                    break;
            }
        }
    };

    private void submit() {
        if (startsecond == 0 || endsecond == 0) {
            Toast.makeText(this, "please select date", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Title", meetingname.getText().toString());
                    jsonObject.put("Description", meetingduration.getText().toString());
                    jsonObject.put("StartDate", startsecond);
                    jsonObject.put("EndDate", endsecond);
                    jsonObject.put("LessonType", 5);
                    jsonObject.put("SchoolID", schoolId);

                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < customerList.size(); i++) {
                        JSONObject j = new JSONObject();
                        j.put("MemberID", customerList.get(i).getUserID());
                        j.put("Role", 1);
                        jsonArray.put(j);
                    }
                    JSONObject j2 = new JSONObject();
                    j2.put("MemberID", AppConfig.UserID);
                    j2.put("Role", 2);
                    jsonArray.put(j2);
                    jsonObject.put("LessonMembers", jsonArray);

                    JSONObject returnJson = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/AddScheduleMeetingLesson", jsonObject);
                    Log.e("CreateOrUpdateLessons", jsonObject.toString() + "    " + returnJson.toString());
                    int retCode = returnJson.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            Message msg = Message.obtain();
                            msg.what = 0x1001;
                            handler.sendMessage(msg);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
