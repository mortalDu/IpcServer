package com.mududu.communicate.okhttp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by niebin on 2016/12/21.
 */
public class UploadService extends Service {
    private Timer mTimer;
    private long AGAIN_TIME=24*60*60*1000;//24小时.一天一次
    private long FIRST_TIME=1*3*60*1000;//3分钟
    @Override
    public void onCreate() {
        super.onCreate();
        UploadManager.getInstance().init(this);
//        mTimer=new Timer();
//        mTimer.schedule(task,FIRST_TIME,AGAIN_TIME);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private TimerTask task=new TimerTask() {
        @Override
        public void run() {
//            UploadManager.getInstance().uploadFiles();
        }
    };
}
