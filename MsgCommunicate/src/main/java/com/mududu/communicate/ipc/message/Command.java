package com.mududu.communicate.ipc.message;

import android.text.TextUtils;

public class Command {
	private String service = "";
	//加入operate==""，那么表示接收所有的operate
	private String operation = "";
	public final static String ALL_OPERATE = "" ;
	public final static int TYPE_ALL = 0 ; //all
	public final static int TYPE_ONLY_START = 1 ;//仅在开始时接收的命令
	public final static int TYPE_ONLY_RUNING = 2 ;//仅在运行中接收的命令
	private int type ;
	
	public Command(String service, String operation){
		this(service, operation, TYPE_ALL);
	}
	
	public Command(String service, String operation, int type){
		this.service = service ;
		this.operation = operation ;
		this.type = type ;
	}
	
	public void setService(String service) {
		if(TextUtils.isEmpty(service)) {
			return ;
		}
		this.service = service ;
	}
	
	public String getService() {
		return service ;
	}
	
	public void setOperation(String operation) {
		if(TextUtils.isEmpty(operation)) {
			return ;
		}
		this.operation = operation ;
	}
	
	public String getOperation() {
		return operation ;
	}
	
	public boolean isAcceptAllOperate() {
		return TextUtils.isEmpty(operation);
	}
	
	@Override
	public boolean equals(Object cmd) {//判断service和operate是否相同
		// TODO Auto-generated method stub
		if(cmd instanceof Command) {
			if(((Command) cmd).getService().equals(service) &&
					((Command) cmd).getOperation().equals(operation) &&
					((Command) cmd).getType() == type)
				return true ;
			return false ;
		} else {
			return false ;
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "command:[service:" +service + " operation:" +operation + " type:" + type + "]";
	}
	
	public int getType() {
		return type ;
	}
	
	public void setType(int type) {
		this.type = type ;
	}
}
