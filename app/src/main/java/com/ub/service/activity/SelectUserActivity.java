package com.ub.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ub.techexcel.adapter.MyAdapter;
import com.ub.techexcel.tools.CharacterParser;
import com.ub.techexcel.tools.PinyinAddserviceComparator;
import com.ub.techexcel.tools.SiderIndex;
import com.ub.techexcel.view.SideBar;
import com.ub.techexcel.view.SideBar.OnTouchingLetterChangedListener;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.LoginGetListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class SelectUserActivity extends Activity implements OnClickListener {

    private LinearLayout backll;
    private SideBar sideBar;
    private TextView dialog;
    private PinyinAddserviceComparator pinyinComparator;
    private CharacterParser characterParser;
    private ListView listView;
    private MyAdapter myAdapter;
    private ProgressBar progressBar;
    private List<Customer> mList = new ArrayList<Customer>();
    private int schoolId;
    private boolean isMyschool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addservice_user);
        initView();

        schoolId = getIntent().getIntExtra("schoolId", -1);
        if (AppConfig.SchoolID == schoolId || schoolId == -1) {  // 是my school
            isMyschool = true;
        } else {
            isMyschool = false;
        }

        if (getIntent().getBooleanExtra("isDialogue", false)) {
            searchcontactThread();
        } else {
            searchcontactThread2();
        }


    }

    private void initView() {
        // TODO Auto-generated method stub
        backll = (LinearLayout) findViewById(R.id.backll);
        backll.setOnClickListener(this);
        sideBar = (SideBar) findViewById(R.id.friends_sidrbar);
        listView = (ListView) findViewById(R.id.friends_myfriends);

        dialog = (TextView) findViewById(R.id.friends_dialog);
        progressBar = (ProgressBar) findViewById(R.id.pb_contacts);
        progressBar.setVisibility(View.GONE);
        sideBar.setTextView(dialog);
        sideBar.select(-1);

        characterParser = new CharacterParser();
        pinyinComparator = new PinyinAddserviceComparator();

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                final Customer customer = myAdapter.getlist().get(arg2);
                AppConfig.isUpdateDialogue = true;
                if (getIntent().getBooleanExtra("isDialogue", false)) {
                    AppConfig.Name = customer.getName();
                    /*RongContext
                            .getInstance()
							.getUserInfoCache()
							.put(customer.getUBAOUserID(), 
									new UserInfo(customer.getUBAOUserID(),
											customer.getName(), null));*/
                    RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                        @Override
                        public UserInfo getUserInfo(String s) {
                            return new UserInfo(
                                    customer.getUBAOUserID(),
                                    customer.getName(),
                                    null);
                        }
                    }, true);
                    RongIM.getInstance().startPrivateChat(
                            SelectUserActivity.this, customer.getUBAOUserID(),
                            customer.getName());
                    finish();
                } else {
                    AppConfig.tempServiceBean.setCustomer(customer);
                    AppConfig.ISONRESUME = true;
                    finish();
                }

            }
        });

        listView.setOnScrollListener(new OnScrollListener() {

            private String first;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (myAdapter != null) {
                    if (myAdapter.getlist() != null
                            && myAdapter.getlist().size() > 0) {
                        first = myAdapter.getlist().get(firstVisibleItem)
                                .getSortLetters();
                        int index = SiderIndex.stringtoint(first);
                        sideBar.select(index);
                    } else {
                        sideBar.select(-1);
                    }
                }
            }
        });

        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if (myAdapter != null) {
                    int position = myAdapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        listView.setSelection(position);
                    }
                }
            }
        });

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SelectUserActivity");
        MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SelectUserActivity");
        MobclickAgent.onPause(this);
    }

    private void searchcontactThread() {
        mList.clear();
        final LoginGet loginGet = new LoginGet();
        loginGet.setLoginGetListener(new LoginGetListener() {
            @Override
            public void getMember(ArrayList<Customer> list) {
                // TODO Auto-generated method stub
                mList.addAll(list);
                for (Customer model : mList) {
                    String pinyin = characterParser.getPingYin(model.getName());
                    String sortString = pinyin.substring(0, 1).toUpperCase();
                    if (sortString.matches("[A-Z]")) {
                        model.setSortLetters(sortString.toUpperCase());
                    } else {
                        model.setSortLetters("#");
                    }
                }
                Collections.sort(mList, pinyinComparator);
                myAdapter = new MyAdapter(SelectUserActivity.this, mList);
                listView.setAdapter(myAdapter);
            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                // TODO Auto-generated method stub
                mList.clear();
                mList.addAll(list);
                Collections.sort(mList, pinyinComparator);
                myAdapter = new MyAdapter(SelectUserActivity.this, mList);
                listView.setAdapter(myAdapter);
//				loginGet.MemberRequest(getApplicationContext(), 0);
            }
        });
        loginGet.CustomerRequest(getApplicationContext());

    }

    private void searchcontactThread2() {
        mList.clear();
        if (isMyschool) {
            final LoginGet loginGet = new LoginGet();
            loginGet.setLoginGetListener(new LoginGetListener() {

                @Override
                public void getMember(ArrayList<Customer> list) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void getCustomer(ArrayList<Customer> list) {
                    // TODO Auto-generated method stub
                    mList.clear();
                    mList.addAll(list);
                    for (Customer model : mList) {
                        String pinyin = characterParser.getPingYin(model.getName());
                        String sortString = pinyin.substring(0, 1).toUpperCase();
                        if (sortString.matches("[A-Z]")) {
                            model.setSortLetters(sortString.toUpperCase());
                        } else {
                            model.setSortLetters("#");
                        }
                    }
                    Collections.sort(mList, pinyinComparator);
                    myAdapter = new MyAdapter(SelectUserActivity.this, mList);
                    listView.setAdapter(myAdapter);
                }
            });
            loginGet.CustomerRequest(getApplicationContext());
        } else {
            final LoginGet loginget = new LoginGet();
            loginget.setSchoolContactListener(new LoginGet.SchoolContactListener() {
                @Override
                public void getContact(ArrayList<Customer> list) {
                    mList.clear();
                    mList.addAll(list);
                    for (Customer model : mList) {
                        String pinyin = characterParser.getPingYin(model.getName());
                        String sortString = pinyin.substring(0, 1).toUpperCase();
                        if (sortString.matches("[A-Z]")) {
                            model.setSortLetters(sortString.toUpperCase());
                        } else {
                            model.setSortLetters("#");
                        }
                    }
                    Collections.sort(mList, pinyinComparator);
                    myAdapter = new MyAdapter(SelectUserActivity.this, mList);
                    listView.setAdapter(myAdapter);
                }
            });
            loginget.GetSchoolContact(getApplicationContext());
        }


    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.backll:
                finish();
                break;
            default:
                break;
        }
    }

}
