package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.pc.ui.ChangePasswordActivity;
import com.kloudsync.techexcel.pc.ui.EffectiveActivity;
import com.kloudsync.techexcel.pc.ui.PersonalInfoActivity;
import com.kloudsync.techexcel.pc.ui.ProfessionalFieldActivity;
import com.kloudsync.techexcel.pc.ui.SelfDescriptionActivity;
import com.kloudsync.techexcel.personal.AboutActivity2;
import com.kloudsync.techexcel.personal.CreateOrganizationActivity;
import com.kloudsync.techexcel.personal.HelpCenterActivity;
import com.kloudsync.techexcel.personal.LanguageActivity;
import com.kloudsync.techexcel.personal.MyAccountActivity;
import com.kloudsync.techexcel.personal.MyCourseTemplateActivity;
import com.kloudsync.techexcel.personal.PersanalCollectionActivity;
import com.kloudsync.techexcel.personal.SchoolProfileActivity;
import com.kloudsync.techexcel.personal.TeacherProfileActivity;
import com.kloudsync.techexcel.school.SelectSchoolActivity;
import com.kloudsync.techexcel.start.LoginActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.SoftInputUtils;
import com.kloudsync.techexcel.ui.MainActivity;
import com.ub.service.activity.FinishedCourseActivity;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;


public class PersonalCenterFragment extends Fragment {

    private View view;
    private RelativeLayout rl_pc_portrait,
            rl_pc_professionalField, rl_pc_effective, rl_pc_password,
            rl_pc_loginout, rl_pc_language, rl_pc_klassroomID;
    private RelativeLayout rl_pc_about;
    private RelativeLayout rl_pc_hc;
    private RelativeLayout rl_pc_sk;
    private RelativeLayout rl_contacts_portrait;
    private RelativeLayout rl_cn_account;

    private RelativeLayout ll_pc_integral, ll_pc_collection,
            ll_pc_publish_article;
    private RelativeLayout rl_school_name, rl_teacher_profile, rl_my_account;
    private TextView tv_pc_account_name, tv_pc_account_level,
            pi_goodatfield, pc_tv_date, pc_tv_language;
    private TextView tv_roomid;
    private TextView tv_switch;
    private TextView tv_sname;
    private TextView tv_teacher_profile;
    private TextView tv_pc_hc;
    private SharedPreferences sharedPreferences;
    private String account_name;
    private String account_number;
    private String AvatarUrl = "";
    private String memberPoints = "0";
    private String expirationDate = "";
    private String SkilledFields;
    private String ArticleCount;
    public ImageLoader imageLoader;
    private SimpleDraweeView contacts_portrait;

    private AlertDialog dialog = null;
    private String updateClassroomId;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1006:
                    int retcode = (int) msg.obj;
                    if (retcode == -1) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.classroomid_occupied), Toast.LENGTH_LONG).show();
                    } else {
                        GetClassRoomID(updateClassroomId);
                    }
                    break;
                case 0x1007:
                    String retdate = (String) msg.obj;
                    if (retdate.equals(AppConfig.ClassRoomID)) {
                    } else {
                        AppConfig.ClassRoomID = retdate;
                        tv_roomid.setText(AppConfig.ClassRoomID.replaceAll("-", ""));
                    }
                    if(dialog!=null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.personal_center, container, false);
        return view;
    }

    private void GetSchoolInfo() {
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,
                getActivity().MODE_PRIVATE);
        int SchoolId = sharedPreferences.getInt("SchoolID", -1);
        String schoolName = sharedPreferences.getString("SchoolName", null);
        /*if (-1 == SchoolId || SchoolId == AppConfig.SchoolID) {
            tv_sname.setText(getResources().getString(R.string.My_School));
        } else {
            tv_sname.setText(schoolName);
        }*/
        tv_sname.setText(schoolName);
    }

    boolean isFirst = true;

    private boolean isKVisibleToUser;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isFirst && isVisibleToUser) {
            isKVisibleToUser = isVisibleToUser;
            isFirst = false;
            initView();
            getPersonInfo();
        }
        if (isKVisibleToUser) {
            if (AppConfig.HASUPDATAINFO == true) {
                account_name = sharedPreferences.getString("Name", "");

                // account_number = sharedPreferences.getString("telephone",
                // "");

                tv_pc_account_name.setText(account_name);
                // tv_pc_account_number.setText(account_number);
                getPersonInfo();
                AppConfig.HASUPDATAINFO = false;
            }
        }
    }

    @Override
    public void onResume() {
        Log.e("qqqqqqq", AppConfig.isUpdateDialogue + "");
        if (isKVisibleToUser) {
            if (AppConfig.HASUPDATAINFO == true) {
                account_name = sharedPreferences.getString("Name", "");

                // account_number = sharedPreferences.getString("telephone",
                // "");
                tv_pc_account_name.setText(account_name);
                // tv_pc_account_number.setText(account_number);
                getPersonInfo();
                AppConfig.HASUPDATAINFO = false;
            }
            if (AppConfig.HASUPDATESUMMERY) {
                getPersonInfo();
                AppConfig.HASUPDATESUMMERY = false;
            }
        }
        if (tv_sname != null) {
            GetSchoolInfo();
        }
        super.onResume();
    }

    private void initView() {
        rl_pc_portrait = (RelativeLayout) view
                .findViewById(R.id.rl_pc_portrait);
        ll_pc_publish_article = (RelativeLayout) view
                .findViewById(R.id.ll_pc_publish_article);
        ll_pc_collection = (RelativeLayout) view
                .findViewById(R.id.ll_pc_collection);
        // rl_pc_description = (RelativeLayout) view
        // .findViewById(R.id.rl_pc_description);
        ll_pc_integral = (RelativeLayout) view.findViewById(R.id.ll_pc_integral);
        rl_school_name = (RelativeLayout) view.findViewById(R.id.rl_school_name);
        rl_teacher_profile = (RelativeLayout) view.findViewById(R.id.rl_teacher_profile);
        rl_my_account = (RelativeLayout) view.findViewById(R.id.rl_my_account);
        rl_cn_account = (RelativeLayout) view.findViewById(R.id.rl_cn_account);
        ll_pc_integral = (RelativeLayout) view.findViewById(R.id.ll_pc_integral);
        pi_goodatfield = (TextView) view.findViewById(R.id.pi_goodatfield);
        rl_pc_professionalField = (RelativeLayout) view
                .findViewById(R.id.rl_pc_professionalField);
        rl_pc_effective = (RelativeLayout) view
                .findViewById(R.id.rl_pc_effective);
        rl_pc_password = (RelativeLayout) view
                .findViewById(R.id.rl_pc_password);
        rl_pc_loginout = (RelativeLayout) view
                .findViewById(R.id.rl_pc_loginout);
        rl_pc_language = (RelativeLayout) view
                .findViewById(R.id.rl_pc_language);
        rl_pc_klassroomID = (RelativeLayout) view
                .findViewById(R.id.rl_pc_klassroomID);
        rl_pc_about = (RelativeLayout) view
                .findViewById(R.id.rl_pc_about);
        rl_pc_sk = (RelativeLayout) view
                .findViewById(R.id.rl_pc_sk);
        rl_pc_hc = (RelativeLayout) view
                .findViewById(R.id.rl_pc_hc);
        rl_contacts_portrait = (RelativeLayout) view
                .findViewById(R.id.rl_contacts_portrait);
        pc_tv_date = (TextView) view.findViewById(R.id.pc_tv_date);
        pc_tv_language = (TextView) view.findViewById(R.id.pc_tv_language);

        contacts_portrait = (SimpleDraweeView) view
                .findViewById(R.id.contacts_portrait);

        tv_pc_account_name = (TextView) view
                .findViewById(R.id.tv_pc_account_name);
        tv_pc_account_level = (TextView) view
                .findViewById(R.id.tv_pc_account_level);
        tv_roomid = (TextView) view
                .findViewById(R.id.tv_roomid);
        tv_sname = (TextView) view.findViewById(R.id.tv_sname);
        tv_switch = (TextView) view.findViewById(R.id.tv_switch);
        tv_teacher_profile = (TextView) view.findViewById(R.id.tv_teacher_profile);
        tv_pc_hc = (TextView) view.findViewById(R.id.tv_pc_hc);

        if (!TextUtils.isEmpty(AppConfig.ClassRoomID)) {
            tv_roomid.setText(AppConfig.ClassRoomID.replaceAll("-", ""));
        }


        sharedPreferences = getActivity().getSharedPreferences(
                AppConfig.LOGININFO, Context.MODE_PRIVATE);

        account_name = sharedPreferences.getString("Name", "");
        // account_number = sharedPreferences.getString("telephone", "");

        tv_pc_account_name.setText(account_name);
        // tv_pc_account_number.setText(account_number);

        ShowLanguage();

        GetSchoolInfo();

        ll_pc_publish_article.setOnClickListener(new myOnClick());
        ll_pc_collection.setOnClickListener(new myOnClick());
        ll_pc_integral.setOnClickListener(new myOnClick());
        // tv_professionalField.setOnClickListener(new myOnClick());
        // rl_pc_description.setOnClickListener(new myOnClick());
        rl_pc_portrait.setOnClickListener(new myOnClick());
        rl_pc_professionalField.setOnClickListener(new myOnClick());
        rl_pc_effective.setOnClickListener(new myOnClick());
//		img_pc_status.setOnClickListener(new myOnClick());
        rl_pc_password.setOnClickListener(new myOnClick());
        rl_pc_loginout.setOnClickListener(new myOnClick());
        rl_pc_language.setOnClickListener(new myOnClick());
        rl_pc_klassroomID.setOnClickListener(new myOnClick());
        rl_school_name.setOnClickListener(new myOnClick());
        rl_teacher_profile.setOnClickListener(new myOnClick());
        rl_my_account.setOnClickListener(new myOnClick());
        rl_pc_about.setOnClickListener(new myOnClick());
        rl_pc_hc.setOnClickListener(new myOnClick());
        rl_pc_sk.setOnClickListener(new myOnClick());
        rl_contacts_portrait.setOnClickListener(new myOnClick());
        rl_cn_account.setOnClickListener(new myOnClick());
        contacts_portrait.setOnClickListener(new myOnClick());
        tv_switch.setOnClickListener(new myOnClick());
    }

    private void ShowLanguage() {
        if (AppConfig.LANGUAGEID == 1) {
            pc_tv_language.setText(getResources().getString(R.string.English));
        } else if (AppConfig.LANGUAGEID == 2) {
            pc_tv_language.setText(getResources().getString(R.string.Chinese));
        }
    }

    private class myOnClick implements OnClickListener {
        Intent intent = new Intent();

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_pc_klassroomID:
                    ModifyKlassRoomID();
                    break;
                case R.id.rl_contacts_portrait:
                    intent = new Intent(getActivity(), PersonalInfoActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_school_name:
                    intent = new Intent(getActivity(), SchoolProfileActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_cn_account:
                    intent = new Intent(getActivity(), CreateOrganizationActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_teacher_profile:
                    intent = new Intent(getActivity(), TeacherProfileActivity.class);
//                    startActivity(intent);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            new Pair(tv_teacher_profile, TeacherProfileActivity.汗));
                    ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                    break;
                case R.id.rl_my_account:
                    intent = new Intent(getActivity(), MyAccountActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_pc_portrait:
                    intent = new Intent(getActivity(),
                            SelfDescriptionActivity.class);
                    intent.putExtra("topname", "description");
                    startActivity(intent);
                    break;
                case R.id.ll_pc_integral:
                    intent = new Intent(getActivity(), FinishedCourseActivity.class);
                    intent.putExtra("memberPoints", memberPoints);
                    startActivity(intent);
                    break;
                case R.id.ll_pc_publish_article:
                    intent = new Intent(getActivity(),
                            MyCourseTemplateActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ll_pc_collection:
                    intent = new Intent(getActivity(), PersanalCollectionActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_pc_language:
                    intent = new Intent(getActivity(), LanguageActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_pc_professionalField:
                    intent = new Intent(getActivity(),
                            ProfessionalFieldActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_pc_effective:
                    intent = new Intent(getActivity(), EffectiveActivity.class);
                    Log.e("effective", expirationDate + "");
                    intent.putExtra("effective", expirationDate);
                    startActivity(intent);
                    break;
                case R.id.tv_switch:
                    intent = new Intent(getActivity(), SelectSchoolActivity.class);
                    startActivity(intent);
                    break;
            /*case img_pc_status:
                if (AppConfig.UserType != 1) {
					DialogVip(getActivity());
				} else {
					DialogSvip(getActivity());
				}
				break;*/
                case R.id.rl_pc_password:
                    intent = new Intent(getActivity(), ChangePasswordActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_pc_about:
                    GoToAbout();
                    break;
                case R.id.rl_pc_hc:
                    GoToHC();
                    break;
                case R.id.rl_pc_sk:
                    break;
                case R.id.rl_pc_loginout:
                    LoginoutDialog(getActivity());
                    break;
                default:
                    break;
            }

        }

    }

    private void GoToHC() {

        intent = new Intent(getActivity(), HelpCenterActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                new Pair(tv_pc_hc, HelpCenterActivity.汗));
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }

    private void GoToAbout() {
        intent = new Intent(getActivity(), AboutActivity2.class);
        startActivity(intent);
    }

    boolean flag_keybo;

    private void ModifyKlassRoomID() {

        final EditText et = new EditText(getActivity());
//        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setHint(getResources().getString(R.string.Input_ID));
        et.setGravity(Gravity.CENTER);
        et.setTextColor(getResources().getColor(R.color.Turquoise));

        if (!TextUtils.isEmpty(AppConfig.ClassRoomID)) {
            et.setText(AppConfig.ClassRoomID.replaceAll("-", ""));
            et.setSelection(AppConfig.ClassRoomID.replaceAll("-", "").length());
        }
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setTitle(getResources().getString(R.string.Modify_ID))
                .setView(et)
                .setPositiveButton(getResources().getString(R.string.Yes), null)
                /*.setPositiveButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateClassroomId = et.getText().toString();

                        if (updateClassroomId.equals(AppConfig.ClassRoomID)) {
                            dialog.dismiss();
                        } else {
                            UpdateClassRoomID(updateClassroomId);
                        }
                    }
                })*/
                .setNegativeButton(getResources().getString(R.string.No), null);
        dialog = build.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogs) {
                // 隐藏软键盘
//                SoftInputUtils.hideSoftInput(getActivity());
                InputMethodManager inputMgr = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (flag_keybo)
                    inputMgr.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
            }
        });
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateClassroomId = et.getText().toString();

                if (updateClassroomId.equals(AppConfig.ClassRoomID)) {
                    dialog.dismiss();
                } else {
                    UpdateClassRoomID(updateClassroomId);
                }
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 显示软键盘
                SoftInputUtils.showSoftInput(getActivity());
            }
        }, 100);
        et.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            //当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight = getActivity().getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;
                Log.e("Keyboard Size", "Size: " + heightDifference + ":" + r.top + "," + r.bottom + "," + r.left + "," + r.right);
                flag_keybo = (heightDifference < screenHeight / 3 ? true : false);
            }

        });
    }

    private void UpdateClassRoomID(final String classRoomId) {

        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{3,12}$";

        if (TextUtils.isEmpty(classRoomId) || (classRoomId.length() < 3)) {
            Toast.makeText(getActivity(),
                    "Room ID must be greater than 3 digits in length",
                    Toast.LENGTH_SHORT).show();

        } else if (!classRoomId.matches(regex)) {
            Toast.makeText(getActivity(),
                    "Room ID must contain numbers and letters. Room ID must be greater than 3 digits in length ",
                    Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject js = new JSONObject();
                        js.put("classroomID", classRoomId);
                        JSONObject jsonObject = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/UpdateClassRoomID?classRoomID=" + classRoomId, js);
                        Log.e("getClassRoomLessonID2", jsonObject.toString()); //{"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":2477}
                        int retCode = jsonObject.getInt("RetCode");
                        Message msg = Message.obtain();
                        msg.what = 0x1006;
                        msg.obj = retCode;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void GetClassRoomID(final String classRoomId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/GetClassRoomID");
                    Log.e("getClassRoomLessonID2", jsonObject.toString()); //{"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":2477}
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            String retdate = jsonObject.getString("RetData");
                            Message msg = Message.obtain();
                            msg.what = 0x1007;
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

    private void logout() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                JSONObject jsonObject = ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Logout");
                Log.e("dk", jsonObject.toString());
                formatlogout(jsonObject);
            }

        }).start();
    }

    private Intent intent;

    private void formatlogout(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        try {
            int retCode = jsonObject.getInt("RetCode");
            String error = jsonObject.getString("ErrorMessage");
            switch (retCode) {
                case 0:
                    sharedPreferences = getActivity().getSharedPreferences(
                            AppConfig.LOGININFO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLogIn", false);
                    editor.commit();
                    AppConfig.isUpdateCustomer = false;
                    AppConfig.isUpdateDialogue = false;
                    AppConfig.HASUPDATAINFO = false;
                    RongIM.getInstance().disconnect();
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    MainActivity.instance.finish();
                    break;
                case -1500:
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void getPersonInfo() {
        /*UserGet userget = new UserGet();
        Log.e("xxxx", "1");
        userget.setDetailListener(new UserGet.DetailListener() {

            @Override
            public void getUser(Customer user) {
                AvatarUrl = user.getUrl() + "";
                memberPoints = "0";
                expirationDate = user.getExpirationDate();
                SkilledFields = user.getSkilledFields();
                ArticleCount = "0";

                tv_pc_article.setText(ArticleCount);
                pc_tv_date.setText(getResources().getString(R.string.to) + " " + expirationDate);
                pi_goodatfield.setText(SkilledFields);
                tv_pc_integral.setText(memberPoints);
                tv_pc_account_level.setText(user.getPhone());
                downloadAttachment();

            }

            @Override
            public void getMember(MemberBean member) {
				*//*AvatarUrl = member.getAvatarUrl() + "";
                memberPoints = member.getMemberPoints();
				expirationDate = member.getExpirationDate();
				SkilledFields = member.getSkilledFields();
				ArticleCount = member.getArticleCount();

				tv_pc_article.setText(ArticleCount);
				pc_tv_date.setText("至"+expirationDate);
				pi_goodatfield.setText(SkilledFields);
				tv_pc_integral.setText(memberPoints);
                tv_pc_account_level.setText(member.getPhone());
				downloadAttachment();*//*
            }
        });
        userget.CustomerDetailRequest(getActivity(), AppConfig.UserID);*/


        LoginGet loginget = new LoginGet();
        loginget.setDetailGetListener(new LoginGet.DetailGetListener() {

            @Override
            public void getUser(Customer user) {
                AvatarUrl = user.getUrl() + "";
                memberPoints = "0";
                expirationDate = user.getExpirationDate();
                SkilledFields = user.getSkilledFields();
                account_name = user.getName();
                ArticleCount = "0";

//                tv_pc_article.setText(ArticleCount);
                pc_tv_date.setText("至" + expirationDate);
                pi_goodatfield.setText(SkilledFields);
                tv_pc_account_name.setText(account_name);
//                tv_pc_integral.setText(memberPoints);
                tv_pc_account_level.setText(user.getPhone());
                downloadAttachment();
            }

            @Override
            public void getMember(Customer member) {
                // TODO Auto-generated method stub

            }
        });
        loginget.CustomerDetailRequest(getActivity(), AppConfig.UserID);
    }

    public void downloadAttachment() {
        Uri imageUri = Uri.parse(AvatarUrl);
        contacts_portrait.setImageURI(imageUri);
    }

    private AlertDialog builder;

    public void DialogVip(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);

        View windov = inflater.inflate(R.layout.pc_recharge_dialogvip, null);

        windov.findViewById(R.id.tv_pc_apply).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });
        windov.findViewById(R.id.img_pc_close).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });
        builder = new AlertDialog.Builder(context).show();
        Window dialogWindow = builder.getWindow();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth()); // 宽度设置为屏幕的0.65
        p.height = (int) (d.getHeight() * 0.8);
        dialogWindow.setAttributes(p);
        builder.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        builder.setContentView(windov);
    }

    public void DialogSvip(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);

        View windov = inflater.inflate(R.layout.pc_recharge_dialogsvip, null);

        LinearLayout tv_pc_apply = (LinearLayout) windov
                .findViewById(R.id.tv_pc_apply);
        tv_pc_apply.setVisibility(View.GONE);
        LinearLayout ll_pc_svip = (LinearLayout) windov
                .findViewById(R.id.ll_pc_svip);
        ll_pc_svip.setVisibility(View.GONE);

        windov.findViewById(R.id.tv_pc_apply).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });
        windov.findViewById(R.id.img_pc_close).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });
        builder = new AlertDialog.Builder(context).show();
        Window dialogWindow = builder.getWindow();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth()); // 宽度设置为屏幕的0.65
        p.height = (int) (d.getHeight() * 0.8);
        dialogWindow.setAttributes(p);
        builder.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        builder.setContentView(windov);
    }

    public void LoginoutDialog(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);

        View windov = inflater.inflate(R.layout.pc_loginout_dialog, null);

        windov.findViewById(R.id.pc_loginout_dialog_cancel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });
        windov.findViewById(R.id.pc_loginout_dialog_report).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        logout();
                    }
                });
        builder = new AlertDialog.Builder(context).show();
        Window dialogWindow = builder.getWindow();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
//        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
//        p.height = (int) (d.getHeight() * 0.3);
        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(p);
        builder.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        builder.setContentView(windov);
    }
}
