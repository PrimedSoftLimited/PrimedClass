package com.primedsoft.primedclass.Model;

public class TeachingModel {

    private String dateStarted,topicTaught,dateCompleted,completedLevel;

    public TeachingModel() {
    }

    public String getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(String dateStarted) {
        this.dateStarted = dateStarted;
    }

    public String getTopicTaught() {
        return topicTaught;
    }

    public void setTopicTaught(String topicTaught) {
        this.topicTaught = topicTaught;
    }

    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getCompletedLevel() {
        return completedLevel;
    }

    public void setCompletedLevel(String completedLevel) {
        this.completedLevel = completedLevel;
    }
}
