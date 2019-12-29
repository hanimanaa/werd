package com.dimatechs.werd.Model;

public class Groups
{
    private String groupNum,groupName,locked;

    public Groups() {
    }

    public Groups(String groupNum, String groupName, String locked) {
        this.groupNum = groupNum;
        this.groupName = groupName;
        this.locked = locked;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
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
