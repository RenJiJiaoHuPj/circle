package com.zzt.circle.app.entity;

/**
 * Created by zzt on 15-5-18.
 */
public class ImageMessageEntity {
    private int msgID;
    private String avatarURL;
    private String nickname;
    private long postTime;
    private String photoURL;
    private String textDescription;
//    private String soundDescriptionURL;


    public ImageMessageEntity(int msgID, String avatarURL, String nickname, long postTime, String photoURL, String textDescription) {
        this.msgID = msgID;
        this.avatarURL = avatarURL;
        this.nickname = nickname;
        this.postTime = postTime;
        this.photoURL = photoURL;
        this.textDescription = textDescription;
    }

    public int getMsgID() {
        return msgID;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public String getNickname() {
        return nickname;
    }

    public long getPostTime() {
        return postTime;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getTextDescription() {
        return textDescription;
    }
}