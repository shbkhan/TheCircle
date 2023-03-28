package com.shbkhan.thecircle.Model;

public class PostUploadModel {
    String postId, postUrl,postTime,postDesc,postedBy;


    public PostUploadModel() {
    }

    public PostUploadModel(String postId, String postUrl, String postTime, String postDesc,String postedBy) {
        this.postId = postId;
        this.postUrl = postUrl;
        this.postTime = postTime;
        this.postDesc = postDesc;
        this.postedBy = postedBy;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }
}
