package com.example.nettydemo.util;

import java.net.InetSocketAddress;

public class IpUtil {

    public static String getIp(InetSocketAddress netSocketAddress) {
        return netSocketAddress.getAddress().getHostAddress();
    }

}
