package com.ub.kloudsync.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeamSpaceBean implements Serializable {

    private int itemID;
    private String name;
    private int companyID;
    private int type;
    private int parentID;
    private String createdDate;
    private String createdByName;
    private int attachmentCount;
    private int TopicType;
    private int memberCount;
    private int syncRoomCount;

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getSyncRoomCount() {
        return syncRoomCount;
    }

    public void setSyncRoomCount(int syncRoomCount) {
        this.syncRoomCount = syncRoomCount;
    }

    public int getTopicType() {
        return TopicType;
    }

    public void setTopicType(int topicType) {
        TopicType = topicType;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    private List<TeamSpaceBean>  spaceList=new ArrayList<>();

    private List<TeamUser> memberList=new ArrayList<>();

    public List<TeamUser> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<TeamUser> memberList) {
        this.memberList = memberList;
    }

    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public List<TeamSpaceBean> getSpaceList() {
        return spaceList;
    }

    public void setSpaceList(List<TeamSpaceBean> spaceList) {
        this.spaceList = spaceList;
    }
}
