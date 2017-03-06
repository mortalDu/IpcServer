package com.mududu.communicate.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by niebin on 2016/11/16.
 */
public class DataService extends Service implements IDataReceive {
    @Override
    public void onCreate() {
        super.onCreate();
        SocketHelper.getInstance().initServerWithThread(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDataReceive(String msg) {

    }
}
