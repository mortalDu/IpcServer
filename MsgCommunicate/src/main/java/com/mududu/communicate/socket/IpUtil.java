package com.mududu.communicate.socket;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by niebin on 2016/11/16.
 */
public class IpUtil {
    public static String getHostIP() {

        String hostIp = null;
        Enumeration nis = null;
        try {
            nis = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress ia = null;
        while (nis.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) nis.nextElement();
            Enumeration<InetAddress> ias = ni.getInetAddresses();
            while (ias.hasMoreElements()) {
                ia = ias.nextElement();
                if (ia instanceof Inet6Address) {
                    continue;// skip ipv6
                }
                String ip = ia.getHostAddress();
                if (!"127.0.0.1".equals(ip)) {
                    hostIp = ia.getHostAddress();
                    break;
                }
            }
        }
        return hostIp;
    }
}
