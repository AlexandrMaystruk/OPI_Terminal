package com.gmail.maystruks08.opiterminal.terminal


import android.util.Log
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

class ServerSocketConnection {

    private val tag = "ServerSocketConnection"
    private var operationTime = 0L

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null

    private val connectionQueue: Queue<ConnectionInformation?> =
        ArrayBlockingQueue<ConnectionInformation?>(10)

    fun receiveData(receivePort: Int?, onDataReceived: (String) -> Unit) {
        val beginTicks: Long = System.currentTimeMillis()
        if (receivePort == null || receivePort == 0) {
            operationTime = System.currentTimeMillis() - beginTicks
            Log.d(tag, "Error receivePort is null or 0.")
            Log.d(tag, "Method receiveData() finished. Operation time $operationTime ms")
            return
        }
        if (closeConnection()) {
            try {
                serverSocket = ServerSocket(receivePort)
                Log.d(tag, "Waiting for incoming connection on ${serverSocket?.inetAddress}")
                while (true) {

                    clientSocket = serverSocket?.accept()

                    if (clientSocket != null) {
                        DataInputStream(clientSocket!!.getInputStream()).use {
                            while (clientSocket!!.isConnected) {
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
    }

    @Throws(IOException::class)
    fun write(message: ByteArray) {
        val connectionInformation = connectionQueue.remove()
        if (connectionInformation?.channel1InputStream != null) {
            connectionInformation.channel1OutputStream.write(message)
            connectionInformation.channel1OutputStream.flush()
        } else {
            Log.d(
                tag,
                "Failed to send message to terminal using channel 1: no OutputStream available"
            )
        }
    }

    fun closeConnection(): Boolean {
        Log.d(tag, "Close connection started")
        val beginTicks: Long = System.currentTimeMillis()
        // Check that socket not exists

        return try {
            clientSocket?.close()
            serverSocket?.close()
            serverSocket = null
            clientSocket = null

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

    internal data class ConnectionInformation(
        val channel1InputStream: InputStream,
        val channel1OutputStream: OutputStream
    )
}