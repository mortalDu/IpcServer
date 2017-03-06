package com.mududu.communicate.ipc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoBroadcast extends BroadcastReceiver{
    public static final String ACTION = "_clientbroadcast" ;
	@Override
	public void onReceive(final Context context, final Intent intent) {
		final String action=intent.getAction();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(IpcClient.getInstance().getConnectStatus() == IpcClient.ConnectStatus.UNCONNECT) {
//                    RemoteClient.getInstance().init(context.getApplicationContext(), "com.mududu.testa");
//                    String serverName = intent.getStringExtra(IpcClient.SERVICE_PACKAGENAME);
//                    IpcClient.getInstance().init(context.getApplicationContext(), null);
                }
            }
        }).start();
	}

}
