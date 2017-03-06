package com.mududu.communicate.okhttp;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.blankj.utilcode.utils.ConstantsUtil;
import com.blankj.utilcode.utils.FileUtils;
import com.blankj.utilcode.utils.LogUtil;
import com.mududu.communicate.rx.RxBus;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

public class RobootHttpUtil {
	private final static String ROOT_URL= ConstantsUtil.BASE_URL;
	private final static String ROBOT_PATH=ROOT_URL+"/mududu/robot";
	private final static String USER_PATH=ROOT_URL+"/mududu/user";

	private final static String SYNTAX_PATH=ROBOT_PATH+"/syntax/get";
	private final static String CURRENT_PATH=ROBOT_PATH+"/getcurrentmode";

	private final static String VERSION_PATH=USER_PATH+"/getversion";
	private final static String LOGIN_PATH=ROOT_URL+"/login";
	private final static String REGISTER_PATH=ROOT_URL+"/register";

	private final static String ADD_URL= ROOT_URL +"/data/speak/add";
	private final static String ASK_URL=ConstantsUtil.BASE_URL + "/mududu/answer/get";
	private final static String SEMANTIC_TEXT="semantic_text";
	private final static String SEMANTIC_SERVICE="semantic_service";
	private final static String SEMANTIC_OPERATION="semantic_operation";
	private final static String SEMANTIC_TYPE="semantic_type";
	private final static String DEVICE_ID="report_did";
	private final static String REPORT_IP="report_ip";

	private final static String ROBOT_ID="robot_id";

	private final static String OFF_LINE_FOLDER="OffLine";
	private final static String CALL_FOLDER="Call";


	private static RobootHttpUtil instance;
	public static RobootHttpUtil getInstance(){
		synchronized (RobootHttpUtil.class) {
			if(instance==null){
                instance=new RobootHttpUtil();
            }
		}
		return instance;
	}
	public static void uploadSemanticMessage(String text,String service,String operation,String type){
	    	OkHttpUtils
		    .get()//
		    .url(ADD_URL)//
		    .addParams(SEMANTIC_TEXT, text)
		    .addParams(SEMANTIC_SERVICE,service)
		    .addParams(SEMANTIC_OPERATION,operation)
		    .addParams(SEMANTIC_TYPE,type)
		    .build()
		    .execute(new StringCallback(){
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
			}
			@Override
			public void onResponse(String arg0, int arg1) {
			}

                });
    }
	public static void getAskJson(String robot_id,final Handler handler, final int what){
		OkHttpUtils
				.get()
				.url(ASK_URL)
				.addParams("robot_id",robot_id)
				.build()
				.execute(new StringCallback(){
					@Override
					public void onError(Call arg0, Exception arg1, int arg2) {
					}
					@Override
					public void onResponse(String json, int arg1) {
						Message msg=new Message();
						msg.what=what;
						msg.obj=json;
						handler.sendMessage(msg);
					}

                });
    }

    public static void loginSip(String url, StringCallback callback) {
        OkHttpUtils
                .get()//
                .url(url)//
                .build()
                .execute(callback);
    }

    public static void uploadSemanticMessage(String text, String service, String operation, String type, String deviceId, String ip) {
        OkHttpUtils
                .get()//
                .url(ADD_URL)//
                .addParams(SEMANTIC_TEXT, text)
                .addParams(SEMANTIC_SERVICE, service)
                .addParams(SEMANTIC_OPERATION, operation)
                .addParams(SEMANTIC_TYPE, type)
                .addParams(DEVICE_ID, deviceId)
                .addParams(REPORT_IP, ip)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                    }

                    @Override
                    public void onResponse(String arg0, int arg1) {
                    }

                });
    }

	public   void getSyntax(String robotId, final IDownFileDone done){
		final Object lock=new Object();
		OkHttpUtils
				.get()
				.url(SYNTAX_PATH)
				.addParams(ROBOT_ID,robotId)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						synchronized (lock) {
							lock.notify();
						}
					}
					@Override
					public void onResponse(String response, int id) {
					LogUtil.logObject(this,"response="+response);
						try {
							JSONObject job=new JSONObject(response);
							JSONObject jD=job.getJSONObject("data");
							String callUrl=jD.getString("grammar");
							JSONArray sem=jD.getJSONArray("semantics");
							String[] urls=new String[sem.length()];
							for (int i=0;i<sem.length();i++){
								urls[i]=sem.getString(i);
							}
							String offPath=downloadFiles(urls,OFF_LINE_FOLDER);
							if(done!=null){
								done.onOffLineDone(offPath);
							}
							String callPath=downloadFile(callUrl,CALL_FOLDER);
							if(done!=null){
								done.onCallDone(callPath);
								done.onDone();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						synchronized (lock) {
							lock.notify();
						}
					}
				});
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
		 public static String downloadFile(final String url, final String folder){
			 if(url==null||"".equals(url)||"null".equals(url)){
				 return "";
			 }
			 final Object lock=new Object();
			 String name=getFileName(url);
			 final String fPath=FileUtils.createFileVoicePath(folder,name);

			 OkHttpUtils
					 .get()
					 .url(url)
					 .build()
					 .execute(new Callback() {
						 @Override
						 public Object parseNetworkResponse(Response response, int id) throws Exception {
							 try {
								 File f=new File(fPath);
								 BufferedSink sink = Okio.buffer(Okio.sink(f));
								 sink.writeAll(response.body().source());
								 sink.close();
							 } catch (IOException e) {
								 e.printStackTrace();
							 }
							 synchronized (lock) {
								 lock.notify();
							 }
							 return null;
						 }

						 @Override
						 public void onError(Call call, Exception e, int id) {
							 synchronized (lock) {
								 lock.notify();
							 }
						 }

						 @Override
						 public void onResponse(Object response, int id) {

						 }
					 });
			 synchronized (lock) {
				 try {
					 lock.wait();
				 } catch (InterruptedException e) {
					 e.printStackTrace();
				 }
			 }
			 return fPath;
		 }

	public  String  downloadFiles(final String[] urls, final String folder){
			final Object lock=new Object();
				if(urls==null||urls.length<=0){
					return "";
				}
				 String path=FileUtils.getFileVoiceFolderPath(folder);
				for(int i=0;i<urls.length;i++) {
					final String url = urls[i];
					if(url==null||"".equals(url)){
						path="";
						synchronized (lock) {
							lock.notify();
						}
						break;
					}
					OkHttpUtils
							.get()
							.url(url)
							.build()
							.execute(new Callback() {
								@Override
								public Object parseNetworkResponse(Response response, int id) throws Exception {

									try {
										String name = getFileName(url);
										File f = FileUtils.createFileVoice(folder,name);
										BufferedSink sink = Okio.buffer(Okio.sink(f));
										sink.writeAll(response.body().source());
										sink.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
									synchronized (lock) {
										lock.notify();
									}
									return null;
								}

								@Override
								public void onError(Call call, Exception e, int id) {
									synchronized (lock) {
										lock.notify();
									}
								}

								@Override
								public void onResponse(Object response, int id) {

								}
							});
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
		return path;
	}
	public String readString(File f){
		Source source = null;
		String content="";
		BufferedSource bufferedSource = null;
		try {
			source = Okio.source(f);
			bufferedSource = Okio.buffer(source);
			content = bufferedSource.readUtf8();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(bufferedSource!=null){
                    bufferedSource.close();
                }
				if(source!=null){
                        source.close();
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	private static String getFileName(String url){
		if(url==null)return "";
		String[] ns=url.split("/");
		return ns[ns.length-1];
	}
	public String getOffLineFodler(){
			String path=FileUtils.getFileVoiceFolderPath(OFF_LINE_FOLDER);
		return path;
	}
	public String getCallFolder(){
		String path=FileUtils.getFileVoiceFolderPath(CALL_FOLDER);
		return path;
	}
	public interface IDownFileDone{
		void onOffLineDone(String folder);
		void onCallDone(String filepath);
		void onDone();
	}
}
