package com.mududu.communicate.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.mududu.communicate.ipc.bean.MessageCb;
import com.mududu.communicate.ipc.bean.PackageMessage;
import com.mududu.communicate.ipc.bean.ReceiverDataCb;
import com.mududu.communicate.ipc.bean.Result;
import com.mududu.communicate.ipc.bean.TopicMessage;
import com.mududu.communicate.ipc.exception.InvalideException;
import com.mududu.communicate.ipc.exception.TooLargeException;
import com.mududu.communicate.ipc.impl.IInitCompleteListener;
import com.mududu.communicate.ipc.impl.INewMsgListener;
import com.mududu.communicate.ipc.message.BaseMessage;
import com.mududu.communicate.utils.LogUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tiger on 2016/9/21.
 */

public class IpcClient {
    private final String TAG = "IpcClient" ;
    public final static String SERVICE_PACKAGENAME="com.mududu.ipcservice";
    public static  final String IPC_ACTION="com.mududu.traslate_ipcService";
    public static final String PACKAGE_NAME = "package_name";
    private static IpcClient instance;
    private Context context;
    private IInitCompleteListener listener;
    private IRemoteServer remoteServer ;
    private List<PackageMessage> suspendPackageMsgList = new ArrayList<>();
    private List<TopicMessage> suspendTopicMsgList = new ArrayList<>();
    private List<RegiestMessage> suspendRegList = new ArrayList<>();
    public enum ConnectStatus {
        UNCONNECT, CONNECTING, CONNECTED
    };

    public static final int EMERGENEY = 2 ;
    public static final int HIGHT = 1 ;
    public static final int NORMAL = 0 ;
    public static final int LOW = -1 ;

    private ConnectStatus connectStatus = ConnectStatus.UNCONNECT ;
    private ExecutorService executorService ;
    private INewMsgListener newMsgListener ;
    //三方应用只能注册该topic，不能发送该topic
    private String[] topicSend = new String[] {TopicUitl.WAKEUP, TopicUitl.RECONGNIZER_RESULT, TopicUitl.SPEAK_VIEW};
    //三方应用只能发送该topic，不能注册该topic
    private String[] topicRegiest = new String[] {TopicUitl.REQUEST_SPEAK, TopicUitl.REQUEST_RECONGNIZER};
    private final String SYSTEM_PACKAGENAME = "com.mududu.voice" ;

    private final int CHECK_CONNECT_STATUS  = 1 ;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_CONNECT_STATUS:
                    if(connectStatus == ConnectStatus.CONNECTING) {
                        connectStatus = ConnectStatus.UNCONNECT ;
                    }
                    break ;
            }
        }
    };

    private IpcClient() {
    }

    public static IpcClient getInstance() {
        synchronized (IpcClient.class) {
            if (instance == null)
                instance = new IpcClient();
        }
        return instance;
    }

    public void init(Context ctx) {
        init(ctx, null);
    }

    public void init(Context ctx, IInitCompleteListener complete) {
        executorService = Executors.newCachedThreadPool();
        context = ctx.getApplicationContext();
        listener = complete;
        connect();
    }

    private synchronized void connect() {
        if(ConnectStatus.UNCONNECT == connectStatus) {
            Intent intent = new Intent();
            intent.setAction(IPC_ACTION);
            intent.setPackage(SERVICE_PACKAGENAME);
            intent.putExtra(PACKAGE_NAME,context.getPackageName());
            context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
            setConnectStatus(ConnectStatus.CONNECTING);
            mHandler.sendEmptyMessageDelayed(CHECK_CONNECT_STATUS, 6000);
        }
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            setConnectStatus(ConnectStatus.UNCONNECT);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.logString(IpcClient.getInstance(),"onServiceConnected."+"sendMessage.name=" + name.getPackageName());
            synchronized (IpcClient.class) {
                remoteServer = IRemoteServer.Stub.asInterface(service);
            }
            mHandler.removeMessages(CHECK_CONNECT_STATUS);
            String package_name = context.getPackageName();
            try {
                remoteServer.setClientCallback(package_name, cb);
                remoteServer.asBinder().linkToDeath(deathRecipient, 1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            setConnectStatus(ConnectStatus.CONNECTED);
            sendSuspendPackageMsg();
            sendSuspendTopicMsg();
            sendSuspendRegiestMsg();
        }
    };

    private IRemoteClientCb cb = new IRemoteClientCb.Stub() {
        @Override
        public void send(final String content, final IMessageCb cb) throws RemoteException {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if(newMsgListener == null) {
                        try {
                            if(cb!=null){
                                cb.onResult(JSON.toJSONString(new Result(Result.ERROR_NO_RESPONSE)));
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        return ;
                    }
                    newMsgListener.onNewMessage(new PackageMessage(null, content, cb));
                }
            });
            executorService.execute(t);
        }
    } ;

    public void setNewMsgListener(INewMsgListener newMsgListener) {
        this.newMsgListener = newMsgListener;
    }

    public void sendMsgToPackage(String destPakcage, BaseMessage baseMessage) {
        sendMsgToPackage(destPakcage, baseMessage, null);
    }

    public void sendMsgToPackage(String destPakcage, BaseMessage baseMessage, MessageCb cb) {
        if (context == null) {
            throw new RuntimeException(" context is null ");
        }

        if(baseMessage == null) {
            throw new RuntimeException(" baseMessage is null ");
        }

        PackageMessage message = new PackageMessage();
        message.setContent(baseMessage);
        message.setCb(cb);
        message.setDestPackage(destPakcage);
        sendPackageMessage(message);
    }

    private void sendPackageMessage(PackageMessage msg) {
        IpcUtil.checkDataValide(msg);
        boolean sucess = sendToPackage(msg);
        if(!sucess) {
            addSuspendPackageMsgList(msg);
            connect();
        }
    }

    private boolean sendToPackage(PackageMessage msg) {
        boolean sucess = false ;
        synchronized (IpcClient.class) {
            if (remoteServer == null) {
                return sucess;
            }
            try {
                remoteServer.sendToPackage(msg.getDestPackage(), msg.getContent(),msg.getCb());
                sucess = true;
            } catch (DeadObjectException e) {
                trySetConnectStatus(ConnectStatus.UNCONNECT);
                e.printStackTrace();
            } catch (RemoteException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return sucess ;
    }

    public void sendMsgToTopic(String topic, Object content) {
        sendMsgToTopic(topic, JSON.toJSONString(content));
    }

    public void sendMsgToTopic(String topic, String content) {
        checkSendTopicMsg(topic, content);
        TopicMessage message = new TopicMessage();
        message.setContent(content);
        message.setTopic(topic);
        sendTopicMessage(message);
    }

    private void checkSendTopicMsg(String topic, String content) {
        if (context == null) {
            throw new RuntimeException(" context is null ");
        }

        if(content == null) {
            throw new InvalideException("content can not be null");
        }
        for(int i = 0; i < topicSend.length ; i ++) {
            if(topic.equals(topicSend[i])) {
                if(context.getPackageName().equals(SYSTEM_PACKAGENAME)) {
                    return ;
                } else {
                    throw new InvalideException(" can not send " + topic + " this is system topic");
                }
            }
        }
    }

    private void sendTopicMessage(TopicMessage msg) {
        boolean sucess = sendToTopic(msg);
        if(!sucess) {
            addSuspendTopicMsgList(msg);
            connect();
        }
    }

    private boolean sendToTopic(TopicMessage msg) {
        boolean sucess = false ;
        synchronized (IpcClient.class) {
            if (remoteServer == null) {
                return sucess;
            }
            try {
                remoteServer.sendToTopic(msg.getTopic(), msg.getContent());
                sucess = true;
            } catch (DeadObjectException e) {
                trySetConnectStatus(ConnectStatus.UNCONNECT);
                e.printStackTrace();
            } catch (TransactionTooLargeException e) {
                throw new TooLargeException("TransactionTooLargeException happens");
            } catch (RemoteException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return sucess ;
    }

    private void trySetConnectStatus(ConnectStatus status) {
        if(status == ConnectStatus.UNCONNECT && connectStatus == ConnectStatus.CONNECTING) {
            return ;
        }
        setConnectStatus(status);
    }

    private void setConnectStatus(ConnectStatus status) {
        if(connectStatus == status) {
            return ;
        }
        synchronized (status) {
            connectStatus = status;
        }
        if (listener != null) {
            listener.OnComplete(status);
        }
    }

    public ConnectStatus getConnectStatus() {
        synchronized (connectStatus) {
            return connectStatus;
        }
    }

    private void addSuspendPackageMsgList(PackageMessage msg) {
        synchronized (suspendPackageMsgList) {
            suspendPackageMsgList.add(msg);
        }
    }

    private void addSuspendTopicMsgList(TopicMessage msg) {
        synchronized (suspendTopicMsgList) {
            suspendTopicMsgList.add(msg);
        }
    }

    private void addSuspendRegList(RegiestMessage msg) {
        synchronized (suspendRegList) {
            suspendRegList.add(msg);
        }
    }

    private void sendSuspendPackageMsg() {
        synchronized (suspendPackageMsgList) {
            List<PackageMessage> packageMsgList = new ArrayList<>();
            for(PackageMessage msg : suspendPackageMsgList) {
                boolean sucess = sendToPackage(msg);
                if(!sucess) {
                    connect();
                    continue;
                }
                packageMsgList.add(msg);
            }
            suspendPackageMsgList.removeAll(packageMsgList);
        }
    }

    private void sendSuspendTopicMsg() {
        synchronized (suspendTopicMsgList) {
            List<TopicMessage> topicMsgList = new ArrayList<>();
            for(TopicMessage msg : suspendTopicMsgList) {
                boolean sucess = sendToTopic(msg);
                if(!sucess) {
                    connect();
                    continue;
                }
                topicMsgList.add(msg);
            }
            suspendTopicMsgList.removeAll(topicMsgList);
        }
    }

    private void sendSuspendRegiestMsg() {
        synchronized (suspendRegList) {
            List<RegiestMessage> regMsgList = new ArrayList<>();
            for(RegiestMessage msg : suspendRegList) {
                boolean sucess = regiest(msg.getTopic(), msg.getCb(), msg.getPriority());
                if(!sucess) {
                    connect();
                    continue;
                }
                regMsgList.add(msg);
            }
            suspendRegList.removeAll(regMsgList);
        }
    }

    /**
     * @param topic
     * @param receiverDataCb
     */
    public void regiestTopic(String topic, ReceiverDataCb receiverDataCb ) {
        regiestTopic(topic, receiverDataCb, NORMAL);
    }

    /**
     * @param topic
     * @param receiverDataCb
     * @param priority 优先级 EMERGENEY, HIGHT, NORMAL, LOW
     */
    public void regiestTopic(String topic, ReceiverDataCb receiverDataCb , int priority) {
        checkRegiestTopic(topic, receiverDataCb, priority);
        boolean sucess = regiest(topic, receiverDataCb, priority);
        if(sucess == false) {
            addSuspendRegList(new RegiestMessage(topic, receiverDataCb, priority));
            connect();
        }
    }

    private void checkRegiestTopic(String topic, ReceiverDataCb receiverDataCb, int priority) {
        if (context == null) {
            throw new RuntimeException(" context is null ");
        }

        if(TextUtils.isEmpty(topic)) {
            throw new InvalideException("topic is not valide ");
        }

        if(receiverDataCb == null) {
            throw new InvalideException("receiverDataCb is null ");
        }

        if(priority < LOW || priority > EMERGENEY) {
            throw new InvalideException("invalide priority ");
        }

       for(int i = 0; i < topicRegiest.length ; i ++) {
           if(topic.equals(topicRegiest[i])) {
                if(context.getPackageName().equals(SYSTEM_PACKAGENAME)) {
                    return ;
                } else {
                    throw new InvalideException( context.getPackageName() + " can not regiest "+ topic + " this is system topic");
                }
           }
       }
    }

    //如果注销失败一般是因为断开连接导致的，而断开连接后就无需重新连接到server中去清除数据
    public void unRegiestTopic(String topic, ReceiverDataCb receiverDataCb) {
        if(TextUtils.isEmpty(topic)) {
            return ;
        }

        if(receiverDataCb == null) {
            return ;
        }
        synchronized (IpcClient.class) {
            if (remoteServer != null) {
                try {
                    receiverDataCb.setUnregeist(topic, true);
                    topic = topic + "@" + receiverDataCb.hashCode() ;
                    remoteServer.unRegiest(topic, receiverDataCb);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean regiest(String topic, ReceiverDataCb receiverDataCb, int priority) {
        boolean sucess = false ;
        synchronized (IpcClient.class) {
            if (remoteServer == null) {
                return false;
            }
            try {
                String newTopic = topic + "@" + receiverDataCb.hashCode() ;
                remoteServer.regiest(newTopic, context.getPackageName(), receiverDataCb, priority);
                sucess = true ;
            } catch (DeadObjectException e) {
                trySetConnectStatus(ConnectStatus.UNCONNECT);
                e.printStackTrace();
            } catch (TransactionTooLargeException e) {
                throw new TooLargeException("TransactionTooLargeException happens");
            } catch (RemoteException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        if(sucess)
            receiverDataCb.setUnregeist(topic, false);
        return sucess ;
    }

    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            LogUtil.e(" ---- -binderDied- ---- ");
            dealDisconnect();
        }
    } ;

    private void dealDisconnect() {
        synchronized (IpcClient.class) {
            remoteServer = null;
        }
        LogUtil.e(" ---- -onServiceDisconnected- ---- ");
        setConnectStatus(ConnectStatus.UNCONNECT);
    }
}
