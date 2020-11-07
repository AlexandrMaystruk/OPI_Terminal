package com.gmail.maystruks08.opi_core

import java.io.PrintWriter
import java.io.StringWriter
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.text.SimpleDateFormat
import java.util.*

const val SERVER_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

fun Date.toServerUTCFormat(): String = SimpleDateFormat(SERVER_UTC_FORMAT, Locale.getDefault()).format(this)

val Exception.stackTraceString: String
    get() {
        val stringWriter = StringWriter()
        this.printStackTrace(PrintWriter(stringWriter))
        return this.message + "\n" + stringWriter.toString()
    }


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


fun asyncWithCatchException(block: () -> Unit): Thread {
    return Thread {
        runWithCatchException { block() }
    }.also { it.start() }
}

fun runWithCatchException(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) { /*Not need handle exception*/
    }
}