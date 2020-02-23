package com.dimatechs.werd.Model;

public class ScheduleMessages {

    private String sendTime;
    private String body;
    private String receiver;
    private String requestCode;
    private String groupNum;




    public ScheduleMessages() {
    }

    public ScheduleMessages(String sendTime, String body, String receiver, String requestCode, String groupNum) {
        this.sendTime = sendTime;
        this.body = body;
        this.receiver = receiver;
        this.requestCode = requestCode;
        this.groupNum = groupNum;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }


    public String getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

}
