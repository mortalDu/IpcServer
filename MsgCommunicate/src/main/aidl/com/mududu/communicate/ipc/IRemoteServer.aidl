package com.mududu.communicate.ipc;
import com.mududu.communicate.ipc.IRemoteClientCb;
import com.mududu.communicate.ipc.IMessageCb;
import com.mududu.communicate.ipc.IReceiverDataCb;
interface IRemoteServer {
    void setClientCallback(String packageName, IRemoteClientCb callback);
    void sendToPackage(String desPackage, String content, IMessageCb cb);
    void sendToTopic(String topic, String content);
    void regiest(String topic, String packageName, IReceiverDataCb cb, int priority);
    void unRegiest(String topic, IReceiverDataCb cb);
}