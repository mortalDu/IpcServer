package com.mududu.communicate.okhttp;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.utils.AppUtils;
import com.blankj.utilcode.utils.ConstantsUtil;
import com.blankj.utilcode.utils.DeviceUtils;
import com.blankj.utilcode.utils.FileUtils;
import com.blankj.utilcode.utils.LaunchAppUtils;
import com.blankj.utilcode.utils.LogUtil;
import com.blankj.utilcode.utils.SPUtils;
import com.mdd.robotlib.move.util.DeviceUtil;
import com.mududu.communicate.okhttp.item.HeaderItem;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by niebin on 2016/12/6.
 */
public class UploadUtil {
    public static final String ROBOT_ID_KEY = "robot_id";
    private final static String _TYPE_ = "Default";
    public final static String _URL_ = ConstantsUtil.BASE_URL + "/mududu/data/upload";
    private Object wLock = new Object();
    private Object rLock = new Object();
    private ArrayList<String> contentList = new ArrayList<>();
    private Context mCtx;
    private boolean isPause = false;
    private boolean isStop = false;
    private String type ;
    private String fileName ;

    public UploadUtil(Context ctx, String basePath, String type) {
        mCtx = ctx;
        this.type = type ;
        fileName = basePath + "/" + type;
        startWriteService();
    }

    protected boolean isStop() {
        synchronized (wLock) {
            return isStop;
        }
    }

    protected void setStop(boolean stop) {
        synchronized (wLock) {
            isStop = stop;
        }
        unWLock();
    }

    protected void writeHeader() throws IOException {
        HeaderItem hItem = new HeaderItem();
        String name = DeviceUtil.getUserName();
        hItem.setUsername(name);
        hItem.setType(type);
        String robot_id = LaunchAppUtils.getRobotId(mCtx);
        hItem.setRobot_id(robot_id);
        hItem.setPackageName(mCtx.getPackageName());
        hItem.setCreate_time(System.currentTimeMillis() + "");
        hItem.setDevice_id(DeviceUtil.getDeviceId());
        hItem.setIp(DeviceUtils.getIp(mCtx));
        String j = JSON.toJSONString(hItem);
        LogUtil.logObject(this, "json=" + j);
        synchronized (wLock) {
            isPause = true;
            contentList.clear();
            File f = new File(fileName);
            if (f != null && !f.exists()) {
                contentList.add(j + "\n");
            }
            isPause = false;
        }
        unWLock();
    }

    private void writeCachFile(String content) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            Log.e("xx"," writeCachFile  fileName=" +fileName) ;
            File folder = new File(f.getParent());
            if (!folder.exists()) {
                folder.mkdirs();
            }
            f.createNewFile();
        }
        FileUtils.writeFileFromString(f, content, true);
    }

    protected void writeCache(String content) {
        synchronized (wLock) {
            contentList.add(content + "\n");
        }
        unWLock();
    }

    private void startWriteService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isStop()) return;
                    ArrayList<String> cList = new ArrayList<String>();
                    synchronized (wLock) {
                        cList.addAll(contentList);
                        contentList.clear();
                    }
                    if (cList.size() > 0) {
                        for (int i = 0; i < cList.size(); i++) {
                            if (isPause()) break;
                            if (isStop()) return;
                            String content = cList.get(i);
                            try {
                                writeCachFile(content);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (isStop()) return;
                        wLock();
                    }
                }
            }
        }).start();
    }

    private boolean isPause() {
        synchronized (wLock) {
            return isPause;
        }
    }

    private void wLock() {
        synchronized (wLock) {
            try {
                wLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void unWLock() {
        synchronized (wLock) {
            wLock.notify();
        }
    }


    private void rLock() {
        synchronized (rLock) {
            try {
                rLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void unRLock() {
        synchronized (rLock) {
            rLock.notify();
        }
    }

}
