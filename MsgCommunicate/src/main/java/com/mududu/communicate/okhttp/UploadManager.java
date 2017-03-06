package com.mududu.communicate.okhttp;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


import com.blankj.utilcode.utils.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import okhttp3.Call;

/**
 * Created by niebin on 2016/12/13.
 */
public class UploadManager {
    private final String TAG = "UploadManager";
    private static UploadManager instance;
    private Context mCtx;
    private HashMap<String, UploadUtil> tMap = new HashMap<>();
    private Object lock = new Object();
    private final String CACHE_ROOT = "RobotUpload";
    private boolean isPrepareUpload = false ;
    private final String UPLOAD_SUFFIX = "upload";
    private final String SEPARATOR = "_";
    private List<WriteData> writeDataCacheList = new ArrayList<>();

    public static UploadManager getInstance() {
        synchronized (UploadManager.class) {
            if (instance == null) {
                instance = new UploadManager();
            }
        }
        return instance;
    }

    public void init(Context ctx) {
        mCtx = ctx;
        prepareUpload();
    }

    public void writeContent(String type, String[] contents) {
        for(String content : contents) {
            writeContent(type, content);
        }
    }

    public void writeContent(String type, String content) {
        if (TextUtils.isEmpty(content)) return;
        if(isPrepareUpload) {
            synchronized (writeDataCacheList) {
                writeDataCacheList.add(new WriteData(type, content));
            }
            return ;
        }
        writeContentReal(type, content);
    }

    private void writeContentReal(String type, String content) {
        synchronized (lock) {
            addType(type);
            UploadUtil util = tMap.get(type);
            if (null != util) {
                util.writeCache(content);
            }
        }
    }

    private String getShortPkg() {
        String[] pkgs = mCtx.getPackageName().split("\\.");
        int len = pkgs.length;
        return (pkgs[len - 2] + pkgs[len - 1]);
    }

    private String getBasePath() {
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String name = sdPath + "/" + CACHE_ROOT + "/" + getShortPkg() ;
        return name;
    }

    private void addType(String type) {
        if (TextUtils.isEmpty(type)) return;

        if (!tMap.containsKey(type)) {
            Log.e(TAG, " --- addType --- type=" +type) ;
            UploadUtil util = new UploadUtil(mCtx, getBasePath(), type);
            try {
                util.writeHeader();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tMap.put(type, util);
        }
    }

    public void stopWrite(String type) {
        if (TextUtils.isEmpty(type)) return;
        if (tMap.containsKey(type)) {
            UploadUtil util = tMap.get(type);
            util.setStop(true);
            tMap.remove(type);
        }
    }

    private void prepareUpload() {
        isPrepareUpload = true ;
        File rootDir = new File(getBasePath());
        Log.e(TAG," rootDir=" + rootDir.getAbsolutePath() + "  exist=" + rootDir.exists());
        if(!rootDir.exists()) {
            startWriteServer();
            return;
        }

        File[] files = rootDir.listFiles();
        Log.e(TAG," files=null is " + (files == null)  + "  files.size=" + (files == null ? 0 : files.length));
        if(files == null || files.length == 0) {
            startWriteServer();
            return ;
        }
        showLog(files);
        List<File> preUploadFiles = new ArrayList<>();//还没有copy的文件
        List<File> uploadFiles = new ArrayList<>();//已经copy出来准备上传的文件
        for(File file : files) {
            if(!file.getName().endsWith(SEPARATOR + UPLOAD_SUFFIX)) {
                uploadFiles.add(file) ;
            } else {
                preUploadFiles.add(file);
            }
        }
        Log.e(TAG, " preUploadFiles --------------------------------- :");
        showListLog(preUploadFiles);
        Log.e(TAG, "\n  uploadFiles --------------------------------- :");
        showListLog(uploadFiles);
        if(uploadFiles.size() == 0 && preUploadFiles.size() == 0) {
            startWriteServer();
            return ;
        }
        uploadFile(uploadFiles);
    }

    private void showLog(File[] files) {
        for(File file : files) {
            Log.e(TAG, " ---- getName=" + file.getName() + "  getAbsolutePath=" +file.getAbsolutePath());
        }
    }

    private void showListLog(List<File> files) {
        for(File file : files) {
            Log.e(TAG, " ---- getName=" + file.getName() + "  getAbsolutePath=" +file.getAbsolutePath());
        }
    }

    private void startWriteServer() {
        isPrepareUpload = false ;
        writeCacheContent();
    }

    private void writeCacheContent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {//对并发做的延迟处理
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for(WriteData data : writeDataCacheList) {
                    writeContentReal(data.type, data.content);
                }
                writeDataCacheList.clear();
            }
        }).start();
    }

    private void uploadFile(final List<File> preUploadFiles){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (File file : preUploadFiles) {
                        String fileName = System.currentTimeMillis() + SEPARATOR + file.getName() + SEPARATOR + UPLOAD_SUFFIX;
                        Log.e(TAG, " uploadFile " + file.getName() + "renameto fileName=" +fileName);
                        file.renameTo(new File(getBasePath(), fileName));
                    }
                    File rootDir = new File(getBasePath());
                    if (!rootDir.exists())
                        return;

                    File[] files = rootDir.listFiles();
                    if (files == null || files.length == 0) {
                        return;
                    }
                    Log.e(TAG, " uploadFile ---------------------------------start :");
                    showLog(files);
                    for (File file : files) {
                        upload(file, true);
                    }
                } finally {
                    startWriteServer();
                }
            }
        }).start();;
    }

    private void upload(final File cFile, final boolean isDef) {
        if (cFile == null) return;
        OkHttpUtils.post()
                .addFile("mFile", cFile.getName(), cFile)
                .url(UploadUtil._URL_)
                .addHeader("Content-Type", "multipart/form-data")
                .build()//
                .execute(new FileCallBack(cFile.getParent(), cFile.getName()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, " onError ---" + cFile.getName() + " error:"+ e);
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        if (response != null && isDef) {
                            response.delete();
                        }
                    }
                });
    }

    class WriteData {
        String type ;
        String content ;

        WriteData(String type, String content) {
            this.type = type ;
            this.content = content ;
        }
    }

}
