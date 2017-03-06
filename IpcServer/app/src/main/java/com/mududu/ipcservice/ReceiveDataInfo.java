package com.mududu.ipcservice;

import com.mududu.communicate.ipc.IReceiverDataCb;

/**
 * Created by tiger on 2016/12/12.
 */

public class ReceiveDataInfo {
    private String hashCode ;
    private String topic ;
    private IReceiverDataCb cb ;
    private int priority ;
    public ReceiveDataInfo() {

    }

    public ReceiveDataInfo(String hashCode, String topic, IReceiverDataCb cb, int priority) {
        this.hashCode = hashCode ;
        this.topic = topic ;
        this.cb = cb ;
        this.priority = priority ;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public void setCb(IReceiverDataCb cb) {
        this.cb = cb;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getHashCode() {
        return hashCode;
    }

    public IReceiverDataCb getCb() {
        return cb;
    }

    public String getTopic() {
        return topic;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
