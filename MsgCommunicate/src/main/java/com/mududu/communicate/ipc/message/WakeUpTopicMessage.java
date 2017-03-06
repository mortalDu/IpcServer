package com.mududu.communicate.ipc.message;

/**
 * Created by tiger on 2016/12/19.
 */

public class WakeUpTopicMessage {
    private int state ;
    private int angle ;
    public WakeUpTopicMessage() {

    }

    public WakeUpTopicMessage(int state, int angle) {
        this.angle = angle ;
        this.state = state ;
    }
    public int getState() {
        return state;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public void setState(int state) {
        this.state = state;
    }
}
