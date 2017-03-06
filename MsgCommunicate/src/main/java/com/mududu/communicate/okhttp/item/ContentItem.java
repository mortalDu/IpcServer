package com.mududu.communicate.okhttp.item;

/**
 * Created by niebin on 2016/12/8.
 * content:{
 st:0
 errcode:0
 result:{

 }
 start_at:xxxxx
 end_at:xxxxx
 }
 errocode说明
 */
public class ContentItem {
    public static final int ERROR = -1 ;
    public static final int WAKEUP = 0 ;
    public static final int INIT = 1 ;
    public static final int OFFLINE_RECONG = 2 ;
    public static final int INLINE_RECONG = 3 ;
    public static final int UPDATE_CONFERRENCE = 4 ;
    public static final int SUCESS = 0 ;
    public static final int ERROR_LOCAL_FILE = 1001 ;  //离线文件识别错误
    public static final int ERROR_LOCAL_AUDIO = 1002 ; //离线语音识别错误

    public int getSt() {
        return st;
    }

    public void setSt(int st) {
        this.st = st;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getStart_at() {
        return start_at;
    }

    public void setStart_at(long start_at) {
        this.start_at = start_at;
    }

    public long getEnd_at() {
        return end_at;
    }

    public void setEnd_at(long end_at) {
        this.end_at = end_at;
    }

    private int st= 0;
    private int errcode=0;
    private String result;
    private long start_at=0;
    private long end_at=0;
}
