package com.mududu.communicate.json;

import com.mududu.communicate.ipc.message.BaseMessage;

/**
 * Created by niebin on 2016/11/18.
 */
public class VRoomMessage  extends BaseMessage {
    final static public String VROOM_PACKAGE="com.chinasns.quameeting.vr";

    final public static String _TYPE_INIT_ ="init";
    final public static String _TYPE_SPEAK_ ="speak";
    final public static String _TYPE_VIEW_ ="view";
    final public static String _TYPE_EXIT_ ="exit";
    final public static String _TYPE_ERROR_ ="error";


    final public static String _VIEW_HIDE="hide";
    final public static String _VIEW_SHOW="SHOW";

    final public static String _ERROR_LOC="loc";

    private String value;
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
