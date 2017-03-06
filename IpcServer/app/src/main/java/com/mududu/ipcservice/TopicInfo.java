package com.mududu.ipcservice;

import com.mududu.communicate.ipc.IReceiverDataCb;

/**
 * Created by tiger on 2016/12/9.
 */

public class TopicInfo {
    private String packageName ;
    private ReceiveDataInfo receiveDataInfo ;

    public TopicInfo(String packageName, ReceiveDataInfo receiveDataInfo) {
        this.packageName = packageName ;
        this.receiveDataInfo = receiveDataInfo ;
    }

    public void setReceiveDataInfo(ReceiveDataInfo receiveDataInfo) {
        this.receiveDataInfo = receiveDataInfo;
    }

    public ReceiveDataInfo getReceiveDataInfo() {
        return receiveDataInfo;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

}
