package com.dimatechs.werd.Model;

public class UsersGroups {
    private String id,groupNum,groupName, partNum,done,userName,userPhone;

    public UsersGroups() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UsersGroups(String id, String groupNum, String groupName, String partNum, String done, String userPhone, String userName) {
        this.id = id;
        this.groupNum = groupNum;
        this.groupName = groupName;
        this.partNum = partNum;
        this.done=done;
        this.userPhone=userPhone;
        this.userName=userName;

    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
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

    public String getPartNum() {
        return partNum;
    }

    public void setPartNum(String partNum) {
        this.partNum = partNum;
    }
}
