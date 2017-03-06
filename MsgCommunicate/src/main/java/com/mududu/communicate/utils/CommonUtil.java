package com.mududu.communicate.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import org.json.JSONArray;

import java.util.List;

public class CommonUtil {
	public static boolean isNull(Object obj) {
		if (obj == null)
			return true;
		if (obj instanceof String) {
			String s = (String) obj;
			if (s.equals(""))
				return true;
		} else if (obj instanceof List) {
			List l = (List) obj;
			if (l.size() == 0)
				return true;
		} else if (obj instanceof JSONArray) {
			JSONArray jA = (JSONArray) obj;
			if (jA.length() == 0)
				return true;
		}
		return false;
	}

	public static Intent createExplicitFromImplicitIntent(Context context,
			Intent implicitIntent) {
		// Retrieve all services that can match the given intent
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent,
				0);
		// Make sure only one match was found
		if (resolveInfo == null || resolveInfo.size() != 1) {
			return null;
		}
		// Get component info and create ComponentName
		ResolveInfo serviceInfo = resolveInfo.get(0);
		String packageName = serviceInfo.serviceInfo.packageName;
		String className = serviceInfo.serviceInfo.name;
		ComponentName component = new ComponentName(packageName, className);

		// Create a new intent. Use the old one for extras and such reuse
		Intent explicitIntent = new Intent(implicitIntent);

		// Set the component to be explicit
		explicitIntent.setComponent(component);
		return explicitIntent;
	}

	public static String getDeviceId(Context context) {
		TelephonyManager tManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tManager.getDeviceId();
		if (deviceId != null)
			return deviceId;
		String android_id = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		return android_id;
	}

	public static String getIp(Context context) {
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = formatIpAddress(ipAddress);
		return ip;
	}

	private static String formatIpAddress(int ipAdress) {
		return (ipAdress & 0xFF) + "." + ((ipAdress >> 8) & 0xFF) + "."
				+ ((ipAdress >> 16) & 0xFF) + "." + (ipAdress >> 24 & 0xFF);
	}
}
