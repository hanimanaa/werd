package com.dimatechs.werd.Model;

public class UsersGroups {
    private String groupNum, groupName, partNum;

    public UsersGroups() {
    }

    public UsersGroups(String groupNum, String groupName, String partNum) {
        this.groupNum = groupNum;
        this.groupName = groupName;
        this.partNum = partNum;
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
