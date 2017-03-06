package com.mududu.communicate.ipc.bean;

import android.os.RemoteException;
import android.util.Log;

import com.mududu.communicate.ipc.IReceiverDataCb;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tiger on 2016/12/22.
 */

public abstract class ReceiverDataCb extends IReceiverDataCb.Stub {
    private Map<String, Boolean> regiestMap = new HashMap<>();
    private boolean isAbortMessage = false ;

    public void setUnregeist(String topic, boolean unregeist) {
        synchronized (regiestMap) {
            regiestMap.put(topic, unregeist);
        }
    }

    public boolean isUnregeist(String topic) {
        synchronized (regiestMap) {
            Boolean value = regiestMap.get(topic);
            return value == null ? false : value;
        }
    }

    @Override
    public final boolean onNewData(String topic, String data) throws RemoteException {
        if(isUnregeist(topic))
            return false;
        onNewMessage(data);
        return isAbortMessage ? true : false  ;
    }

    public void abortMessage() {
        isAbortMessage = true ;
    }

    public abstract void onNewMessage(String message);
}
