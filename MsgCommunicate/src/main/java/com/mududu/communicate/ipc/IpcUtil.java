package com.mududu.communicate.ipc;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.mududu.communicate.ipc.bean.PackageMessage;
import com.mududu.communicate.ipc.exception.InvalideException;
import com.mududu.communicate.ipc.message.BaseMessage;

/**
 * Created by tiger on 2016/12/5.
 */

public class IpcUtil {
    public static void checkDataValide(PackageMessage msg) {
        BaseMessage baseMessage = JSON.parseObject(msg.getContent(), BaseMessage.class);
        if(baseMessage == null) {
            throw new InvalideException("please input data");
        }

        if(TextUtils.isEmpty(baseMessage.getType())) {
            throw new InvalideException("cmdType is null");
        }
    }
}
