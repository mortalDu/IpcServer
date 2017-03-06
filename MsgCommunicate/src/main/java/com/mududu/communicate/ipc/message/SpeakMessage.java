package com.mududu.communicate.ipc.message;

/**
 * Created by tiger on 2016/12/5.
 */

public class SpeakMessage {
    private String text ;
    public SpeakMessage() {
        this(null);
    }

    public SpeakMessage(String text) {
        this.text = text ;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
