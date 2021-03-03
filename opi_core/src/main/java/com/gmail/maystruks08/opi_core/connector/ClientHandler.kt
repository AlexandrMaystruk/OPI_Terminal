package com.gmail.maystruks08.opi_core.connector

import com.gmail.maystruks08.opi_core.runOrElse
import java.io.DataOutputStream
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer

class ClientHandler(
    private val client: Socket,
    private val logger: OPILogger
) {

    constructor(
        ipAddress: String,
        inputPort: Int,
        connectTimeout: Int,
        readWriteTimeout: Int,
        logger: OPILogger
    ) : this(Socket().apply {
        val address = InetSocketAddress(ipAddress, inputPort)
        logger.log("Start connect to terminal: $address")
        try {
            connect(address, connectTimeout)
            soTimeout = readWriteTimeout
        } catch (e: Exception) {
            logger.log("Connect to terminal: $address error ${e.localizedMessage}")
            throw e
        }
    }, logger)

    private val writer: DataOutputStream? by lazy {
        runOrElse(
            { DataOutputStream(client.getOutputStream()) },
            { logger.log("Can't get output stream") })
    }
    private val reader: InputStream? by lazy {
        runOrElse(
            { client.getInputStream() },
            { logger.log("Can't get input stream") })
    }


    fun read(): String? {
        try {
            reader ?: throw RuntimeException("Reader is null")
            while (!client.isInputShutdown) {
                val readiedMessage = reader?.convertBytesToString()
                readiedMessage?.let { logger.log("$TAG receive message: $it") }
                return readiedMessage
            }
        } catch (e: Exception) {
            logger.log("Read message error ${e.localizedMessage}")
            shutdown()
        }
        return null
    }

    fun write(message: String) {
        try {
            if (client.isOutputShutdown) return
            val messageBytes = message.toByteArray()
            writer ?: throw RuntimeException("Writer is null")
            writer?.write(ByteBuffer.allocate(4).putInt(messageBytes.size).array())
            writer?.write(messageBytes)
            logger.log("Write message: $message")
        } catch (e: Exception) {
            logger.log("Write message to device error ${e.localizedMessage};")
        }
    }

    fun shutdown() {
        try {
            if (!client.isInputShutdown) client.shutdownInput()
            if (!client.isOutputShutdown) client.shutdownOutput()
            if (!client.isClosed) client.close()
            logger.log("${client.inetAddress?.hostAddress} closed the connection")
        } catch (e: NullPointerException) {
            logger.log("Closed the connection error, null pointer exception")
        } catch (e: Exception) {
            logger.log("Closed the connection error")
        } catch (t: Throwable) {
            logger.log("Closed the connection error ${t.localizedMessage} ")
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
            if (totalBytesRead == expectedBytes) break
        }
        return String(result)
    }

    companion object {
        private const val TAG = "CASH REGISTER:"
    }
}