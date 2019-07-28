package com.dimatechs.werd.Model;

public class Groups
{
    private String groupNum,groupName;

    public Groups() {
    }

    public Groups(String groupNum, String groupName) {
        this.groupNum = groupNum;
        this.groupName = groupName;
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
