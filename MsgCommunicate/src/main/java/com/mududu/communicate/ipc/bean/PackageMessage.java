package com.mududu.communicate.ipc.bean;

import android.os.RemoteException;

import com.alibaba.fastjson.JSON;
import com.mududu.communicate.ipc.IMessageCb;
import com.mududu.communicate.ipc.message.BaseMessage;

/**
 * Created by tiger on 2016/12/12.
 */

public class PackageMessage {
    private String content ;
    private IMessageCb cb ;
    private String destPackage ;

    public PackageMessage(){

    }

    public PackageMessage(String destPackage, String content, IMessageCb cb) {
        this.destPackage = destPackage ;
        this.content = content ;
        this.cb = cb ;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setContent(BaseMessage baseMessage) {
        content = JSON.toJSONString(baseMessage);
    }

    public IMessageCb getCb() {
        return cb;
    }

    public void setCb(IMessageCb cb) {
        this.cb = cb;
    }

    public void onResult(String result) {
        try {
            if(cb!=null)
                cb.onResult(result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setDestPackage(String destPackage) {
        this.destPackage = destPackage;
    }

    public String getDestPackage() {
        return destPackage;
    }
}
