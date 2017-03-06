package com.mududu.communicate.ipc.impl;

import com.mududu.communicate.ipc.bean.PackageMessage;

/**
 * Created by tiger on 2016/9/21.
 */

public interface INewMsgListener {
    void onNewMessage(PackageMessage msg);
}
