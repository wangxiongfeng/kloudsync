package com.ub.techexcel.bean;

import java.io.Serializable;

public class LineItem implements Serializable {
    /**
     * 解决方案具体信息(片段)
     */
    private static final long serialVersionUID = 0x110;
    private int incidentID;
    private int eventID;
    private String eventName;
    private String fileName;
    private String description;
    private int itemTypeID;
    private int checkOption; // Optional No = 0, Optional Yes=1, Mandatory=2

    private String url;
    private String attachmentID;  //itemId
    private String attachmentID2;
    private boolean isSelect;

    private boolean isHtml5;
    private String blankPageNumber;
    private String CreatedBy;
    private String CreatedByAvatar;

    private int progress;
    private int flag;



    private int topicId;

    private int syncRoomCount;

    private String createdDate;
    private String newPath;
    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public String getCreatedBy() {
        return CreatedBy == null ? "" : CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedByAvatar() {
        return CreatedByAvatar == null ? "" : CreatedByAvatar;
    }

    public void setCreatedByAvatar(String createdByAvatar) {
        CreatedByAvatar = createdByAvatar;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getSyncRoomCount() {
        return syncRoomCount;
    }

    public void setSyncRoomCount(int syncRoomCount) {
        this.syncRoomCount = syncRoomCount;
    }

    public String getAttachmentID2() {
        return attachmentID2;
    }

    public void setAttachmentID2(String attachmentID2) {
        this.attachmentID2 = attachmentID2;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getBlankPageNumber() {
        return blankPageNumber;
    }

    public void setBlankPageNumber(String blankPageNumber) {
        this.blankPageNumber = blankPageNumber;
    }

    public boolean isHtml5() {
        return isHtml5;
    }

    public void setHtml5(boolean html5) {
        isHtml5 = html5;
    }


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getAttachmentID() {
        return attachmentID;
    }

    public void setAttachmentID(String attachmentID) {
        this.attachmentID = attachmentID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIncidentID() {
        return incidentID;
    }

    public void setIncidentID(int incidentID) {
        this.incidentID = incidentID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getItemTypeID() {
        return itemTypeID;
    }

    public void setItemTypeID(int itemTypeID) {
        this.itemTypeID = itemTypeID;
    }

    public int getCheckOption() {
        return checkOption;
    }

    public void setCheckOption(int checkOption) {
        this.checkOption = checkOption;
    }

}
