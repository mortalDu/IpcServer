package com.mududu.communicate.ipc.impl;

import com.mududu.communicate.ipc.IpcClient;

public interface IInitCompleteListener {
	public void OnComplete(IpcClient.ConnectStatus state);
}
