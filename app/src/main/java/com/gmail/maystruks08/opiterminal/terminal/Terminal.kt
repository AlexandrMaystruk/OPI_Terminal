package com.gmail.maystruks08.opiterminal.terminal

import android.util.Log
import com.gmail.maystruks08.opiterminal.entity.request.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.*
import java.util.*


class Terminal(
    private val ipAddress: String?,
    private val inputPort: Int?,
    private val outputPort: Int?,
    private val timeout: Int?
) {

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
            return Terminal(
                ipAddress,
                inputPort,
                outputPort,
                timeout
            )
        }
    }

    fun login() {
        socketConnectionHelper.openSendConnection(ipAddress, inputPort)?.let {

            val serviceRequest = ServiceRequest(requestType = "Login", applicationSender = "SmartCheckout", workstationID = getLocalIpAddress(), requestID = UUID.randomUUID().toString())

            serviceRequest.serializeToXMLString()?.let {
                socketConnectionHelper.sendData(it)
            }

            socketConnectionHelper.receiveData(outputPort) {
                Log.d(tag, "Receive login data $it")
            }
        }
    }

    fun transaction(paymentData: Payment) {
        socketConnectionHelper.receiveData(outputPort) {
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
        socketConnectionHelper.receiveData(outputPort) {
            Log.d(tag, "Receive logout data $it")
        }
        socketConnectionHelper.sendData("xml")
        socketConnectionHelper.closeConnection()
    }
}

class SocketConnectionHelper {

    private val tag = "SocketConnectionHelper"
    private var sendDataSocket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var operationTime = 0L
    private var bufferOut: DataOutputStream? = null
    private var bufferIn: DataInputStream? = null

    fun openSendConnection(ipAddress: String?, port: Int?): Socket? {
        val beginTicks: Long = System.currentTimeMillis()
        Log.d(tag, "Open connection started")

        // Check IP and Port not empty
        if (ipAddress.isNullOrEmpty() || port == null || port == 0) {
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

        return try {
            val address: InetAddress = InetAddress.getByName(ipAddress)
            Log.d(tag, "Connecting..")
            //create a socket to make the connection with the server
            sendDataSocket = Socket(address, port)
            if (sendDataSocket?.isConnected == true) {
                Log.d(tag, "Socket connected $ipAddress : $port")
                operationTime = System.currentTimeMillis() - beginTicks
                Log.d(tag, "Method openConnection() finished. Operation time ${Date(operationTime)}")
                sendDataSocket
            } else {
                Log.d(tag, "Connect socket error $ipAddress : $port")
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

    fun sendData(data: String) {
        try {
            bufferOut = DataOutputStream(sendDataSocket!!.getOutputStream())
            bufferOut?.writeUTF(data)
            bufferOut?.flush()
            Log.d(tag, "Send data $data")
        } catch (e: IOException) {
            bufferIn?.close()
            Log.d(tag, "Send data error: " + e.message)
        }
    }

    fun receiveData(receivePort: Int?, onDataReceived: (String) -> Unit) {
        try {
            val beginTicks: Long = System.currentTimeMillis()
            if(receivePort == null || receivePort == 0){
                operationTime = System.currentTimeMillis() - beginTicks
                Log.d(tag, "Error receivePort is null or 0.")
                Log.d(tag, "Method receiveData() finished. Operation time ${Date(operationTime)}")
                return
            }
            val deviceIp = getLocalIpAddress()
            serverSocket = ServerSocket(receivePort, 0, InetAddress.getByName(deviceIp))
            Log.d(tag, " Waiting for incoming connection on IP $deviceIp and port $receivePort")
            while (true) {
                serverSocket?.accept()?.use { socket ->
                    clientSocket = socket
                    DataInputStream(socket.getInputStream()).use {
                        while (socket.isConnected) {
                            val serverMessage = it.readUTF()
                            onDataReceived(serverMessage)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
            clientSocket?.close()
            serverSocket?.close()
            bufferOut?.flush()
            bufferOut?.close()
            bufferIn?.close()
            sendDataSocket = null
            serverSocket = null
            clientSocket = null
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

fun getLocalIpAddress(): String {
    return try {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val enumIpAddress = networkInterface.inetAddresses
            while (enumIpAddress.hasMoreElements()) {
                val internetAddress = enumIpAddress.nextElement()
                if (!internetAddress.isLoopbackAddress && internetAddress is Inet4Address) {
                    return internetAddress.getHostAddress()
                }
            }
        }
        ""
    } catch (ex: SocketException) {
        ""
    }

}