package com.mududu.communicate.ipc.bean;

/**
 * Created by tiger on 2016/9/22.
 */

public class Result {
    public static final int SUCESS = -1 ;
    public static final int ERROR_UNCONNECT = 0 ;
    public static final int ERROR_NO_RESPONSE = 1 ;
    private int resultCode = SUCESS;
    private String errorMsg ;
    public Result(){

    }

    public Result(int resultCode){
        this.resultCode = resultCode ;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
