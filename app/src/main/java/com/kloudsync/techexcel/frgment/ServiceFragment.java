package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.school.SelectSchoolActivity;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.mining.app.zxing.MipcaActivityCapture;
import com.ub.kloudsync.activity.DocumentEditYinXiangPopup;
import com.ub.kloudsync.activity.DocumentYinXiangPopup;
import com.ub.service.activity.MyKlassroomActivity;
import com.ub.service.activity.NewMeetingActivity;
import com.ub.service.activity.NewPublicLessonActivity;
import com.ub.service.activity.NotifyActivity;
import com.ub.service.activity.SelectCourseActivity;
import com.ub.service.activity.ServiceDetailActivity;
import com.ub.service.activity.WatchCourseActivity2;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.adapter.ServiceAdapter2;
import com.ub.techexcel.adapter.UpcomeingAdapter;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.tools.EventSchoolPopup;
import com.ub.techexcel.tools.MenuEventPopup;
import com.ub.techexcel.tools.NewMeetingPopup;
import com.ub.techexcel.tools.ServiceTool;
import com.ub.techexcel.tools.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class ServiceFragment extends MyFragment implements View.OnClickListener {
    private boolean isPrepared = false;

    private ServiceAdapter2 serviceAdapter1;
    private ListView serviceListView1;
    private ServiceAdapter2 serviceAdapter2;
    private ListView serviceListView2;
    private ServiceAdapter2 serviceAdapter3;
    private ListView serviceListView3;
    private List<ServiceBean> mList1 = new ArrayList<>();
    private List<ServiceBean> mList2 = new ArrayList<>();
    private List<ServiceBean> mList3 = new ArrayList<>();

    private List<List<ServiceBean>> mlist = new ArrayList<>();  //所有课程的list集合

    private ViewPager mViewPager;
    private List<View> viewList = new ArrayList<>();
    private TextView inprogressunderline;
    private int currIndex = 0;
    private int width;

    private ImageView addService;
    private ImageView img_notice;
    private TextView underline;
    private TextView tv_ns;
    private BroadcastReceiver broadcastReceiver;

    private ArrayList<Customer> custometList = new ArrayList<Customer>();
    private BroadcastReceiver broadcastReceiver_finish;
    private LinearLayout defaultPage;
    private EventSchoolPopup eventSchoolPopup;
    private MenuEventPopup menuPopupWindow;

    private LinearLayout lin_myroom, lin_join, lin_schedule;
    private TextView tv_title;
    private TextView xxschool;
    private ImageView changeschool;
    private int lessionid;
    private TextView upcoming, pastdue, finished;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    defaultPage.setVisibility(View.GONE);
                    serviceAdapter1 = new ServiceAdapter2(getActivity(), mList1, true, 0);
                    serviceListView1.setAdapter(serviceAdapter1);
                    serviceAdapter1
                            .setOnModifyServiceListener(new ServiceAdapter2.OnModifyServiceListener() {
                                @Override
                                public void onBeginStudy(int position) { // 开始上课
                                    Intent intent = new Intent(getActivity(), WatchCourseActivity2.class);
                                    intent.putExtra("userid", mList1.get(position).getUserId());
                                    intent.putExtra("meetingId", mList1.get(position).getId() + "");
                                    intent.putExtra("filemeetingId", mList1.get(position).getId() + "");
                                    intent.putExtra("teacherid", mList1.get(position).getTeacherId());
                                    intent.putExtra("identity", mList1.get(position).getRoleinlesson());
                                    intent.putExtra("isInstantMeeting", 0);
                                    intent.putExtra("isStartCourse", true);
                                    startActivity(intent);
                                }


                                @Override
                                public void prepareCourse(int position) {  //备课
                                    Intent intent = new Intent(getActivity(), WatchCourseActivity2.class);
                                    intent.putExtra("userid", mList1.get(position).getUserId());
                                    intent.putExtra("meetingId", mList1.get(position).getId() + "");
                                    intent.putExtra("teacherid", mList1.get(position).getTeacherId());
                                    intent.putExtra("identity", mList1.get(position).getRoleinlesson());
                                    intent.putExtra("isInstantMeeting", 0);
                                    intent.putExtra("isStartCourse", true);
                                    intent.putExtra("isPrepare", true);
                                    intent.putExtra("yinxiangmode", 1);
                                    startActivity(intent);
                                }
                            });

                    serviceAdapter2 = new ServiceAdapter2(getActivity(), mList2, true, 1);
                    serviceAdapter2.setOnModifyServiceListener(new ServiceAdapter2.OnModifyServiceListener() {
                        @Override
                        public void onBeginStudy(int position) {

                        }

                        @Override
                        public void prepareCourse(int position) {  //view
                            Intent intent = new Intent(getActivity(), WatchCourseActivity2.class);
                            intent.putExtra("userid", mList1.get(position).getUserId());
                            intent.putExtra("meetingId", mList1.get(position).getId() + "");
                            intent.putExtra("teacherid", mList1.get(position).getTeacherId());
                            intent.putExtra("identity", mList1.get(position).getRoleinlesson());
                            intent.putExtra("isInstantMeeting", 0);
                            intent.putExtra("isStartCourse", true);
                            intent.putExtra("isPrepare", true);
                            intent.putExtra("yinxiangmode", 1);
                            startActivity(intent);
                        }
                    });
                    serviceListView2.setAdapter(serviceAdapter2);

                    serviceAdapter3 = new ServiceAdapter2(getActivity(), mList3, true, 2);
                    serviceAdapter3.setOnModifyServiceListener(new ServiceAdapter2.OnModifyServiceListener() {
                        @Override
                        public void onBeginStudy(int position) {
                        }


                        @Override
                        public void prepareCourse(int position) {  //finish
                            Intent intent = new Intent(getActivity(), WatchCourseActivity2.class);
                            intent.putExtra("userid", mList3.get(position).getUserId());
                            intent.putExtra("meetingId", mList3.get(position).getId() + "");
                            intent.putExtra("filemeetingId", mList3.get(position).getId() + "");
                            intent.putExtra("isFinished", true);
                            intent.putExtra("teacherid", mList3.get(position).getTeacherId());
                            intent.putExtra("identity", mList3.get(position).getRoleinlesson());
                            intent.putExtra("isInstantMeeting", 0);
                            intent.putExtra("isStartCourse", true);
                            intent.putExtra("isPrepare", true);
                            intent.putExtra("yinxiangmode", 1);
                            startActivity(intent);
                        }
                    });
                    serviceListView3.setAdapter(serviceAdapter3);
                    break;
                case AppConfig.CONFIRM_SERVICE:  //结束课程
                    break;
                case 0x1102:
                    Intent intent = new Intent(getActivity(), SelectCourseActivity.class);
                    intent.putExtra("service", (ServiceBean) msg.obj);
                    AppConfig.ISMODIFY_SERVICE = true;
                    startActivity(intent);
                    break;
                case 0x1104:  //加入会议室
                    ServiceBean serviceBean = (ServiceBean) msg.obj;
                    Intent intent3 = new Intent(getActivity(), WatchCourseActivity2.class);
                    intent3.putExtra("isHtml", serviceBean.getLineItems().get(0).isHtml5());
                    intent3.putExtra("url", serviceBean.getLineItems().get(0).getUrl());
                    intent3.putExtra("CustomerRongCloudID", serviceBean.getCustomerRongCloudId());
                    intent3.putExtra("attachmentid", serviceBean.getLineItems().get(0).getAttachmentID());
                    intent3.putExtra("userid", serviceBean.getUserId());
                    intent3.putExtra("meetingId", serviceBean.getId() + "");
                    intent3.putExtra("teacherid", serviceBean.getTeacherId());
                    intent3.putExtra("isInstantMeeting", 0);
                    if (serviceBean.getTeacherId().equals((AppConfig.UserID + "").replace("-", ""))) {
                        intent3.putExtra("identity", 2);
                    } else if (serviceBean.getUserId().equals((AppConfig.UserID + "").replace("-", ""))) {
                        intent3.putExtra("identity", 1);
                    } else {
                        intent3.putExtra("identity", 3);
                    }
                    intent3.putExtra("lineItems", (Serializable) serviceBean.getLineItems());
                    startActivity(intent3);
                    break;
                case 0x1001:   // start meeting
                    lessionid = (int) msg.obj;
                    if (lessionid == -1) {
                        addInstantLesson(AppConfig.ClassRoomID);
                    } else {
                        Intent intent4 = new Intent(getActivity(), WatchCourseActivity3.class);
                        intent4.putExtra("meetingId", AppConfig.ClassRoomID + "");
                        intent4.putExtra("identity", 2);
                        intent4.putExtra("lessionId", lessionid + "");
                        intent4.putExtra("ishavedefaultpage",true);
                        intent4.putExtra("isInstantMeeting", 1);
                        intent4.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
                        intent4.putExtra("isStartCourse", true);
                        startActivity(intent4);
                        getActivity().finish();
                    }
                    break;
                case 0x1002:  // addInstantLesson
                    Log.e("getClassRoomLessonID", "加入课程成功");
                    int lessionid2 = (int) msg.obj;
                    Intent intent5 = new Intent(getActivity(), WatchCourseActivity3.class);
                    intent5.putExtra("meetingId", AppConfig.ClassRoomID + "");
                    intent5.putExtra("identity", 2);
                    intent5.putExtra("ishavedefaultpage",true);
                    intent5.putExtra("lessionId", lessionid2 + "");
                    intent5.putExtra("isInstantMeeting", 1);
                    intent5.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
                    intent5.putExtra("isStartCourse", true);
                    startActivity(intent5);
                    getActivity().finish();
                    break;
                default:
                    break;
            }
        }
    };

    private List<ServiceBean> sortBydata(List<ServiceBean> serviceBeanList) {
        Collections.sort(serviceBeanList, new Comparator<ServiceBean>() {
            @Override
            public int compare(ServiceBean s1, ServiceBean s2) {
                String x1 = s1.getPlanedStartDate();
                String x2 = s2.getPlanedStartDate();
                if (TextUtils.isEmpty(x1)) {
                    x1 = "0";
                }
                if (TextUtils.isEmpty(x2)) {
                    x2 = "0";
                }
                if (Long.parseLong(x1) > Long.parseLong(x2)) {
                    return 1;
                }
                if (Long.parseLong(x1) == Long.parseLong(x2)) {
                    return 0;
                }
                return -1;
            }
        });
        for (ServiceBean bean : serviceBeanList) {
            String planedsatrtdate = bean.getPlanedStartDate();
            if (TextUtils.isEmpty(planedsatrtdate)) {
                bean.setDateType(4);
            } else {
                long today = System.currentTimeMillis();
                long planed = Long.parseLong(planedsatrtdate);
                long diff = diffTime();
                long xx = planed - today;
                if (xx < 0) {
                    bean.setDateType(4);//今天之前的  已结束的
                } else if (xx >= 0 && xx < diff) {
                    bean.setDateType(1); //今天的
                    bean.setMins((int) (xx / 1000 / 60));
                } else if (xx >= diff && xx < 86400000 * 2) {
                    bean.setDateType(2); //明天的
                } else if (xx >= 86400000 * 2) {
                    bean.setDateType(3);//后天及以后
                }
            }
        }
        return serviceBeanList;

    }

    private long diffTime() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long diff = cal.getTimeInMillis() - System.currentTimeMillis();
        return diff;
    }


    /**
     * 按planstartdate排序
     */
    private void sortList(List<ServiceBean> list) {
        int i, j;
        int n = list.size();
        for (i = 0; i < n; i++) {
            for (j = 1; j < n - 1; j++) {
                String x1 = list.get(j - 1).getPlanedStartDate();
                String x2 = list.get(j).getPlanedStartDate();
                if (TextUtils.isEmpty(x1)) {
                    x1 = "0";
                }
                if (TextUtils.isEmpty(x2)) {
                    x2 = "0";
                }
                if (Long.parseLong(x1) > Long.parseLong(x2)) {
                    ServiceBean serviceBean = list.get(j - 1);
                    list.set(j - 1, list.get(j));
                    list.set(j, serviceBean);
                }
            }
        }
    }

    /**
     * @param customer
     */
    public void GoToDialog(final Customer customer, final String rongcloudid) {
        AppConfig.Name = customer.getName();
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                return new UserInfo(
                        customer.getUBAOUserID(),
                        customer.getName(),
                        null);
            }
        }, true);
        RongIM.getInstance().startPrivateChat(getActivity(),
                rongcloudid + "", customer.getName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service, container, false);
        initView(view);
        isPrepared = true;
        lazyLoad();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        broadcastReceiver_finish = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getAllServiceData(mlist);
            }
        };
        IntentFilter filter_finish = new IntentFilter();
        filter_finish.addAction("com.ubao.techexcel.frgment");
        getActivity().registerReceiver(broadcastReceiver_finish, filter_finish);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        getActivity().unregisterReceiver(broadcastReceiver_finish);
        broadcastReceiver_finish = null;
        if (broadcastReceiver != null && getActivity() != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        super.onDestroy();
    }


    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible) {
            GetSchoolInfo();
        }
    }


    /**
     * 获得所有原始数据
     *
     * @param pjList
     */
    private void getAllServiceData(List<List<ServiceBean>> pjList) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,
                getActivity().MODE_PRIVATE);
        int schoolId = sharedPreferences.getInt("SchoolID", -1);

        List<ServiceBean> list0 = new ArrayList<>();
        List<ServiceBean> list1 = new ArrayList<>();
        List<ServiceBean> list2 = new ArrayList<>();

        pjList.clear();
        pjList.add(list0); //即将开始
        pjList.add(list1); //已过期
        pjList.add(list2);  //已结束
        mList1.clear();
        mList2.clear();
        mList3.clear();

        ExecutorService executorService = Executors.newFixedThreadPool(pjList.size());
        for (int i = 0; i < pjList.size(); i++) {
            executorService.execute(new ServiceTool(i, pjList.get(i), custometList, schoolId));
        }
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {

                List<ServiceBean> serviceBeanList = sortBydata(list0);
                for (ServiceBean bean : serviceBeanList) {
                    mList1.add(bean);
                }

                List<ServiceBean> serviceBeanList1 = sortBydata(list1);
                for (ServiceBean bean : serviceBeanList1) {
                    mList2.add(bean);
                }

                List<ServiceBean> serviceBeanList2 = sortBydata(list2);
                for (ServiceBean bean : serviceBeanList2) {
                    mList3.add(bean);
                }

                Collections.reverse(mList1);
                Collections.reverse(mList2);
                Collections.reverse(mList3);

                handler.sendEmptyMessage(AppConfig.LOAD_FINISH);
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppConfig.newlesson) {  //新建课程刷新
            AppConfig.newlesson = false;
            getCustomerList();
        }
        if (tv_title != null) {  //切换schoolId刷新
            GetSchoolInfo();
        }
    }


    private int schoolid = -1;

    private void GetSchoolInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,
                getActivity().MODE_PRIVATE);
        int SchoolId = sharedPreferences.getInt("SchoolID", -1);
        String schoolName = sharedPreferences.getString("SchoolName", null);
        if (-1 == SchoolId || SchoolId == AppConfig.SchoolID) {
//            tv_title.setText(getResources().getString(R.string.My_School));
            xxschool.setText(getResources().getString(R.string.My_School));
        } else {
//            tv_title.setText(schoolName);
            xxschool.setText(schoolName);
        }
        Log.e("SERVICEFRAGMENT", SchoolId + "  " + schoolid);
        if (schoolid != SchoolId) { //切换 schoolid 刷新servicelist
            getCustomerList();
            schoolid = SchoolId;
        }
    }


    private void getCustomerList() {
        final LoginGet loginGet = new LoginGet();
        loginGet.setLoginGetListener(new LoginGet.LoginGetListener() {
            @Override
            public void getMember(ArrayList<Customer> list) {

            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                custometList = new ArrayList<Customer>();
                custometList = list;
                getLoginUser();
            }
        });
        loginGet.CustomerRequest(getActivity());
    }


    //登录账号信息
    private void getLoginUser() {
        Customer CustomerDetailRequest = new Customer();
        CustomerDetailRequest.setUserID(AppConfig.UserID);
        custometList.add(CustomerDetailRequest);
        getAllServiceData(mlist);
    }


    private void initView(View view) {

        Fresco.initialize(getActivity());
        lin_myroom = (LinearLayout) view.findViewById(R.id.lin_myroom);
        lin_join = (LinearLayout) view.findViewById(R.id.lin_join);
        lin_schedule = (LinearLayout) view.findViewById(R.id.lin_schedule);
        lin_myroom.setOnClickListener(this);
        lin_join.setOnClickListener(this);
        lin_schedule.setOnClickListener(this);

        inprogressunderline = (TextView) view.findViewById(R.id.inprogressunderline);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        width = screenW / 3;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) inprogressunderline.getLayoutParams();
        lp.width = width / 2; //设置滑动条的宽度为屏幕的1/6
        lp.leftMargin = width / 4;
        inprogressunderline.setLayoutParams(lp);

        upcoming = (TextView) view.findViewById(R.id.upcoming);
        upcoming.setOnClickListener(this);
        pastdue = (TextView) view.findViewById(R.id.pastdue);
        pastdue.setOnClickListener(this);
        finished = (TextView) view.findViewById(R.id.finished);
        finished.setOnClickListener(this);


        changeschool = (ImageView) view.findViewById(R.id.changeschool);
        changeschool.setOnClickListener(this);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        addService = (ImageView) view.findViewById(R.id.addService);
        underline = (TextView) view.findViewById(R.id.underline);
        xxschool = (TextView) view.findViewById(R.id.xxschool);

        addService.setOnClickListener(this);
        addService.setVisibility(View.GONE);

        tv_ns = (TextView) view.findViewById(R.id.tv_ns);
        img_notice = (ImageView) view.findViewById(R.id.img_notice);
        img_notice.setOnClickListener(this);

        defaultPage = (LinearLayout) view.findViewById(R.id.defaultpage);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View allView1 = layoutInflater.inflate(R.layout.tabone, null);
        View allView2 = layoutInflater.inflate(R.layout.tabtwo, null);
        View allView3 = layoutInflater.inflate(R.layout.tabthree, null);

        serviceListView1 = (ListView) allView1.findViewById(R.id.serviceList);
        serviceListView2 = (ListView) allView2.findViewById(R.id.serviceList);
        serviceListView3 = (ListView) allView3.findViewById(R.id.serviceList);
        viewList.add(allView1);
        viewList.add(allView2);
        viewList.add(allView3);

        mViewPager.setAdapter(new ServicePagerAdapter());
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("positionOffset", position + "  " + positionOffset);
                float currMarginLeft = width * positionOffset + position * width + width / 4;
                RelativeLayout.LayoutParams redLp = (RelativeLayout.LayoutParams) inprogressunderline.getLayoutParams();
                redLp.leftMargin = (int) currMarginLeft;
                inprogressunderline.setLayoutParams(redLp);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setDefault();
                        upcoming.setTextColor(getResources().getColor(R.color.skyblue));
                        break;
                    case 1:
                        setDefault();
                        pastdue.setTextColor(getResources().getColor(R.color.skyblue));
                        break;
                    case 2:
                        setDefault();
                        finished.setTextColor(getResources().getColor(R.color.skyblue));
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        GetCourseBroad();

    }

    class ServicePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));

        }

    }


    private void GetCourseBroad() {
        RefreshNotify();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RefreshNotify();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.Receive_Course));
        getActivity().registerReceiver(broadcastReceiver, filter);
    }


    private void RefreshNotify() {
        int sum = 0;
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (!AppConfig.progressCourse.get(i).isStatus()) {
                sum++;
            }
        }
        tv_ns.setText(sum + "");
        tv_ns.setVisibility(sum == 0 ? View.GONE : View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addService:
                menuPopupWindow = new MenuEventPopup();
                menuPopupWindow.getPopwindow(getActivity());
                menuPopupWindow.StartPop(addService);
                menuPopupWindow.setWebCamPopupListener(new MenuEventPopup.WebCamPopupListener() {
                    @Override
                    public void changeOptions(int position) {
                        Intent i;
                        switch (position) {
                            case 0: //new private lesson
                                i = new Intent(getActivity(), SelectCourseActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                                break;
                            case 1:
                                i = new Intent(getActivity(), NewPublicLessonActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                                break;
                            case 2:
                                i = new Intent(getActivity(), MyKlassroomActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                                break;
                            case 3:
                                i = new Intent(getActivity(), MipcaActivityCapture.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                                break;
                            case 4:
                                i = new Intent(getActivity(), SelectSchoolActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                                break;
                        }
                    }
                });
                break;
            case R.id.img_notice:
                startActivity(new Intent(getActivity(), NotifyActivity.class));
                break;
            case R.id.lin_myroom:
                startActivity(new Intent(getActivity(), MyKlassroomActivity.class));
                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                break;
            case R.id.lin_join:
                startActivity(new Intent(getActivity(), NotifyActivity.class));
                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                break;
            case R.id.lin_schedule:

                NewMeetingPopup newMeetingPopup = new NewMeetingPopup();
                newMeetingPopup.getPopwindow(getActivity());
                newMeetingPopup.setFavoritePoPListener(new NewMeetingPopup.FavoritePoPListener() {

                    @Override
                    public void scheduleMeeting() {
                        Intent schintent = new Intent(getActivity(), NewMeetingActivity.class);
                        startActivity(schintent);
                        getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                    }

                    @Override
                    public void startNewMeeting() {
                        if (!Tools.isFastClick()) {
                            if (TextUtils.isEmpty(AppConfig.ClassRoomID)) {
                                Toast.makeText(getActivity(), "你加入的课堂不存在!", Toast.LENGTH_LONG).show();
                            } else {
                                getClassRoomLessonID(AppConfig.ClassRoomID);
                            }
                        }
                    }
                });
                newMeetingPopup.StartPop(lin_schedule);

                break;
            case R.id.changeschool:
                eventSchoolPopup = new EventSchoolPopup();
                eventSchoolPopup.getPopwindow(getActivity());
                eventSchoolPopup.StartPop(changeschool);
                eventSchoolPopup.setWebCamPopupListener(new EventSchoolPopup.WebCamPopupListener() {
                    @Override
                    public void changeOptions(int position) {
                        if (position == 0) {

                        } else if (position == 1) {

                        }
                    }
                });
                break;
            case R.id.upcoming:
                mViewPager.setCurrentItem(0);
                setDefault();
                upcoming.setTextColor(getResources().getColor(R.color.skyblue));
                break;
            case R.id.pastdue:
                mViewPager.setCurrentItem(1);
                setDefault();
                pastdue.setTextColor(getResources().getColor(R.color.skyblue));
                break;
            case R.id.finished:
                mViewPager.setCurrentItem(2);
                setDefault();
                finished.setTextColor(getResources().getColor(R.color.skyblue));
                break;
            default:
                break;
        }

    }


    private void setDefault() {
        upcoming.setTextColor(getResources().getColor(R.color.c5));
        pastdue.setTextColor(getResources().getColor(R.color.c5));
        finished.setTextColor(getResources().getColor(R.color.c5));
    }


    private void getClassRoomLessonID(final String classRoomId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/GetClassRoomLessonID?classRoomID=" + classRoomId);
                    Log.e("getClassRoomLessonID", jsonObject.toString()); // {"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":-1}
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            int retdate = jsonObject.getInt("RetData");
                            Message msg = Message.obtain();
                            msg.what = 0x1001;
                            msg.obj = retdate;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void addInstantLesson(final String classRoomId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    js.put("classroomID", classRoomId);
                    js.put("addBlankPage", 0);
                    JSONObject jsonObject = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/AddInstantLesson?classroomID=" + classRoomId+"&addBlankPage=0", js);
                    Log.e("getClassRoomLessonID2", jsonObject.toString()); // {"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":2477}
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            int retdate = jsonObject.getInt("RetData");
                            Message msg = Message.obtain();
                            msg.what = 0x1002;
                            msg.obj = retdate;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}