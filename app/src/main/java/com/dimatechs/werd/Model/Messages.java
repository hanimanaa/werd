package com.dimatechs.werd.Model;

public class Messages {

    private String senderName,groupName, from, body,date,time,groupNum;

    public Messages() {
    }

    public Messages(String senderName, String groupName, String from, String body, String date, String time, String groupNum) {
        this.senderName = senderName;
        this.groupName = groupName;
        this.from = from;
        this.body = body;
        this.date = date;
        this.time = time;
        this.groupNum = groupNum;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
