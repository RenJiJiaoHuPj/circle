package com.zzt.circle.app.entity;

/**
 * Created by zzt on 15-5-18.
 */
public class UserEntity {
    private String account;
    private String nickname;
    private String avatarURL;
    private int gender;

    public UserEntity(String account, String nickname, int gender, String avatarURL) {
        this.account = account;
        this.nickname = nickname;
        this.avatarURL = avatarURL;
        this.gender = gender;
    }

    public String getAccount() {
        return account;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public int getGender() {
        return gender;
    }
}
