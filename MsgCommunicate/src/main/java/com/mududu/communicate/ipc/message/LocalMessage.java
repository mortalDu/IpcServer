package com.mududu.communicate.ipc.message;

import android.os.Bundle;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * Created by tiger on 2017/2/14.
 */

public class LocalMessage extends BaseMessage {
    public final static String LOCAL_CMD = "get_location";
    public final static int NO_CITY = 3 ;
    public LocalMessage() {
        setType(LOCAL_CMD);
    }
}
