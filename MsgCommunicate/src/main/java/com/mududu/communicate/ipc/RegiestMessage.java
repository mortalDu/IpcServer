package com.mududu.communicate.ipc;

import com.mududu.communicate.ipc.IMessageCb;
import com.mududu.communicate.ipc.IReceiverDataCb;
import com.mududu.communicate.ipc.bean.ReceiverDataCb;

/**
 * Created by tiger on 2016/12/9.
 */

public class RegiestMessage {
    private String topic ;
    private ReceiverDataCb cb ;
    private int priority;

    public RegiestMessage(String topic, ReceiverDataCb cb, int priority) {
        this.topic = topic ;
        this.cb = cb ;
        this.priority = priority ;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setCb(ReceiverDataCb cb) {
        this.cb = cb;
    }

    public String getTopic() {

        return topic;
    }

    public ReceiverDataCb getCb() {
        return cb;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
