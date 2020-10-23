package com.hssoft.smartcheckout.opi_core.terminal

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


fun getLocalIpAddress(): String? {
    try {
        val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val networkInterface: NetworkInterface = en.nextElement()
            val enumIpAddress: Enumeration<InetAddress> = networkInterface.inetAddresses
            while (enumIpAddress.hasMoreElements()) {
                val internetAddress = enumIpAddress.nextElement()
                if (!internetAddress.isLoopbackAddress && internetAddress is Inet4Address) {
                    return internetAddress.hostAddress
                }
            }
        }
    } catch (e: SocketException) {
        e.printStackTrace()
    }
    return null
}