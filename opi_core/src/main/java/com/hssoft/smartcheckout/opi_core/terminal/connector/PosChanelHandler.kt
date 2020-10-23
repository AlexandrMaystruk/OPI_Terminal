package com.hssoft.smartcheckout.opi_core.terminal.connector

import java.io.DataOutputStream
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer

class PosChanelHandler(private val client: Socket) {

    constructor(ipAddress: String, inputPort: Int, timeout: Int) : this(Socket().apply {
        val address = InetSocketAddress(ipAddress, inputPort)
        connect(address, timeout)
    })

    private var running: Boolean = true
    private val writer: DataOutputStream = DataOutputStream(client.getOutputStream())


    fun read(): String? {
        running = true
        while (running) {
            try {
                return client.getInputStream().convertBytesToString()
            } catch (ex: Exception) {
                shutdown()
            }
        }
        return null
    }


    fun write(message: String) {
        try {
            if (!client.isOutputShutdown) {
                val messageBytes = message.toByteArray()
                writer.write(ByteBuffer.allocate(4).putInt(messageBytes.size).array())
                writer.write(messageBytes)
            }
        } catch (e: Exception) {
            println("Write message to device error")
        }
    }

    fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
        Thread.currentThread().interrupt()
    }

    private fun InputStream.convertBytesToString(): String? {
        var result = ByteArray(0)
        val buff = ByteArray(1024)
        var totalBytesRead = 0
        this.read(buff, 0, 4)
        val expectedBytes = ByteBuffer.wrap(buff).int
        if (expectedBytes == 0) return null
        var bytesRead: Int
        while (this.read(buff, 0, buff.size).also { bytesRead = it } > -1) {
            val tempResult = ByteArray(result.size + bytesRead)
            System.arraycopy(result, 0, tempResult, 0, result.size)
            System.arraycopy(buff, 0, tempResult, result.size, bytesRead)
            result = tempResult
            totalBytesRead += bytesRead
            if (totalBytesRead == expectedBytes) {
                break
            }
        }
        return String(result)
    }
}