package com.mududu.communicate.ipc;
import com.mududu.communicate.ipc.IMessageCb;
interface IRemoteClientCb {
    void send(String content, IMessageCb cb);
}
