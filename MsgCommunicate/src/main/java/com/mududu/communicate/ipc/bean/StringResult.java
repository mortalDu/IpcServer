package com.mududu.communicate.ipc.bean;

/**
 * Created by tiger on 2016/9/22.
 */

public class StringResult extends Result {
    private String content ;

    public StringResult() {
        super();
    }

    public StringResult(String content, int resultCode) {
        super(resultCode);
        this.content = content ;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
