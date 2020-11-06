package com.gmail.maystruks08.opi_core.connector

import com.gmail.maystruks08.opi_core.asyncWithCatchException
import com.gmail.maystruks08.opi_core.entity.OperationResult
import com.gmail.maystruks08.opi_core.entity.request.DeviceRequest
import com.gmail.maystruks08.opi_core.entity.response.DeviceResponse
import com.gmail.maystruks08.opi_core.getLocalIpAddress
import java.net.InetAddress
import java.net.ServerSocket

class DeviceChanelHandler(
    private val port: Int,
    private val timeout: Int,
    private val logger: OPILogger
) {

    private var serverSocket: ServerSocket? = null
    private var posChanelHandler: PosChanelHandler? = null
    private var isRunning = false

    fun runServer(onReceiveMessageFromDevice: (request: DeviceRequest) -> Unit) {
        val server = createSocket() ?: return
        isRunning = true

        asyncWithCatchException {
            Thread.sleep(timeout.toLong())
            shutdown("Device chanel shutdown by timeout timer")
            Thread.currentThread().interrupt()
        }

        val beginTicks: Long = System.currentTimeMillis()
        var operationTime = 0L

        while (isRunning && operationTime < timeout && !server.isClosed) {
            operationTime = System.currentTimeMillis() - beginTicks
            posChanelHandler = PosChanelHandler(server.accept(), logger).apply {
                val deviceRequestXml = read().orEmpty()
                val deviceRequest = DeviceRequest(deviceRequestXml)
                onReceiveMessageFromDevice(deviceRequest)

                val callback = deviceMessageResolver(deviceRequest)
                write(callback.serializeToXMLString())
                shutdown()
            }
            posChanelHandler = null
        }
        shutdown("Device chanel shutdown")
    }

    fun shutdown(message: String) {
        if (isRunning) {
            posChanelHandler?.shutdown()
            try {
                if (serverSocket?.isClosed == false) {
                    serverSocket?.close()
                    serverSocket = null
                    logger.log(message)
                }
            } catch (e: Exception) {
                logger.logError(e, "Device chanel shutdown error")
            }
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

    private fun createSocket(): ServerSocket? {
        return try {
            shutdown("Device chanel shutdown")
            serverSocket = ServerSocket(port, 0, InetAddress.getByName(getLocalIpAddress()))
            serverSocket
        } catch (e: Exception) {
            logger.logError(e, "Create server socket error")
            null
        }
    }
}