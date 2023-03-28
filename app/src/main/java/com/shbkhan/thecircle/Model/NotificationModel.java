package com.shbkhan.thecircle.Model;

public class NotificationModel {
    String reactionType,ReactionBy,reactionOn;

    public NotificationModel() {
    }

    public NotificationModel(String reactionType, String reactionBy, String reactionOn) {
        this.reactionType = reactionType;
        ReactionBy = reactionBy;
        this.reactionOn = reactionOn;
    }

    public String getReactionType() {
        return reactionType;
    }

    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }

    public String getReactionBy() {
        return ReactionBy;
    }

    public void setReactionBy(String reactionBy) {
        ReactionBy = reactionBy;
    }

    public String getReactionOn() {
        return reactionOn;
    }

    public void setReactionOn(String reactionOn) {
        this.reactionOn = reactionOn;
    }
}
