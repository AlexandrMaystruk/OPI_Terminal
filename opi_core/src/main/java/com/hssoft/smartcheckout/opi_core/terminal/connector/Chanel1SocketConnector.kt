package com.hssoft.smartcheckout.opi_core.terminal.connector

import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.*
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.logging.Level
import java.util.logging.Logger

class Chanel1SocketConnector {

    private val tag = "ServerSocketConnection"
    private var operationTime = 0L

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var logger = Logger.getLogger(tag)


    private val connectionQueue: Queue<ConnectionInformation?> = ArrayBlockingQueue(10)

    fun receiveData(receivePort: Int?, onDataReceived: (String) -> Unit) {
        val beginTicks: Long = System.currentTimeMillis()
        if (receivePort == null || receivePort == 0) {
            operationTime = System.currentTimeMillis() - beginTicks
            logger.log(Level.INFO,"Error receivePort is null or 0.")
            logger.log(Level.INFO,"Method receiveData() finished. Operation time $operationTime ms")
            return
        }
        if (closeConnection()) {
            try {
                serverSocket = ServerSocket(receivePort, 0, InetAddress.getByName(getLocalIpAddress()))
                logger.log(Level.INFO,"Waiting for incoming connection on ${getLocalIpAddress()} : $receivePort")
                if(serverSocket?.isClosed == false){
                    while (true) {
                        clientSocket = serverSocket?.accept()

                        if (clientSocket != null && clientSocket?.isConnected == true) {
                            val inputStream = clientSocket!!.getInputStream()
                            val outputStream = clientSocket!!.getOutputStream()
                            connectionQueue.add(ConnectionInformation(inputStream, outputStream))
                            DataInputStream(inputStream).use {
                                while (clientSocket!!.isConnected) {
                                    val serverMessage = it.readUTF()
                                    if(serverMessage.isNotEmpty()){
                                        onDataReceived(serverMessage)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    logger.log(Level.INFO,"Server socket is closed!!!")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun write(message: String): Boolean {
        val connectionInformation = connectionQueue.remove()
        return try {
            val out = connectionInformation?.channel1OutputStream!!
            synchronized(out) {
                val messageBytes = message.toByteArray()
                out.write(ByteBuffer.allocate(4).putInt(messageBytes.size).array())
                out.write(messageBytes)
                logger.log(Level.INFO, "Send data $message")
            }
            logger.log(Level.INFO, "Method sendData() finished. Operation time $operationTime ms")
            true
        } catch (e: IOException) {
            logger.log(Level.INFO, "Failed to send message to terminal using channel 1: no OutputStream available")
            logger.log(Level.INFO, "Method sendData() finished. Operation time $operationTime ms")
            false
        }
    }

    fun closeConnection(): Boolean {
        logger.log(Level.INFO,"Close connection started")
        val beginTicks: Long = System.currentTimeMillis()
        // Check that socket not exists

        return try {
            clientSocket?.close()
            serverSocket?.close()
            serverSocket = null
            clientSocket = null

            operationTime = System.currentTimeMillis() - beginTicks
            logger.log(Level.INFO,"Close socket successful")
            logger.log(Level.INFO,"Method closeConnection() finished. Operation time $operationTime ms")
            true
        } catch (socketException: SocketException) {
            operationTime = System.currentTimeMillis() - beginTicks
            logger.log(Level.INFO,"Socket exception while disconnecting $socketException")
            logger.log(Level.INFO,"Method closeConnection() finished. Operation time $operationTime ms")
            false
        } catch (e: Exception) {
            operationTime = System.currentTimeMillis() - beginTicks
            logger.log(Level.INFO,"Unknown exception while disconnecting $e")
            logger.log(Level.INFO,"Method closeConnection() finished. Operation time $operationTime ms")
            false
        }
    }

    internal data class ConnectionInformation(
        val channel1InputStream: InputStream,
        val channel1OutputStream: OutputStream
    )

    private fun getLocalIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkInterface: NetworkInterface = en.nextElement()
                val enumIpAddress: Enumeration<InetAddress> = networkInterface.inetAddresses
                while (enumIpAddress.hasMoreElements()) {
                    val inetAddress = enumIpAddress.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }
}