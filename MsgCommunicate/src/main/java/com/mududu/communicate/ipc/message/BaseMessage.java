package com.mududu.communicate.ipc.message;

/**
 * Created by niebin on 2016/11/18.
 */
public class BaseMessage {
    private String type = "common";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
