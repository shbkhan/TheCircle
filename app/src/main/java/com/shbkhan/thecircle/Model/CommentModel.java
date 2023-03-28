package com.shbkhan.thecircle.Model;

public class CommentModel {
    String commentText,commentedBy;

    public CommentModel() {
    }

    public CommentModel(String commentText, String commentedBy) {
        this.commentText = commentText;
        this.commentedBy = commentedBy;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }
}
