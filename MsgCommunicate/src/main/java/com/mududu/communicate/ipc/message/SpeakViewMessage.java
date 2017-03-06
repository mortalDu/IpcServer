package com.mududu.communicate.ipc.message;

/**
 * Created by tiger on 2016/12/5.
 */

public class SpeakViewMessage {
    public static final String HIDE = "HIDE";
    public static final String SHOW = "SHOW";
    private String operate ;

    public SpeakViewMessage() {
        this(null);
    }
    public SpeakViewMessage(String operate) {
        this.operate = operate ;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }
}
