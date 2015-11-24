package com.zzt.circle.app.entity;

/**
 * Created by zzt on 15-6-14.
 */
public class CommentEntity {
    private int commentID;
    private String nickname;
    private String avatar_url;
    private String content;
    private long postTime;

    public CommentEntity(int commentID, String nickname, String avatar_url, String content, long postTime) {
        this.commentID = commentID;
        this.nickname = nickname;
        this.avatar_url = avatar_url;
        this.content = content;
        this.postTime = postTime;
    }

    public int getCommentID() {
        return commentID;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public String getContent() {
        return content;
    }

    public long getPostTime() {
        return postTime;
    }
}
