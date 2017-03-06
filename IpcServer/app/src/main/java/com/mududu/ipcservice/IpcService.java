package com.mududu.ipcservice;

import android.app.Service;
import android.content.Intent;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.mududu.communicate.ipc.AutoBroadcast;
import com.mududu.communicate.ipc.IMessageCb;
import com.mududu.communicate.ipc.IReceiverDataCb;
import com.mududu.communicate.ipc.IRemoteClientCb;
import com.mududu.communicate.ipc.IRemoteServer;
import com.mududu.communicate.ipc.IpcClient;
import com.mududu.communicate.ipc.bean.PackageMessage;
import com.mududu.communicate.ipc.impl.INewMsgListener;
import com.mududu.communicate.utils.CommonUtil;
import com.mududu.communicate.utils.LogUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**`
 * Created by tiger on 2016/9/21.
 */

public class IpcService extends Service{
    private static final String BUGLY_ID = "57123afd2a";
    private final String TAG = "IpcService";
    private ExecutorService executorService ;
    private INewMsgListener newMessage ;
    public static final String SERVER_NAME = "server_name";
    private Map<String, CopyOnWriteArrayList<TopicInfo>> topicMap = new ConcurrentHashMap<>();
    private Map<String, List<MessageInfo>> msgMap = new HashMap<>();
    private Map<String, ClientInfo> packageMap = new HashMap<String, ClientInfo>();

    private final int CHECK_CLIENT_STATUS  = 1 ;
    private long DELAY_TIME = 5000 ;
    public static IpcService instance ;

    public IpcService getInstance(){
        synchronized (IpcService.this) {
            instance = new IpcService();
        }
        return instance ;
    }

      private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_CLIENT_STATUS:
                    ClientInfo clientInfo = getClientCallback((String) msg.obj);
                    if(clientInfo == null)
                        return ;
                    if(clientInfo.isStartingClient()) {
                        clientInfo.notifyConnectFail();
                    }
                    break ;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newCachedThreadPool();

        // Bugly init
        CrashReport.initCrashReport(getApplicationContext(), BUGLY_ID, true);
        Log.e(TAG, " -- IpcService --- onCreate ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return server.asBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG," -- onUnbind --- intent=" +intent.toString() + " packageName=" +intent.getStringExtra(IpcClient.PACKAGE_NAME));
        String action = intent.getAction();
        if(!TextUtils.isEmpty(action) && action.equals(IpcClient.IPC_ACTION)) {
            String packageName = intent.getStringExtra(IpcClient.PACKAGE_NAME);
//            removeTopicInfo(packageName);
        }
        return super.onUnbind(intent);
    }

    private IRemoteServer server = new IRemoteServer.Stub(){
        @Override
        public void setClientCallback(String packageName, IRemoteClientCb callback) throws RemoteException {
            synchronized (packageMap) {
                if (!CommonUtil.isNull(packageName)) {
                    ClientInfo info = packageMap.get(packageName);
                    if(info == null) {
                        packageMap.put(packageName, new ClientInfo(IpcService.this, packageName, callback));
                        return ;
                    }
                    info.setRemoteClientCb(callback);
                    info.notifyConnectSucess();
                }
            }
        }

        @Override
        public void sendToPackage(String desPackage, String content, IMessageCb cb) throws RemoteException {
            sendMessage(new PackageMessage(desPackage, content, cb));
        }

        @Override
        public void sendToTopic(final String topic, final String content) throws RemoteException {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    CopyOnWriteArrayList<TopicInfo> topicInfoList = topicMap.get(topic);
                    LogUtil.e(" ---IpcService send---   topic=" + topic);
                    if (topicInfoList == null) {
                        return;
                    }
                    LogUtil.e(" ---IpcService send---   topicSize=" + topicInfoList.size());
                    List<TopicInfo> deadTopic = new ArrayList<>();
                    for (TopicInfo topicInfo : topicInfoList) {
                        try {
                            boolean abort = topicInfo.getReceiveDataInfo().getCb().onNewData(topic, content);
                            if(abort) {
                                break ;
                            }
                        } catch (DeadObjectException e) {
                            deadTopic.add(topicInfo);
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    if (deadTopic.size() != 0) {
                        topicInfoList.removeAll(deadTopic);
                    }
                }
            });
            executorService.execute(t);
        }

        @Override
        public void regiest(final String topic, final String packageName, final IReceiverDataCb cb, final int priority) throws RemoteException {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (topicMap) {
                        String hashCode = getHashCode(topic);
                        String newTopic = getTopic(topic);
                        CopyOnWriteArrayList<TopicInfo> topicCbList = topicMap.get(newTopic);
                        if (topicCbList == null) {
                            topicCbList = new CopyOnWriteArrayList<>();
                            topicMap.put(newTopic, topicCbList);
                        }
                        for (TopicInfo info : topicCbList) {
                            if (info.getReceiveDataInfo().getHashCode().equals(hashCode)) {
                                Log.e(TAG, " -- IpcService set same topic and cb --- regiest topic=" + newTopic + " hashCode=" + hashCode);
                                info.setReceiveDataInfo(new ReceiveDataInfo(hashCode, newTopic, cb, priority));
                                return;
                            }
                        }
                        topicCbList.add(new TopicInfo(packageName, new ReceiveDataInfo(hashCode, newTopic, cb, priority)));
                        sortTopicList(topicCbList);
                        Log.e(TAG, " -- IpcService --- regiest topic=" + newTopic + " hashCode=" + hashCode);
                    }
                }
            });
            executorService.execute(t);
        }

        @Override
        public void unRegiest(final String topic, IReceiverDataCb cb) throws RemoteException {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String hashCode = getHashCode(topic);
                        String newTopic = getTopic(topic);
                        Log.e(TAG, " -- unRegiest --- topic= " +topic + " newTopic=" +newTopic);
                        List<TopicInfo> topicInfoList = topicMap.get(newTopic);
                        if (topicInfoList == null) {
                            return;
                        }
                        for (TopicInfo info : topicInfoList) {
                            Log.e(TAG, " -- unRegiest --- info.getCb=" + info.getReceiveDataInfo().getHashCode());
                            if (info.getReceiveDataInfo().getHashCode().equals(hashCode)) {
                                Log.e(TAG, " -- unRegiest --- remove ");
                                topicInfoList.remove(info);
                                return;
                            }
                        }
                    }
                });
            executorService.execute(t);
        }
    };

    private void sortTopicList(CopyOnWriteArrayList<TopicInfo> topicCbList) {
        List list = Arrays.asList(topicCbList.toArray());
        Collections.sort(list, new TopicComparator());
        topicCbList.clear();
        topicCbList.addAll(list);
    }

    private class TopicComparator implements Comparator<TopicInfo> {

        @Override
        public int compare(TopicInfo topicInfo, TopicInfo t1) {
            if(topicInfo.getReceiveDataInfo().getPriority() > t1.getReceiveDataInfo().getPriority()) {
                return -1 ;
            }
            return 1;
        }
    }

    private void sortList() {

    }

    private String getHashCode(String topic) {
        int index = topic.lastIndexOf("@");
        return topic.substring(index+1) ;
    }

    private String getTopic(String topic) {
        int index = topic.lastIndexOf("@");
        return topic.substring(0, index);
    }

    public void sendMessage(PackageMessage message) {
        String packageName = message.getDestPackage() ;
        ClientInfo clientInfo =  getClientCallback(packageName);
        if(clientInfo == null) {
            synchronized (packageMap) {
                clientInfo = new ClientInfo(IpcService.this, packageName, null) ;
                packageMap.put(packageName, clientInfo);
            }
        }
        clientInfo.sendMessage(message);
    }

    private ClientInfo getClientCallback(String packageName) {
        synchronized (packageMap) {
            return packageMap.get(packageName);
        }
    }

    private synchronized void addSuspendList(MessageInfo info) {
        String topic = info.getTopic();
        List<MessageInfo> msgInfoList = msgMap.get(topic);
        if(msgInfoList == null) {
            msgInfoList = new ArrayList<>();
        }
        msgInfoList.add(info);
    }

    public void tryConnectPackage(String packageName) {
        Intent intent = new Intent();
        intent.setAction(packageName + AutoBroadcast.ACTION);
        intent.putExtra(SERVER_NAME, getPackageName());
        sendBroadcast(intent);
        Message msg = Message.obtain();
        msg.what = CHECK_CLIENT_STATUS ;
        msg.obj = packageName ;
        mHandler.sendMessageDelayed(msg, DELAY_TIME);
    }

    private void removeTopicInfo(String packageName) {
        if(TextUtils.isEmpty(packageName)) {
            return ;
        }
        synchronized (IpcService.class) {
            Iterator iter = topicMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                List<TopicInfo> topicInfoList = (List<TopicInfo>) entry.getValue();
                int size = topicInfoList.size();
                for(int i = size -1 ; i >= 0 ; i--){
                    TopicInfo topicInfo = topicInfoList.get(i);
                    if(packageName.equals(topicInfo.getPackageName())) {
                        topicInfoList.remove(i);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
