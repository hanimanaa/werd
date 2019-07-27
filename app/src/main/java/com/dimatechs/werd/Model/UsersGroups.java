package com.dimatechs.werd.Model;

public class UsersGroups
{
    private String group,partNum;

    public UsersGroups() {
    }

    public UsersGroups(String group,String partNum) {
        this.group = group;
        this.partNum = partNum;

    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPartNum() {
        return partNum;
    }

    public void setPartNum(String partNum) {
        this.partNum = partNum;
    }
}
