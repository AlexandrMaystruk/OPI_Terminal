package com.gmail.maystruks08.opi_core.connector

import com.gmail.maystruks08.opi_core.asyncWithCatchException
import com.gmail.maystruks08.opi_core.entity.OperationResult
import com.gmail.maystruks08.opi_core.entity.request.DeviceRequest
import com.gmail.maystruks08.opi_core.entity.response.DeviceResponse
import com.gmail.maystruks08.opi_core.getLocalIpAddress
import com.gmail.maystruks08.opi_core.runWithCatchException
import java.net.InetAddress
import java.net.ServerSocket

class DeviceChanelHandler(private val port: Int, private val timeout: Int, private val logger: OPILogger) {

    private var serverSocket: ServerSocket? = null
    private lateinit var posChanelHandler: PosChanelHandler
    private var timeoutThread: Thread? = null

    fun runServer(onReceiveMessageFromDevice: (request: DeviceRequest) -> Unit) {
        val server = createSocket()
        if (server == null) {
            logger.log("$TAG createSocket() return null")
            return
        }
        timeoutThread = asyncWithCatchException {
            Thread.sleep(timeout.toLong())
            shutdown("$TAG shutdown by timeout timer")
            Thread.currentThread().interrupt()
        }
        while (!server.isClosed) {
            posChanelHandler = PosChanelHandler(server.accept(), logger)
            val deviceRequestXml = posChanelHandler.read().orEmpty()
            val deviceRequest = DeviceRequest(deviceRequestXml)
            onReceiveMessageFromDevice(deviceRequest)
            posChanelHandler.write(deviceMessageResolver(deviceRequest).toXMLString())
            posChanelHandler.shutdown()
        }
    }

    private fun createSocket(): ServerSocket? {
        return try {
            shutdown("$TAG Close old socket and create new")
            ServerSocket(port, 0, InetAddress.getByName(getLocalIpAddress())).also { serverSocket = it }
        } catch (e: Exception) {
            logger.logError(e, "$TAG Create server socket error")
            null
        }
    }

    fun shutdown(message: String) {
        runWithCatchException { posChanelHandler.shutdown() }
        try {
            serverSocket?.close()
            serverSocket = null
            logger.log(TAG + message)
            timeoutThread?.interrupt()
        } catch (e: Exception) {
            logger.logError(e, "$TAG shutdown error")
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

    companion object{
        private const val TAG = "[OPI] DEVICE CHANEL:"
    }
}