package com.dimatechs.werd.Model;



public class Users
{
    private String name, phone, password,group,num;
    private boolean isSelected;


    public Users()
    {
    }

    public Users(String name, String phone, String password, String group, String num, boolean isSelected) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.group = group;
        this.num = num;
        this.isSelected = isSelected;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}