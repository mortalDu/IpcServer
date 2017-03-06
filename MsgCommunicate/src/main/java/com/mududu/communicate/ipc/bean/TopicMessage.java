package com.mududu.communicate.ipc.bean;

import com.alibaba.fastjson.JSON;
import com.mududu.communicate.ipc.message.BaseMessage;

/**
 * Created by tiger on 2016/12/12.
 */

public class TopicMessage {
    private String content ;
    private String topic ;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
