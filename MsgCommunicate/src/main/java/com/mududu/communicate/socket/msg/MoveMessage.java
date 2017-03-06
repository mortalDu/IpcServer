package com.mududu.communicate.socket.msg;

/**
 * Created by niebin on 2016/11/10.
 */
public class MoveMessage {
    private String type;
    private String value;
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
