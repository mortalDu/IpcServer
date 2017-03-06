package com.mududu.communicate.ipc;

/**
 * Created by tiger on 2016/12/19.
 */

public class TopicUitl {
    public static final String WAKEUP = "wakeup";//唤醒的topic
    public static final String REQUEST_SPEAK = "request_speak"; //请求说话的topic
    public static final String RECONGNIZER_RESULT = "recognizer_result"; //识别结果的topic
    public static final String REQUEST_RECONGNIZER = "request_recongnizer";//请求识别的topic
    public static final String SPEAK_VIEW = "speak_view" ;//请求显示或隐藏说话提示的topic
    public static final String MQTT_MSG = "mqtt_msg" ;//接收到mqtt消息的topic
    public static final String MQTT_CONTROL_MSG = "mqtt_control_msg" ;// 机器人的控制topic
    public static final String SLEEP_TOPIC = "sleep_tpic" ;// 器人的休眠topic
    public static final String FLOAT_CONTROL_TOPIC = "float_control_tpic" ;// 机器人的悬浮框按键控制topic
}
