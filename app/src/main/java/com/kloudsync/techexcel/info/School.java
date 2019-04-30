package com.kloudsync.techexcel.info;

import com.ub.kloudsync.activity.TeamSpaceBean;

public class School {
    private int SchoolID;
    private String SchoolName;
    private TeamSpaceBean teamSpaceBean;


    public School() {
    }

    public int getSchoolID() {
        return SchoolID;
    }

    public void setSchoolID(int schoolID) {
        SchoolID = schoolID;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public TeamSpaceBean getTeamSpaceBean() {
        return teamSpaceBean;
    }

    public void setTeamSpaceBean(TeamSpaceBean teamSpaceBean) {
        this.teamSpaceBean = teamSpaceBean;
    }
}
