package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.InviteNewActivity;
import com.kloudsync.techexcel.help.PopContactHAHA;
import com.kloudsync.techexcel.view.CustomViewPager;
import com.ub.service.activity.NotifyActivity;

import java.util.ArrayList;
import java.util.List;

public class TwoToOneFragment extends Fragment {

    private View view;

    private ImageView img_notice;
    private ImageView img_add;
    private TextView tv_ns;
    private TextView tv_myc;
    private TextView tv_sc;
    private CustomViewPager vp_contact;

    BroadcastReceiver broadcastReceiver;

    private boolean isFragmentVisible = false;
    private boolean isFirst = true;
    private boolean isContact = true;

    private int width;

    private List<Fragment> mTabs = new ArrayList<Fragment>();

    private FragmentPagerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.twotoone_fragment, container, false);
            initView();
        }

        return view;
    }

    private void initView() {
        img_add = (ImageView) view.findViewById(R.id.img_add);
        img_notice = (ImageView) view.findViewById(R.id.img_notice);
        tv_ns = (TextView) view.findViewById(R.id.tv_ns);
        tv_myc = (TextView) view.findViewById(R.id.tv_myc);
        tv_sc = (TextView) view.findViewById(R.id.tv_sc);
        vp_contact = (CustomViewPager) view.findViewById(R.id.vp_contact);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        isFragmentVisible = isVisibleToUser;
        if (isFirst && isVisibleToUser) {
            isFirst = false;
            initFunction();
        }
    }

    private void initFunction() {
        GetCourseBroad();
        initVP();
        img_add.setOnClickListener(new myOnClick());
        img_notice.setOnClickListener(new myOnClick());
        tv_myc.setOnClickListener(new myOnClick());
        tv_sc.setOnClickListener(new myOnClick());

    }

    private void initVP() {

        DialogueFragment dialogueFragment = new DialogueFragment();
        ContactFragment contactFragment = new ContactFragment();

        mTabs = new ArrayList<Fragment>();
        mTabs.add(dialogueFragment);
        mTabs.add(contactFragment);


        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };
        vp_contact.setAdapter(mAdapter);
        vp_contact.setOffscreenPageLimit(2);
    }

    protected class myOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_add:
//                    ShowPop();
                    GoToInviteNew();
                    break;
                case R.id.img_notice:
                    GoToNotice();
                    break;
                case R.id.tv_myc:
                    ChangeList(0);
                    break;
                case R.id.tv_sc:
                    ChangeList(1);
                    break;
                default:
                    break;
            }
        }
    }

    private void GoToInviteNew() {
        Intent intent = new Intent(getActivity(), InviteNewActivity.class);
        intent.putExtra("flag_c", true);
        startActivity(intent);
    }

    private void ShowPop() {
        PopContactHAHA haha = new PopContactHAHA();
        haha.getPopwindow(getActivity());
        haha.StartPop(img_add);
    }

    @SuppressLint("NewApi")
    public void ChangeList(int i) {
        isContact = (0 == i);
        tv_myc.setTextColor(getResources().getColor(0 == i ?R.color.white:R.color.main_color));
        tv_sc.setTextColor(getResources().getColor(0 == i ?R.color.main_color:R.color.white));
        tv_myc.setBackground(getActivity().getDrawable(0 == i ? R.drawable.blue_left_bg:R.drawable.white_left_bg));
        tv_sc.setBackground(getActivity().getDrawable(0 == i ? R.drawable.white_right_bg:R.drawable.blue_right_bg));
        vp_contact.setCurrentItem(i, false);

    }

    private void GoToNotice() {
        startActivity(new Intent(getActivity(), NotifyActivity.class));
    }

    LocalBroadcastManager localBroadcastManager;

    private void GetCourseBroad() {
        RefreshNotify();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RefreshNotify();
            }
        };
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.Receive_Course));
//        getActivity().registerReceiver(broadcastReceiver, filter);
        //LocalBroadcastManager 是基于Handler实现的，拥有更高的效率与安全性。安全性主要体现在数据仅限于应用内部传输，避免广播被拦截、伪造、篡改的风险
        localBroadcastManager.registerReceiver(broadcastReceiver, filter);

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
    public void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null && getActivity() != null) {
//            getActivity().unregisterReceiver(broadcastReceiver);
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }
}
