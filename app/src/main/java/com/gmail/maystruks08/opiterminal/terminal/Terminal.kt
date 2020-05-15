package com.gmail.maystruks08.opiterminal.terminal

import android.util.Log
import com.gmail.maystruks08.opiterminal.entity.request.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.charset.Charset
import java.util.*


class Terminal(private val ipAddress: String, private val inputPort: Int, private val outputPort: Int, private val timeout: Int?) {

    private val tag = "Terminal"
    private val socketConnectionHelper = SocketConnectionHelper()

    class Builder {

        private var ipAddress: String? = null
        private var inputPort: Int? = null
        private var outputPort: Int? = null
        private var timeout: Int? = null

        fun ipAddress(ip: String) = apply { this.ipAddress = ip }
        fun inputPort(port: Int) = apply { this.inputPort = port }
        fun outputPort(port: Int) = apply { this.outputPort = port }
        fun timeout(timeout: Int) = apply { this.timeout = timeout }

        fun build(): Terminal {
            if (ipAddress == null || inputPort == null) throw Exception() //TODO change exception type
            return Terminal(
                ipAddress!!,
                inputPort!!,
                outputPort!!,
                timeout
            )
        }
    }

    fun login() {

        val socket = socketConnectionHelper.openSendConnection(ipAddress, inputPort, timeout)

        if (socket != null) {
            socketConnectionHelper.sendData("<?xml version=\"1.0\" encoding=\"utf-8\"?> <ServiceRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" RequestType=\"Login\" ApplicationSender=\"CashAssist\" WorkstationID=\"WINDOWS-3N69TMJ\" RequestID=\"192\" />".toUTF8())

            socketConnectionHelper.receiveData(ipAddress, outputPort) {
                Log.d(tag, "Receive login data $it")
            }
        }
    }

    fun transaction(paymentData: Payment) {
        socketConnectionHelper.receiveData(ipAddress, outputPort) {
            Log.d(tag, "Receive transaction data $it")
        }

        val testEntity = ServiceRequest()
            .apply {
                this.workstationID = "SmartCheckout id124235"
                this.applicationSender = "SmartCheckout android"
                this.posData = PosData(Date(), "clerkId = 10", true, ClerkPermission.High, paymentData.transactionId)
                this.privateData = PrivateData(PrepaidCard("", false, paymentData.total.toString()), listOf("Text 1", "Text 2", "Cat"))
            }

        val xml = testEntity.serializeToXMLString()
        if (xml != null) {
            socketConnectionHelper.sendData(xml)
        }
    }

    fun logout() {
        socketConnectionHelper.receiveData(ipAddress, outputPort) {
            Log.d(tag, "Receive logout data $it")
        }
        socketConnectionHelper.sendData("xml")
        socketConnectionHelper.closeConnection()
    }
}


private fun String.toUTF8(): String{
    return this.toByteArray().toString(Charset.defaultCharset())
}


class SocketConnectionHelper {

    private val tag = "SocketConnectionHelper"
    private var sendDataSocket: Socket? = null
    private var receiveDataSocket: Socket? = null
    private var operationTime = 0L
    private var defaultTimeout = 10000

    // used to send messages
    private var bufferOut: DataOutputStream? = null

    // used to read messages from the server
    private var bufferIn: DataInputStream? = null

    fun openSendConnection(serverIpAddress: String, serverPort: Int, timeout: Int?): Socket? {
        val beginTicks: Long = System.currentTimeMillis()
        Log.d(tag, "Open connection started")

        // Check IP and Port not empty
        if (serverIpAddress.isEmpty() || serverPort == 0) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Error serverIpAddress or serverPort is empty.")
            Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
            return null
        }

        // Check that socket not exists
        if (sendDataSocket != null) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Error socket alreadyExists.")
            Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
            return null
        }

        // Create and connect socket
        return try {
            val serverAddress: InetAddress = InetAddress.getByName(serverIpAddress)
            Log.d(tag, "Connecting..")

            //create a socket to make the connection with the server
            sendDataSocket = Socket(serverAddress, serverPort).apply { soTimeout = timeout?: defaultTimeout }
            Log.d(tag, "Socket created")
            if (sendDataSocket?.isConnected == true) {
                Log.d(tag, "Socket connected $serverIpAddress $serverPort")
                operationTime = System.currentTimeMillis() - beginTicks
                Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
                sendDataSocket
            } else {
                Log.d(tag, "Connect socket error $serverIpAddress $serverPort")
                operationTime = System.currentTimeMillis() - beginTicks
                Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
                null
            }
        } catch (socketException: SocketException) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Socket exception while connecting $socketException")
            Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
            null
        } catch (e: Exception) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Unknown exception while connecting $e")
            Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
            null
        }
    }

   private fun openReceiveConnection(serverPort: Int) {
        val beginTicks: Long = System.currentTimeMillis()
        Log.d(tag, "Open connection started")

        // Check IP and Port not empty
        if ( serverPort == 0) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Error serverIpAddress or serverPort is empty.")
            Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
            return
        }

        // Check that socket not exists
        if (receiveDataSocket != null) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Error socket alreadyExists.")
            Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
            return
        }

        // Create and connect socket
         try {
            Log.d(tag, "Connecting..")

            val serverSocket = ServerSocket(serverPort)

            while (true){
                //create a socket to make the connection with the server
                receiveDataSocket = serverSocket.accept()

                Log.d(tag, "Socket created")
                if (receiveDataSocket?.isConnected == true) {
                    operationTime = System.currentTimeMillis() - beginTicks
                    Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
                } else {
                    Log.d(tag, "Connect socket error $serverPort")
                    operationTime = System.currentTimeMillis() - beginTicks
                    Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
                }
            }
        } catch (socketException: SocketException) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Socket exception while connecting $socketException")
            Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
        } catch (e: Exception) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Unknown exception while connecting $e")
            Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
        }
    }

    @Throws(Exception::class)
    fun sendData(data: String) {
        if (sendDataSocket == null || sendDataSocket?.isClosed == true) {
            throw Exception("Send data error. Socket not created or closed")
        }
        try {
            //sends the message to the server
            bufferOut = DataOutputStream(sendDataSocket!!.getOutputStream())
            bufferOut?.writeUTF(data)
            bufferOut?.flush()
        } catch (e: IOException) {
            throw Exception("Send data error: " + e.message)
        }
    }

    @Throws(Exception::class)
    fun receiveData(serverIpAddress: String, serverPort: Int, onDataReceived: (String) -> Unit) {

        openReceiveConnection(serverPort)

        if (receiveDataSocket == null || receiveDataSocket?.isClosed == true) {
            throw Exception("Receive data error. Socket not created or closed")
        }
        try {
            //receives the message which the server sends back
            bufferIn = DataInputStream(receiveDataSocket!!.getInputStream());
            //in this while the client listens for the messages sent by the server
            while (receiveDataSocket?.isConnected == true) {
                val serverMessage = bufferIn?.readUTF()
                if (serverMessage != null ) {
                    onDataReceived(serverMessage)
                }
            }
        } catch (e: IOException) {
            throw Exception("Receive data error: $e")
        }
    }

    fun closeConnection(): Boolean {
        Log.d(tag, "Close connection started")
        val beginTicks: Long = System.currentTimeMillis()
        // Check that socket not exists
        if (sendDataSocket == null) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Socket not exist")
            Log.d(tag, "Method closeConnection() finished. Operation time ${Date(operationTime)}")
            return false
        }
        return try {
            sendDataSocket?.close()
            bufferOut?.flush()
            bufferOut?.close()
            bufferIn?.close()
            sendDataSocket = null
            bufferOut = null
            bufferIn = null

            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Close socket successful")
            Log.d(tag, "Method closeConnection() finished. Operation time ${Date(operationTime)}")
            true
        } catch (socketException: SocketException) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Socket exception while disconnecting $socketException")
            Log.d(tag, "Method closeConnection() finished. Operation time ${Date(operationTime)}")
            false
        } catch (e: Exception) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Unknown exception while disconnecting $e")
            Log.d(tag, "Method closeConnection() finished. Operation time ${Date(operationTime)}")
            false
        }
    }
}