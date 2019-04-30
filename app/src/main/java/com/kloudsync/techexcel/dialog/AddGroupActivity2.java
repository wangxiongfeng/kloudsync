package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.GroupAdapter;
import com.kloudsync.techexcel.help.SideBar;
import com.kloudsync.techexcel.help.SideBar.OnTouchingLetterChangedListener;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity2 extends Activity {

    private TextView tv_back, tv_sure;
    private EditText et_search;
    private ListView lv_group;
    private SideBar sidebar;

    private ArrayList<Customer> eList = new ArrayList<Customer>();
    private ArrayList<Customer> mlist = new ArrayList<Customer>();
    private GroupAdapter gadapter;

    private List<String> clist = new ArrayList<String>();

    private InputMethodManager inputManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
//		isAddGroup = getIntent().getBooleanExtra("isAddGroup", false);
        initView();
    }


    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_sure = (TextView) findViewById(R.id.tv_sure);
        et_search = (EditText) findViewById(R.id.et_search);
        lv_group = (ListView) findViewById(R.id.lv_group);
        sidebar = (SideBar) findViewById(R.id.sidebar);

        getData();
        getSide();
        editGroup();

        tv_back.setOnClickListener(new myOnClick());
        tv_sure.setOnClickListener(new myOnClick());

    }

    private void editGroup() {
        inputManager = (InputMethodManager) et_search
                .getContext().getSystemService(getApplication().INPUT_METHOD_SERVICE);
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                gadapter.SetSelected(false);
                eList = new ArrayList<Customer>();
                for (int i = 0; i < mlist.size(); i++) {
                    Customer cus = mlist.get(i);
                    String name = et_search.getText().toString();
                    String getName = cus.getName().toLowerCase();//转小写
                    String nameb = name.toLowerCase();//转小写
                    if (getName.contains(nameb.toString())
                            && name.length() > 0) {
                        Customer customer;
                        customer = cus;
                        eList.add(customer);
                    }
                }
                if (et_search.length() != 0) {
                    gadapter = new GroupAdapter(getApplicationContext(), eList);
                } else {
                    gadapter = new GroupAdapter(getApplicationContext(), mlist);
                }
                lv_group.setAdapter(gadapter);

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

        });
    }

    private void getData() {
        final LoginGet loginget = new LoginGet();

//		loginget.CustomerRequest(getApplicationContext());

        loginget.setSchoolContactListener(new LoginGet.SchoolContactListener() {
            @Override
            public void getContact(ArrayList<Customer> list) {
                mlist = new ArrayList<Customer>();
                mlist.addAll(list);

                gadapter = new GroupAdapter(AddGroupActivity2.this, mlist);
                lv_group.setAdapter(gadapter);
                lv_group.setOnItemClickListener(new myOnItem());
            }
        });
        loginget.GetSchoolContact(getApplicationContext());
    }

    private void getSide() {
        sidebar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position;
                position = SideBarSortHelp.getPositionForSection(mlist,
                        s.charAt(0));
                if (position != -1) {
                    lv_group.setSelection(position);
                } else {
                    lv_group
                            .setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                }
            }
        });

    }

    private class myOnItem implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            gadapter.SetSelected(true);
            Customer cus;
            if (et_search.length() != 0) {
                cus = eList.get(position);
                for (int i = 0; i < mlist.size(); i++) {
                    if (cus.getUserID().equals(mlist.get(i).getUserID())) {
                        if (!cus.isHasSelected()) {
                            if (cus.isSelected()) {
                                mlist.get(i).setSelected(false);
                            } else {
                                mlist.get(i).setSelected(true);
                            }
                        }
                        break;
                    }
                }
                gadapter.updateListView(eList);
            } else {
                cus = mlist.get(position);
                if (!cus.isHasSelected()) {
                    if (cus.isSelected()) {
                        mlist.get(position).setSelected(false);
                    } else {
                        mlist.get(position).setSelected(true);
                    }
                }
                gadapter.updateListView(mlist);
            }

        }

    }

    protected class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.tv_sure:
                    getChatGroup();
                    break;

                default:
                    break;
            }

        }

    }


    private void getChatGroup() {
        int size = 0;
        List<Customer> ll = new ArrayList<>();
        for (int i = 0; i < mlist.size(); i++) {
            Customer cus = mlist.get(i);
            if (cus.isSelected()) {
                ll.add(cus);
            }
        }
        EventBus.getDefault().post(ll);
        finish();

    }


}
