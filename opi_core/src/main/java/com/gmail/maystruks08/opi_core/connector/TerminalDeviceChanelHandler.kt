package com.gmail.maystruks08.opi_core.connector

import com.gmail.maystruks08.opi_core.entity.OperationResult
import com.gmail.maystruks08.opi_core.entity.request.DeviceRequest
import com.gmail.maystruks08.opi_core.entity.response.DeviceResponse
import com.gmail.maystruks08.opi_core.getLocalIpAddress
import com.gmail.maystruks08.opi_core.runWithCatchException
import java.net.BindException
import java.net.InetSocketAddress
import java.net.ServerSocket

class TerminalDeviceChanelHandler(private val port: Int, private val timeout: Int, private val logger: OPILogger) {

    private var serverSocket: ServerSocket? = null
    private var clientHandler: ClientHandler? = null

    fun startServer(onReceiveMessageFromDevice: (request: DeviceRequest) -> Unit) {
        val server = createServerSocket() ?: return
        while (!server.isClosed) {
            val clientSocket = server.accept()
            clientSocket.soTimeout = timeout
            runWithCatchException {
                clientHandler = ClientHandler(clientSocket, logger)
                val deviceRequestXml = clientHandler?.read().orEmpty()
                val deviceRequest = DeviceRequest(deviceRequestXml)
                onReceiveMessageFromDevice(deviceRequest)
                clientHandler?.write(deviceMessageResolver(deviceRequest).toXMLString())
                clientHandler?.shutdown()
            }
        }
        shutdown("Server socket shutdown")
    }

    fun shutdown(message: String) {
        try {
            clientHandler?.shutdown()
            if (serverSocket?.isClosed == false) serverSocket?.close()
            serverSocket = null
            logger.log(TAG + message)
        } catch (e: Exception) {
            logger.logError(e, "$TAG shutdown error")
        }
    }

    @Synchronized
    private fun createServerSocket(): ServerSocket? {
        return try {
            shutdown("Shutdown old server")
            ServerSocket().apply {
                reuseAddress = true
                bind(InetSocketAddress(getLocalIpAddress(), port), port)
                serverSocket = this
                runWithCatchException { logger.log("$TAG Server socket created. Inet address: $inetAddress") }
            }
        } catch (e: BindException) {
            logger.logError(e, "$TAG Bind server socket error: ${e.localizedMessage}")
            null
        } catch (e: Exception) {
            logger.logError(e, "$TAG Create server socket error")
            null
        }
    }

    //return confirmation message
    private fun deviceMessageResolver(deviceRequest: DeviceRequest): DeviceResponse {
        return DeviceResponse(
                requestID = deviceRequest.requestID,
                workstationID = deviceRequest.workstationID,
                requestType = deviceRequest.requestType,
                result = OperationResult.Success,
                output = DeviceResponse.Output(
                        outDeviceTarget = deviceRequest.output?.outDeviceTarget.orEmpty(),
                        outResult = OperationResult.Success.name
                )
        )
    }

    companion object {
        private const val TAG = "TERMINAL DEVICE:"
    }
}