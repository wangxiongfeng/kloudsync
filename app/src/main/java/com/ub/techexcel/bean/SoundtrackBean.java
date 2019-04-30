package com.ub.techexcel.bean;

import com.kloudsync.techexcel.info.Favorite;

public class SoundtrackBean {


    private int soundtrackID;
    private String title;
    private String userID;
    private String userName;
    private String avatarUrl;
    private String duration;
    private String attachmentId;

    private boolean isCheck = false;
    private boolean isHidden = false;

    private int newAudioAttachmentID;
    private int selectedAudioAttachmentID;
    private int backgroudMusicAttachmentID;

    private Favorite newAudioInfo;
    private Favorite selectedAudioInfo;
    private Favorite backgroudMusicInfo;

    private String backgroudMusicTitle,selectedAudioTitle,newAudioTitle;
    private String createdDate;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getBackgroudMusicTitle() {
        return backgroudMusicTitle;
    }

    public void setBackgroudMusicTitle(String backgroudMusicTitle) {
        this.backgroudMusicTitle = backgroudMusicTitle;
    }


    public String getSelectedAudioTitle() {
        return selectedAudioTitle;
    }

    public void setSelectedAudioTitle(String selectedAudioTitle) {
        this.selectedAudioTitle = selectedAudioTitle;
    }

    public String getNewAudioTitle() {
        return newAudioTitle;
    }

    public void setNewAudioTitle(String newAudioTitle) {
        this.newAudioTitle = newAudioTitle;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public int getNewAudioAttachmentID() {
        return newAudioAttachmentID;
    }

    public void setNewAudioAttachmentID(int newAudioAttachmentID) {
        this.newAudioAttachmentID = newAudioAttachmentID;
    }

    public int getSelectedAudioAttachmentID() {
        return selectedAudioAttachmentID;
    }

    public void setSelectedAudioAttachmentID(int selectedAudioAttachmentID) {
        this.selectedAudioAttachmentID = selectedAudioAttachmentID;
    }

    public int getBackgroudMusicAttachmentID() {
        return backgroudMusicAttachmentID;
    }

    public void setBackgroudMusicAttachmentID(int backgroudMusicAttachmentID) {
        this.backgroudMusicAttachmentID = backgroudMusicAttachmentID;
    }

    public Favorite getNewAudioInfo() {
        return newAudioInfo;
    }

    public void setNewAudioInfo(Favorite newAudioInfo) {
        this.newAudioInfo = newAudioInfo;
    }

    public Favorite getSelectedAudioInfo() {
        return selectedAudioInfo;
    }

    public void setSelectedAudioInfo(Favorite selectedAudioInfo) {
        this.selectedAudioInfo = selectedAudioInfo;
    }

    public Favorite getBackgroudMusicInfo() {
        return backgroudMusicInfo;
    }

    public void setBackgroudMusicInfo(Favorite backgroudMusicInfo) {
        this.backgroudMusicInfo = backgroudMusicInfo;
    }

    public int getSoundtrackID() {
        return soundtrackID;
    }

    public void setSoundtrackID(int soundtrackID) {
        this.soundtrackID = soundtrackID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    private boolean havePresenter;

    public void setHavePresenter(boolean havePresenter) {
        this.havePresenter = havePresenter;
    }

    public boolean isHavePresenter() {
        return havePresenter;
    }
}
