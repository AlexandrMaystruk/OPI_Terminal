package com.gmail.maystruks08.opi_core.connector

import java.io.DataOutputStream
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer

class PosChanelHandler(private val client: Socket, private val logger: OPILogger) {

    constructor(ipAddress: String, inputPort: Int, timeout: Int, logger: OPILogger) : this(Socket().apply {
        val address = InetSocketAddress(ipAddress, inputPort)
        connect(address, timeout)
    }, logger)

    private var running: Boolean = true
    private val writer: DataOutputStream = DataOutputStream(client.getOutputStream())

    fun read(): String? {
        running = true
        while (running && !client.isClosed) {
            try {
                val readiedMessage = client.getInputStream().convertBytesToString()
                readiedMessage?.let { logger.log("POS receive message: $it") }
                return readiedMessage
            } catch (e: Exception) {
                logger.logError(e, "POS read message error")
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
                logger.log("POS write message: $message")
            }
        } catch (e: Exception) {
            logger.logError(e, "POS write message to device error")
        }
    }

    fun shutdown() {
        if (running) {
            try {
                running = false
                writer.close()
                client.close()
                logger.log("POS ${client.inetAddress.hostAddress} closed the connection")
            } catch (e: Exception) {
                logger.log("POS ${client.inetAddress.hostAddress} closed the connection error")
            }
        }
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