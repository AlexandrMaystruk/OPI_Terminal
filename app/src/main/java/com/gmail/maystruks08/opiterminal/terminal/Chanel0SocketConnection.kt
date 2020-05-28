package com.gmail.maystruks08.opiterminal.terminal

import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer

private const val SOCKET_CONNECT_TIMEOUT = 4000

class ClientSocketConnection {

    private val tag = "ClientSocketConnection"

    private var sendDataSocket: Socket? = null
    private var operationTime = 0L
    private var bufferOut: DataOutputStream? = null
    private var bufferIn: DataInputStream? = null
    private var disconnecting = false

    fun openSendConnection(ipAddress: String?, port: Int?): Socket? {
        val beginTicks: Long = System.currentTimeMillis()
        Log.d(tag, "Open connection started")

        closeConnection()

        // Check IP and Port not empty
        if (ipAddress.isNullOrEmpty() || port == null || port == 0) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Error serverIpAddress or serverPort is empty.")
            Log.d(tag, "Method openConnection() finished. Operation time $operationTime ms")
            return null
        }

        return try {
            val address = InetSocketAddress(ipAddress, port)
            Log.d(tag, "Connecting..")
            //create a socket to make the connection with the server
            sendDataSocket = Socket()
            sendDataSocket?.connect(address, SOCKET_CONNECT_TIMEOUT)
            if (sendDataSocket?.isConnected == true) {
                Log.d(tag, "Socket connected $ipAddress : $port")
                operationTime = System.currentTimeMillis() - beginTicks
                Log.d(tag, "Method openConnection() finished. Operation time $operationTime ms")
                sendDataSocket
            } else {
                Log.d(tag, "Connect socket error $ipAddress : $port")
                operationTime = System.currentTimeMillis() - beginTicks
                Log.d(tag, "Method openConnection() finished. Operation time $operationTime ms")
                null
            }
        } catch (socketException: SocketException) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Socket exception while connecting $socketException")
            Log.d(tag, "Method openConnection() finished. Operation time $operationTime ms")
            null
        } catch (e: Exception) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Unknown exception while connecting $e")
            Log.d(tag, "Method openConnection() finished. Operation time $operationTime ms")
            null
        }
    }

    fun sendData(message: String): Boolean {
        val beginTicks: Long = System.currentTimeMillis()
        if (sendDataSocket != null && sendDataSocket?.isConnected == true) {
            if (sendDataSocket != null) {
                operationTime = System.currentTimeMillis() - beginTicks
                Log.d(tag, "Send data socket is null")
                Log.d(tag, "Method sendData() finished. Operation time $operationTime ms")
                return false
            }

            bufferOut = DataOutputStream(sendDataSocket!!.getOutputStream())
            if (bufferOut != null) {
                val out = bufferOut!!
                return try {
                    Log.d(tag, "Send data $message")
                    synchronized(out) {
                        out.write(ByteBuffer.allocate(4).putInt(message.toByteArray().size).array())
                        out.write(message.toByteArray())
                    }
                    operationTime = System.currentTimeMillis() - beginTicks
                    Log.d(tag, "Method sendData() finished. Operation time $operationTime ms")
                    true
                } catch (e: IOException) {
                    operationTime = System.currentTimeMillis() - beginTicks
                    Log.d(tag, "Send data error: " + e.message)
                    Log.d(tag, "Method sendData() finished. Operation time $operationTime ms")
                    false
                }
            }
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Method sendData() finished. Operation time $operationTime ms")
            return false
        }
        operationTime = System.currentTimeMillis() - beginTicks
        Log.d(tag, "Method sendData() finished. Operation time $operationTime ms")
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
                Log.d(tag, "Trying to read " + expectedBytes + "  bytes from socket " + socket.inetAddress)
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
        return String(result)
    }

    private fun closeConnection(): Boolean {
        Log.d(tag, "Close connection started")
        val beginTicks: Long = System.currentTimeMillis()
        // Check that socket not exists
        if (sendDataSocket == null) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Socket not exist")
            Log.d(tag, "Method closeConnection() finished. Operation time $operationTime ms")
            return false
        }
        return try {
            if (this.sendDataSocket != null && this.sendDataSocket?.isClosed == false) {
                this.sendDataSocket?.close()
            }
            bufferOut?.flush()
            bufferOut?.close()
            bufferIn?.close()
            bufferOut = null
            bufferIn = null

            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Close socket successful")
            Log.d(tag, "Method closeConnection() finished. Operation time $operationTime ms")
            true
        } catch (socketException: SocketException) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Socket exception while disconnecting $socketException")
            Log.d(tag, "Method closeConnection() finished. Operation time $operationTime ms")
            false
        } catch (e: Exception) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Unknown exception while disconnecting $e")
            Log.d(tag, "Method closeConnection() finished. Operation time $operationTime ms")
            false
        }
    }


    private fun disconnect() {
        if (!this.disconnecting) {
            disconnecting = true
            Log.d(tag, "Trying to disconnect")
            if (this.sendDataSocket != null && this.sendDataSocket?.isClosed == false) {
                try {
                    this.sendDataSocket?.shutdownInput()
                    this.sendDataSocket?.close()
                } catch (var2: IOException) {
                    Log.d(tag, "Unable to close socket $var2")
                }
                this.sendDataSocket = null
            }
            disconnecting = false
        }
    }
}