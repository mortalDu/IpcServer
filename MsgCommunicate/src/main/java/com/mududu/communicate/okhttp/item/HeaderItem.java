package com.mududu.communicate.okhttp.item;

/**
 * Created by niebin on 2016/12/9.
 {
 "header": {
 "robot_id": "AnfzFl9E",
 "packagename": "com.mududu.communicate",
 "create_time": "long time",
 "type": "voice",
 "username": "niebin",
 "ip":"192.168.1.1",
 "device_id": "xxxxxxxxxxxxxxxxxxxxxxx"
 }
 }


 */
public class HeaderItem {
    private String packagename="";
    private String create_time="";
    private String type="";
    private String ip="";
    private String device_id="";
    private String robot_id="";
    private String username="";
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRobot_id() {
        return robot_id;
    }

    public void setRobot_id(String robot_id) {
        this.robot_id = robot_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getPackageName() {
        return packagename;
    }

    public void setPackageName(String packagename) {
        this.packagename = packagename;
    }



}
