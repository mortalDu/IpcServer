package com.mududu.communicate.json;

import com.mududu.communicate.ipc.AppPackage;
import com.mududu.communicate.ipc.IpcClient;
import com.mududu.communicate.ipc.TopicUitl;
import com.mududu.communicate.ipc.message.SpeakMessage;
import com.mududu.communicate.ipc.message.SpeakViewMessage;

/**
 * Created by niebin on 2016/11/23.
 */
public class MessageUtil {
    public static void sendVRoomMessage(String pkg,String type,String value){
        VRoomMessage vrM=new VRoomMessage();
        vrM.setType(type);
        vrM.setValue(value);
        IpcClient.getInstance().sendMsgToPackage(AppPackage.APP_MDDVOICE, vrM);
    }

    public static void sendSpeakViewMessage(String operate) {
//        SpeakViewMessage msg = new SpeakViewMessage(operate);
//        IpcClient.getInstance().sendMsgToTopic(TopicUitl.SPEAK_VIEW, msg);
    }

    public static void sendSpeakMessage(String text) {
        SpeakMessage msg = new SpeakMessage(text);
        IpcClient.getInstance().sendMsgToTopic(TopicUitl.REQUEST_SPEAK, msg);
    }
}
