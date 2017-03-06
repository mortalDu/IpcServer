package com.mududu.communicate.socket;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by niebin on 2016/11/16.
 */
public class SocketHelper {
    private static SocketHelper instance;
    Socket socket=null;
    ServerSocket serverSocket=null;
    private BlockingQueue queue = new ArrayBlockingQueue(100);
    boolean stop=false;
    private  SocketHelper() {
    }
    public static  SocketHelper getInstance(){
       if(isNull()){
           instance=new SocketHelper();
       }
        return instance;
    }
    private static boolean isNull(){
        synchronized (SocketHelper.class) {
            if(instance==null)return true;
            return false;
        }
    }
    //客户端发送消息
    public void sendMessage(Object o){
        if(o==null||socket==null){
            return;
        }
        String content=  JSON.toJSONString( o);
        queue.offer(content) ;
    }
    //客户端连接服务器
    public void connectServerWithThread(final String ip){
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        //创建DatagramSocket对象并指定一个端口号，注意，如果客户端需要接收服务器的返回数据,
                        //还需要使用这个端口号来receive，所以一定要记住
                        socket = new Socket(ip, 1989);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("xxx"," sendMessageThread 2222 socket=null is " + (socket == null));
                    while(true) {
                        String content = null;
                        try {
                            content = (String) queue.take();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e("xxx"," sendMessage content=" +content );
                        try {
                            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                            writer.writeUTF(content);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
    }
    public void initServerWithThread(final IDataReceive iData){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerReceviedByTcp(iData);
            }
        }).start();
    }
    public void ServerReceviedByTcp(IDataReceive iData) {
        // 声明一个ServerSocket对象
        try {
            // 创建一个ServerSocket对象，并让这个Socket在1989端口监听
         serverSocket= new ServerSocket(1989);
            // 调用ServerSocket的accept()方法，接受客户端所发送的请求，
            // 如果客户端没有发送数据，那么该线程就停滞不继续
            while (true) {
                Socket socket = serverSocket.accept();
                // 从Socket当中得到InputStream对象
                Log.e("xxx", "ServerReceviedByTcp  getsocket");
                dealSocket(socket,iData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setStop(boolean s){
        synchronized (SocketHelper.instance) {
            stop=s;
        }
    }
    public boolean isStop(){
        synchronized (SocketHelper.instance) {
            return stop;
        }
    }
    public void stopServer(){
        stop=false;
    }
    private void dealSocket(final Socket socket, final IDataReceive iData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream reader ;
                try {
                    // 获取读取流
                    reader = new DataInputStream( socket.getInputStream());
                    while (true) {
                        // 读取数据
                        String msg = reader.readUTF();
                        if(iData!=null){
                                iData.onDataReceive(msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
