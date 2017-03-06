// IReceiverData.aidl
package com.mududu.communicate.ipc;
import com.mududu.communicate.ipc.IMessageCb;

// Declare any non-default types here with import statements

interface IReceiverDataCb {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    boolean onNewData(String topic, String data);
}
