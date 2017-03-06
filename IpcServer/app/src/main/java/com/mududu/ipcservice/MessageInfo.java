package com.mududu.ipcservice;

import com.mududu.communicate.ipc.IMessageCb;

/**
 * Created by tiger on 2016/12/12.
 */

public class MessageInfo {
    private String topic ;
    private String content ;
    private IMessageCb cb ;

    public MessageInfo(String topic, String content, IMessageCb cb){
        this.topic = topic ;
        this.content = content ;
        this.cb = cb ;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCb(IMessageCb cb) {
        this.cb = cb;
    }

    public String getTopic() {

        return topic;
    }

    public String getContent() {
        return content;
    }

    public IMessageCb getCb() {
        return cb;
    }
}
