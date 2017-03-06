package com.mududu.communicate.ipc.common;

        import android.text.TextUtils;

        import com.alibaba.fastjson.JSON;
        import com.blankj.utilcode.utils.ExitApplication;
        import com.mududu.communicate.ipc.RegiestMessage;
        import com.mududu.communicate.ipc.message.RecongnizerMessage;
        import com.mududu.communicate.ipc.message.SemanticItem;

/**
 * Created by niebin on 2016/12/26.
 */
public class VoiceResultUtil {
    public static String CLOSE_CONFERENCE="关闭会议室";
    public static RecongnizerMessage getRegiestMessage(String json){
        RecongnizerMessage msg = JSON.parseObject(json, RecongnizerMessage.class);
        return  msg;
    }
    public static SemanticItem getSemanticItem(String json){
        RecongnizerMessage msg =getRegiestMessage(json);
        if(msg!=null&&msg.getType() == RecongnizerMessage.RECONGNIZER_RESULT) {
            SemanticItem item = msg.getItem();
            return item;
        }
        return null;
    }
    /**
     *必须是能解析为RecongnizerMessage的json
     * close 如 关闭会议室
     * */
    public static boolean isCloseCurrent(String json,String close){
            SemanticItem item=getSemanticItem(json);
        if(item==null)return false;
        if(TextUtils.equals(item.getService(), SemanticItem.SERVICE_COMMON)){
            if(item.getOperation().equals(SemanticItem.OPERATION_CLOSE)){
                if(item.getText().equals(close)){
                    return true;
                }
            }
        }
        return false;
    }
}
