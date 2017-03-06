package com.mududu.communicate.json;

import com.mududu.communicate.ipc.message.BaseMessage;

/**
 * Created by niebin on 2016/11/30.
 */
public class HelperMessage extends BaseMessage {
    final static public String HELPER_PACKAGE="com.mdd.robothelper";

    final static public String _TYPE_WAKE_="wake";/**唤醒*/
    final static public String _TYPE_VROOM="vroom";/**虚拟办公*/
    final static public String _TYPE_HELPE_="help";/**帮助*/
    final static public String _TYPE_COMMOND_="common";/**语音命令*/
    final static public String _TYPE_SPEAK="speak";/**说话*/

    final static public String _TYPE_HELPE_CLOSE="close";/**帮助*/

    private String value;
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
