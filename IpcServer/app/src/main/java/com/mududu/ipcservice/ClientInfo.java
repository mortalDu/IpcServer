package com.mududu.ipcservice;

import android.os.DeadObjectException;
import android.os.RemoteException;

import com.alibaba.fastjson.JSON;
import com.mududu.communicate.ipc.IRemoteClientCb;
import com.mududu.communicate.ipc.IpcUtil;
import com.mududu.communicate.ipc.bean.PackageMessage;
import com.mududu.communicate.ipc.bean.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tiger on 2016/9/22.
 */

public class ClientInfo {
    private String packageName ;
    private IRemoteClientCb remoteClientCb ;
    private List<PackageMessage> suspendMsgList = new ArrayList<>();
    private IpcService service ;
    private boolean isStartingClient = false ;

    public ClientInfo(IpcService service, String packageName, IRemoteClientCb remoteClientCb) {
        this.service = service ;
        this.packageName = packageName ;
        this.remoteClientCb = remoteClientCb ;
    }

    public String getPackageName() {
        return packageName;
    }

    public IRemoteClientCb getRemoteClientCb() {
        return remoteClientCb;
    }

    public void setStartingClient(boolean startingClient) {
        isStartingClient = startingClient;
    }

    public boolean isStartingClient() {
        return isStartingClient;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setRemoteClientCb(IRemoteClientCb remoteClientCb) {
        this.remoteClientCb = remoteClientCb;
    }

    private void addSuspendMessage(PackageMessage remoteMsg) {
        synchronized (suspendMsgList) {
            suspendMsgList.add(remoteMsg);
        }
    }

    public void sendMessage(PackageMessage remoteMsg) {
        IpcUtil.checkDataValide(remoteMsg);
        if(isStartingClient) {
            addSuspendMessage(remoteMsg);
            return ;
        }
        boolean sucess = send(remoteMsg);
        if(!sucess) {
            remoteClientCb = null ;
            isStartingClient = true ;
            addSuspendMessage(remoteMsg);
            service.tryConnectPackage(packageName);
        }
    }

    private boolean sendSuspendMessage(PackageMessage remoteMsg) {
        boolean sucess = send(remoteMsg);
        if(!sucess) {
            remoteClientCb = null ;
            isStartingClient = true ;
            service.tryConnectPackage(packageName);
        }
        return sucess ;
    }

    private boolean send(PackageMessage remoteMsg) {
        boolean sucess = false ;
        if(remoteClientCb != null) {
            try {
                remoteClientCb.send(remoteMsg.getContent(), remoteMsg.getCb());
                sucess = true;
            } catch (DeadObjectException e) {
            } catch (RemoteException e) {
                e.printStackTrace();
                sucess = true;
            }
        }
        return sucess ;
    }

    public void notifyConnectFail() {
        isStartingClient = false ;
        synchronized (suspendMsgList) {
            for(PackageMessage msg : suspendMsgList) {
                if(msg == null)
                    continue;
                msg.onResult(JSON.toJSONString(new Result(Result.ERROR_UNCONNECT)));
            }
            suspendMsgList.clear();
        }
    }

    public void notifyConnectSucess() {
        isStartingClient = false ;
        synchronized (suspendMsgList) {
            List<PackageMessage> sendMsgList = new ArrayList<>();
            for(PackageMessage msg : suspendMsgList) {
                boolean sucess = sendSuspendMessage(msg);
                if(!sucess) {
                    continue ;
                }
                sendMsgList.add(msg);
            }
            suspendMsgList.removeAll(sendMsgList);
        }
    }
}
