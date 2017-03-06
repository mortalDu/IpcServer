package com.mududu.communicate.ipc.message;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * * {
	  "semantic": {
	    "slots": {
	      "type": "call"
	    }
	  }, 
	  "rc": 0, 
	  "operation": "SWITCH", 
	  "service": "mode", 
	  "text": "木嘟嘟切换到电话模式"
	}
	--------------------------------
	{
  "semantic": {
    "slots": {
      "contact": {
        "name": "张三"
      }
    }
  }, 
  "rc": 0, 
  "operation": "CALL", 
  "service": "telephone", 
  "text": "木嘟嘟帮我打电话给张三"
}

 * 
 * */
public class SemanticItem {
	public final static String SEMANTIC="semantic";
	public final static String SLOTS="slots";
	public final static String TYPE="type";
	public final static String CONTACT="contact";
	public final static String CONTENT="content";
	public final static String NAME="name";
	public final static String RC="rc";
	public final static String OPERATION="operation";
	public final static String SERVICE="service";
	public final static String TEXT="text";
	public final static String ANSWER="answer";
	public final static String DATA="data";
	public final static String WEB_PAGE="webPage";
	public final static String URL="url";
	public final static String RESULT="result";
	public final static String DOWN_LOAD_URL="downloadUrl";
	
	//问答
	public static final String SERVICE_OPENQA = "openQA";
	public static final String SERVICE_CALC = "calc";
	public static final String SERVICE_CHAT = "chat";
	public static final String SERVICE_DATETIME = "datetime";
	public static final String SERVICE_BAIKE = "baike";
	public static final String SERVICE_PHOTO = "picture";
	public static final String SERVICE_VIDEO = "video";
	public static final String SERVICE_CONTACT = "contact";
	public static final String SERVICE_HELP = "help";
	public static final String SERVICE_COMFERENCE = "conference";


	//天气
	public static final String SERVICE_WEATHER = "weather";
	//电话
	public static final String SERVICE_PHONE = "telephone";
	//巡航
	public static final String SERVICE_CRUISE = "free_cruise";
	//行走
	public static final String SERVICE_WALK = "walk";
	//视频通话时的服务
	public static final String SERVICE_VIDEOCHAT = "videochat";
	//音乐
	public static final String SERVICE_MUSIC = "music";
	//股票
	public static final String SERVICE_STOCK = "stock";
	//菜谱
	public static final String SERVICE_COOKBOOK = "cookbook";
	//翻译
	public static final String SERVICE_TRANSLATION = "translation";
	//随动
	public static final String SERVICE_FOLLOW = "follow";
	//通用
	public static final String SERVICE_COMMON = "common";
	//关闭
	public static final String OPERATION_CLOSE = "CLOSE";
	public static final String OPERATION_STOP = "STOP";
	public static final String OPERATION_YES = "YES";
	public static final String OPERATION_NO = "NO";
	
	public static final String OPERATION_START = "START";
	public static final String OPERATION_END = "END";
	public static final String OPERATION_FORWARD = "FORWARD";
	public static final String OPERATION_BACK = "BACK";
	public static final String OPERATION_LEFT = "LEFT";
	public static final String OPERATION_RIGHT = "RIGHT";
	public static final String OPERATION_CALL = "CALL";


	public static final String TYPE_VIDEO = "VIDEO";
	public static final String TYPE_PICTURE = "PICTURE";
	
	private String headerUrl;
	private String json ;
	private String semantic;
	private String slots;
	private String slotContent;
	private String answer;
	private String type;
	private int rc;
	private String operation;
	private String service;
	private String text;
	private String musicName;
	private String downloadUrl;
	private String result;
	private String content;
	private Command cmd ;

	//结构化数据
	private String data;
	
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	public String getSemantic() {
		return semantic;
	}
	public void setSemantic(String semantic) {
		this.semantic = semantic;
	}
	public String getSlots() {
		return slots;
	}
	public void setSlots(String slots) {
		this.slots = slots;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getRc() {
		return rc;
	}
	public void setRc(int rc) {
		this.rc = rc;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getSlotContent() {
		return slotContent;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "text="+text+" service="+service+" operation="+operation+" rc="+rc+" slotContent="+slotContent+" type="+type;
	}
	public void parseSlots(String json) throws JSONException{
		if(TextUtils.isEmpty(json)) {
			return ;
		}
		JSONObject oj=new JSONObject(json);
		if(oj.has(TYPE)){
			type = oj.getString(TYPE);
		}else if(oj.has(CONTACT)){
			String contact = oj.getString(CONTACT);
//			JSONObject cOj = new JSONObject(contact);
//			slotContent = cOj.getString(NAME);
			slotContent = contact;
		}else if (oj.has(CONTENT)) {
			content = oj.getString(CONTENT);
		}
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	public void parseResult(String json) throws JSONException{
		if(TextUtils.isEmpty(json)) {
			return ;
		}
		JSONObject oj=new JSONObject(json);
		if(oj.has(TYPE)){
			musicName = oj.getString(NAME);
		}
		if (oj.has(DOWN_LOAD_URL)) {
			downloadUrl = oj.getString(DOWN_LOAD_URL);
		}
	}
	
	public String getMusicName() {
		return musicName;
	}
	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	public String getHeaderUrl() {
		return headerUrl;
	}
	public void setHeaderUrl(String headerUrl) {
		this.headerUrl = headerUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

    public void setSlotContent(String slotContent) {
        this.slotContent = slotContent;
    }

    public Command getCmd() {
        return cmd;
    }

    public void setCmd(Command cmd) {
        this.cmd = cmd;

    }

    public Command getCommand(){
		synchronized (SemanticItem.this) {
			if(cmd == null) {
				cmd = new Command(service, operation);
			}
		}
		return cmd ;
	}

}