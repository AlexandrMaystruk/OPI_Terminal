package com.hssoft.smartcheckout.opi_core.terminal.connector

import java.util.logging.Level
import java.util.logging.Logger
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer


class Chanel0SocketConnector {

    private val tag = "ClientSocketConnection"

    private var sendDataSocket: Socket? = null
    private var operationTime = 0L
    private var bufferOut: DataOutputStream? = null
    private var disconnecting = false
    private var logger = Logger.getLogger(tag)

    fun openSendConnection(ipAddress: String?, port: Int?, timeout: Int = 4000): Socket? {
        val beginTicks: Long = System.currentTimeMillis()
        logger.log(Level.INFO, "Open connection started")
        // Check IP and Port not empty
        if (ipAddress.isNullOrEmpty() || port == null || port == 0) {
            operationTime = System.currentTimeMillis() - beginTicks
            logger.log(Level.INFO,"Error serverIpAddress or serverPort is empty.")
            logger.log(Level.INFO,"Method openConnection() finished. Operation time $operationTime ms")
            return null
        }

        return try {
            val address = InetSocketAddress(ipAddress, port)
            logger.log(Level.INFO,"Connecting..")
            //create a socket to make the connection with the server
            sendDataSocket = Socket()
            sendDataSocket?.connect(address, timeout)
            if (sendDataSocket?.isConnected == true) {
                logger.log(Level.INFO,"Socket connected $ipAddress : $port")
                operationTime = System.currentTimeMillis() - beginTicks
                logger.log(Level.INFO,"Method openConnection() finished. Operation time $operationTime ms")
                sendDataSocket
            } else {
                logger.log(Level.INFO,"Connect socket error $ipAddress : $port")
                operationTime = System.currentTimeMillis() - beginTicks
                logger.log(Level.INFO,"Method openConnection() finished. Operation time $operationTime ms")
                null
            }
        } catch (socketException: SocketException) {
            operationTime = System.currentTimeMillis() - beginTicks
            logger.log(Level.INFO,"Socket exception while connecting $socketException")
            logger.log(Level.INFO,"Method openConnection() finished. Operation time $operationTime ms")
            null
        } catch (e: Exception) {
            operationTime = System.currentTimeMillis() - beginTicks
            logger.log(Level.INFO,"Unknown exception while connecting $e")
            logger.log(Level.INFO,"Method openConnection() finished. Operation time $operationTime ms")
            null
        }
    }

    fun sendData(message: String): Boolean {
        val beginTicks: Long = System.currentTimeMillis()
        if (sendDataSocket != null && sendDataSocket?.isConnected == true) {
            bufferOut = DataOutputStream(sendDataSocket!!.getOutputStream())
            if (bufferOut != null) {
                val out = bufferOut!!
                return try {
                    synchronized(out) {
                        val messageBytes = message.toByteArray()
                        out.write(ByteBuffer.allocate(4).putInt(messageBytes.size).array())
                        out.write(messageBytes)
                        logger.log(Level.INFO,"Send data $message")
                    }
                    operationTime = System.currentTimeMillis() - beginTicks
                    logger.log(Level.INFO,"Method sendData() finished. Operation time $operationTime ms")
                    true
                } catch (e: IOException) {
                    operationTime = System.currentTimeMillis() - beginTicks
                    logger.log(Level.INFO,"Send data error: " + e.message)
                    logger.log(Level.INFO,"Method sendData() finished. Operation time $operationTime ms")
                    false
                }
            }
            operationTime = System.currentTimeMillis() - beginTicks
            logger.log(Level.INFO,"Method sendData() finished. Operation time $operationTime ms")
            return false
        }
        operationTime = System.currentTimeMillis() - beginTicks
        logger.log(Level.INFO,"Send data socket is null or closed")
        logger.log(Level.INFO,"Method sendData() finished. Operation time $operationTime ms")
        return false
    }

    @Throws(IOException::class)
    fun read(): String? {
        var result = ByteArray(0)
        val buff = ByteArray(1024)
        var totalBytesRead = 0
        if (sendDataSocket != null) {
            val socket = sendDataSocket!!
            synchronized(socket) {
                socket.getInputStream().read(buff, 0, 4)
                val expectedBytes = ByteBuffer.wrap(buff).int
                if (expectedBytes == 0) {
                    return null
                }
                logger.log(Level.INFO,"Trying to read " + expectedBytes + "  bytes from socket " + socket.inetAddress)
                var bytesRead: Int
                while (socket.getInputStream().read(buff, 0, buff.size).also { bytesRead = it } > -1) {
                    val tempResult = ByteArray(result.size + bytesRead)
                    System.arraycopy(result, 0, tempResult, 0, result.size)
                    System.arraycopy(buff, 0, tempResult, result.size, bytesRead)
                    result = tempResult
                    totalBytesRead += bytesRead
                    if (totalBytesRead == expectedBytes) {
                        break
                    }
                }
            }
        }
        logger.log(Level.INFO,"Read data: ${String(result)}")
        return String(result)
    }

    fun disconnect() {
        if (!this.disconnecting) {
            disconnecting = true
            logger.log(Level.INFO,"Trying to disconnect")
            if (this.sendDataSocket != null && this.sendDataSocket?.isClosed == false) {
                try {
                    this.sendDataSocket?.shutdownInput()
                    this.sendDataSocket?.close()
                } catch (var2: IOException) {
                    logger.log(Level.INFO,"Unable to close socket $var2")
                }
                this.sendDataSocket = null
            }
            disconnecting = false
        }
    }
}