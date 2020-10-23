package com.gmail.maystruks08.opiterminal

import android.os.Handler
import android.os.Message
import com.hssoft.smartcheckout.opi_core.terminal.entity.Payment
import com.hssoft.smartcheckout.opi_core.terminal.entity.RequestType
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.CardRequest
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.ServiceRequest
import com.hssoft.smartcheckout.opi_core.terminal.entity.response.CardResponse
import com.hssoft.smartcheckout.opi_core.terminal.entity.response.ServiceResponse
import com.hssoft.smartcheckout.opi_core.terminal.connector.DeviceChanelHandler
import com.hssoft.smartcheckout.opi_core.terminal.connector.PosChanelHandler
import com.hssoft.smartcheckout.opi_core.terminal.entity.BaseXMLEntity
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.DeviceRequest
import com.hssoft.smartcheckout.opi_core.terminal.entity.response.DeviceResponse
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

const val SERVER_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val TEXT_OUTPUT_TIMEOUT = "120"

fun Date.toServerUTCFormat(): String =
    SimpleDateFormat(SERVER_UTC_FORMAT, Locale.getDefault()).format(this)

class Terminal(
    private val ipAddress: String,
    private val inputPort: Int,
    private val outputPort: Int,
    private val timeout: Int = 4000,
    private val applicationSender: String?,
    private val workstationID: String?
) {
    private val tag = "OPI_Terminal"
    private var logger = Logger.getLogger(tag)
    lateinit var handler: Handler

    class Builder {

        private var ipAddress: String = "0.0.0.0"
        private var inputPort: Int = 0
        private var outputPort: Int = 0
        private var timeout: Int = 4000
        private var applicationSender: String = ""
        private var workstationID: String = ""

        fun ipAddress(ip: String) = apply { this.ipAddress = ip }
        fun inputPort(port: Int) = apply { this.inputPort = port }
        fun outputPort(port: Int) = apply { this.outputPort = port }
        fun timeout(timeout: Int) = apply { this.timeout = timeout }
        fun applicationSender(applicationSender: String) =
            apply { this.applicationSender = applicationSender }

        fun workstationID(id: String) = apply { this.workstationID = id }

        fun build(): Terminal {
            return Terminal(
                ipAddress,
                inputPort,
                outputPort,
                timeout,
                applicationSender,
                workstationID
            )
        }
    }

    fun login(): ServiceResponse {
        val serviceRequest = ServiceRequest(
            requestType = RequestType.Login.name,
            workstationID = workstationID,
            requestID = "0",
            applicationSender = applicationSender,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        return callWithShutdown(serviceRequest)
    }

    fun status(): ServiceResponse {
        val serviceRequest = ServiceRequest(
            requestType = RequestType.Diagnosis.name,
            applicationSender = applicationSender,
            workstationID = workstationID,
            requestID = "0",
            elmeTunnelCallback = true,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        return callWithShutdown(serviceRequest)
    }

    fun transaction(paymentData: Payment): CardResponse {
        val cardServiceRequest = CardRequest(
            requestID = 10,
            workstationID = workstationID,
            requestType = RequestType.CardPayment.name,
            posData = CardRequest.PosData(
                posTimeStamp = Date().toServerUTCFormat(),
                usePreselectedCard = false
            ),
            privateData = CardRequest.PrivateData(
                lastReceiptNumber = paymentData.lastReceiptNumber.toString()
            ),
            totalAmount = CardRequest.TotalAmount(
                currency = paymentData.currency,
                paymentAmount = paymentData.total.toString()
            )
        )

        return callWithShutdown(cardServiceRequest) {
            val deviceChanelHandler = DeviceChanelHandler(outputPort, 30000)
            deviceChanelHandler.runServer(
                { request ->
                    logTransaction("server request: $request")
                },
                { response ->
                    logTransaction("server response: $response")
                }, {
                    logTransaction("server shutdown")
                })
        }
    }

    fun cancelTransaction(paymentData: Payment): CardRequest {
        val cardServiceRequest = CardRequest(
            requestID = paymentData.transactionId,
            workstationID = workstationID,
            requestType = RequestType.PaymentReversal.name,
            privateData = CardRequest.PrivateData(
                lastReceiptNumber = paymentData.lastReceiptNumber.toString()
            ),
            posData = CardRequest.PosData(
                posTimeStamp = Date().toServerUTCFormat(),
                usePreselectedCard = false
            ),
            totalAmount = CardRequest.TotalAmount(
                currency = paymentData.currency,
                paymentAmount = paymentData.total.toString()
            )
        )
        return callWithShutdown(cardServiceRequest) {
            val deviceChanelHandler = DeviceChanelHandler(outputPort, 40000)
            deviceChanelHandler.runServer(
                { request ->
                    logTransaction("server request: $request")
                },
                { response ->
                    logTransaction("server response: $response")
                }, {
                    logTransaction("server shutdown")
                    deviceChanelHandler.shutdown()
                })
        }
    }

    fun cancelOperation(requestID: String): ServiceResponse {
        val serviceRequest = ServiceRequest(
            requestType = RequestType.AbortRequest.name,
            workstationID = workstationID,
            requestID = requestID,
            applicationSender = applicationSender,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        return callWithShutdown(serviceRequest)
    }

    fun logout(): ServiceResponse {
        val serviceRequest = ServiceRequest(
            requestType = RequestType.Logoff.name,
            workstationID = workstationID,
            requestID = "8",
            applicationSender = applicationSender,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        return callWithShutdown(serviceRequest)
    }

    private inline fun <reified Response : BaseXMLEntity> callWithShutdown(xmlEntity: BaseXMLEntity): Response {

        var posChanelHandler: PosChanelHandler? = null
        try {
            posChanelHandler = PosChanelHandler(ipAddress, inputPort, timeout)
            val request = xmlEntity.serializeToXMLString()
            posChanelHandler.write(request)
            logTransaction("Data send -> $request")

            val response = posChanelHandler.read().orEmpty()
            posChanelHandler.shutdown()
            logTransaction("Data receive -> $response")
            return when (xmlEntity) {
                is CardRequest -> CardResponse(response) as Response
                is DeviceRequest -> DeviceResponse(response) as Response
                is ServiceRequest -> ServiceResponse(response) as Response
                else -> throw IllegalStateException("Not request supported type")
            }
        } finally {
            posChanelHandler?.shutdown()
        }
    }


    private inline fun <reified Response : BaseXMLEntity> callWithShutdown(xmlEntity: BaseXMLEntity, block: () -> Unit): Response {
        var posChanelHandler: PosChanelHandler? = null
        try {
            posChanelHandler = PosChanelHandler(ipAddress, inputPort, timeout)
            val request = xmlEntity.serializeToXMLString()
            posChanelHandler.write(request)
            logTransaction("Data send -> $request")

            block()

            val response = posChanelHandler.read().orEmpty()
            posChanelHandler.shutdown()
            logTransaction("Data receive -> $response")
            return when (xmlEntity) {
                is CardRequest -> CardResponse(response) as Response
                is DeviceRequest -> DeviceResponse(response) as Response
                is ServiceRequest -> ServiceResponse(response) as Response
                else -> throw IllegalStateException("Not supported request type")
            }
        } finally {
            posChanelHandler?.shutdown()
        }
    }

    private fun logTransaction(message: String) {
        logger.log(Level.INFO, message)
        handler.sendMessage(
            Message().apply {
                what = 0
                data.putString("0", message)
            })
    }
}