package com.ub.techexcel.tools;

import android.util.Log;

import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/8/3.
 */

public class ServiceTool implements Runnable {

    private int schoolId = -1;
    private List<ServiceBean> mList = new ArrayList<>();
    private int roleId;
    private ArrayList<Customer> custometList = new ArrayList<Customer>();
    private int type;

    public ServiceTool(int i, List<ServiceBean> mList, ArrayList<Customer> custometList, int schoolId) {
        this.mList = mList;
        this.schoolId = schoolId;
        this.custometList = custometList;
        switch (i) {
            case 0:
                roleId = 2;
                type = 1;
                break;
            case 1:
                roleId = 2;
                type = 2;
                break;
            case 2:
                roleId = 2;
                type = 3;
                break;
        }
    }


    @Override
    public void run() {
        JSONObject returnJson = ConnectService
                .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                        + "Lesson/List?pageIndex=0&pageSize=20&roleID=" + roleId + "&schoolID=" + schoolId + "&type=" + type);
        Log.e("--------------uuuuuuuuu", returnJson.toString() + "    " +
                "  " + AppConfig.URL_PUBLIC
                + "Lesson/List?pageIndex=0&pageSize=20&roleID=" + roleId + "&schoolID=" + schoolId + "&type=" + type);
        formatServiceData(returnJson, roleId);
    }

    private void formatServiceData(JSONObject returnJson, int roleId) {
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retdata = returnJson.getJSONArray("RetData");
                    for (int i = 0; i < retdata.length(); i++) {
                        JSONObject service = retdata.getJSONObject(i);
                        ServiceBean bean = new ServiceBean();
                        int statusID = service.getInt("Status");
                        bean.setStatusID(statusID);
                        bean.setId(service.getInt("LessonID"));
                        bean.setRoleinlesson(roleId);
                        bean.setPlanedEndDate(service.getString("PlanedEndDate"));
                        bean.setPlanedStartDate(service.getString("PlanedStartDate"));
                        bean.setCourseName(service.getString("CourseName"));
                        bean.setUserName(service.getString("StudentNames"));
                        bean.setName(service.getString("Title"));
                        bean.setTeacherName(service.getString("TeacherNames"));
                        bean.setFinished(service.getInt("IsFinished") == 1 ? true : false);
                        mList.add(bean);
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
