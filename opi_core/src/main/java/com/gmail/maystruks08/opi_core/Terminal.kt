package com.gmail.maystruks08.opi_core

import com.gmail.maystruks08.opi_core.connector.ClientHandler
import com.gmail.maystruks08.opi_core.connector.OPILogger
import com.gmail.maystruks08.opi_core.connector.TerminalDeviceChanelHandler
import com.gmail.maystruks08.opi_core.entity.*
import com.gmail.maystruks08.opi_core.entity.request.CardRequest
import com.gmail.maystruks08.opi_core.entity.request.DeviceRequest
import com.gmail.maystruks08.opi_core.entity.request.ServiceRequest
import com.gmail.maystruks08.opi_core.entity.response.CardResponse
import com.gmail.maystruks08.opi_core.entity.response.DeviceResponse
import com.gmail.maystruks08.opi_core.entity.response.ServiceResponse
import java.util.*

class Terminal(
    private val ipAddress: String, private val inputPort: Int,
    private val outputPort: Int, private val connectTimeout: Int,
    private val valueReadWriteTimeout: Int, private val applicationSender: String?,
    private val workstationID: String?, private val opiLogger: OPILogger
) {
    class Builder {

        private var ipAddress: String = "0.0.0.0"
        private var inputPort: Int = 0
        private var outputPort: Int = 0
        private var connectTimeout: Int = 3000
        private var valueReadWriteTimeout: Int = 120000
        private var applicationSender: String = "Default"
        private var workstationID: String = "Default workstationID"
        private lateinit var opiLogger: OPILogger

        fun ipAddress(ip: String) = apply { this.ipAddress = ip }
        fun inputPort(port: Int) = apply { this.inputPort = port }
        fun outputPort(port: Int) = apply { this.outputPort = port }
        fun connectTimeout(timeout: Int) = apply { this.connectTimeout = timeout }
        fun readWriteTimeout(timeout: Int) = apply { this.valueReadWriteTimeout = timeout }
        fun applicationSender(applicationSender: String) =
            apply { this.applicationSender = applicationSender }

        fun workstationID(id: String) = apply { this.workstationID = id }
        fun logger(opiLogger: OPILogger) = apply { this.opiLogger = opiLogger }
        fun build(): Terminal {
            return Terminal(
                ipAddress = ipAddress,
                inputPort = inputPort,
                outputPort = outputPort,
                connectTimeout = connectTimeout,
                valueReadWriteTimeout = valueReadWriteTimeout,
                applicationSender = applicationSender,
                workstationID = workstationID,
                opiLogger = opiLogger
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
        return callWithShutdown(serviceRequest, 5000)
    }

    fun status(): ServiceResponse {
        val serviceRequest = ServiceRequest(
            requestType = RequestType.StatusQuery.name,
            applicationSender = applicationSender,
            workstationID = workstationID,
            requestID = "0",
            elmeTunnelCallback = false,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        return callWithShutdown(serviceRequest, 5000)
    }

    fun initialisation(): ServiceResponse {
        val serviceRequest = ServiceRequest(
            requestType = RequestType.Administration.name,
            applicationSender = applicationSender,
            workstationID = workstationID,
            requestID = "0",
            elmeTunnelCallback = true,
            privateData = ServiceRequest.PrivateData(value = "type=initialise"),
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        return callWithShutdown(serviceRequest, 60 * 10000)
    }

    fun diagnostic(): ServiceResponse {
        val serviceRequest = ServiceRequest(
            requestType = RequestType.Diagnosis.name,
            applicationSender = applicationSender,
            workstationID = workstationID,
            requestID = "0",
            elmeTunnelCallback = true,
            privateData = ServiceRequest.PrivateData(value = "subtype=config"),
            posData = ServiceRequest.PosData(
                posTimeStamp = Date(),
                diagnosisMethod = ServiceRequest.DiagnosisMethod.OnLine
            )
        )
        return callWithShutdown(serviceRequest, 60 * 10000)
    }

    fun transaction(paymentData: Payment, status: (String) -> Unit): CardResponse {
        val cardServiceRequest = CardRequest(
            requestID = paymentData.transactionId,
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
        return callTransactionWithShutdown(cardServiceRequest) { status.invoke(it) }
    }

    fun repeatLastMessage(requestId: Long, status: (String) -> Unit): CardResponse {
        val cardServiceRequest = CardRequest(
            requestID = requestId,
            workstationID = workstationID,
            requestType = RequestType.RepeatLastMessage.name,
        )
        return callTransactionWithShutdown(cardServiceRequest) { status.invoke(it) }
    }

    fun cancelPaymentTransaction(requestId: Long, originalTransaction: OriginalTransactionData, status: (String) -> Unit): CardResponse {
        val cardServiceRequest = CardRequest(
            requestID = requestId,
            workstationID = workstationID,
            requestType = RequestType.PaymentReversal.name,
            totalAmount = CardRequest.TotalAmount(
                paymentAmount = originalTransaction.total.toString(),
                currency = originalTransaction.currency
            ),
            originalTransaction = originalTransaction.let {
                CardRequest.OriginalTransaction(
                    terminalID = it.terminalID,
                    STAN = it.stan,
                    timeStamp = it.timeStamp
                )
            },
        )
        return callTransactionWithShutdown(cardServiceRequest) { status.invoke(it) }
    }

    fun reconciliationWithClosure(requestID: String, status: (String) -> Unit): ServiceResponse {
        val serviceRequest = ServiceRequest(
            requestType = RequestType.ReconciliationWithClosure.name,
            workstationID = workstationID,
            requestID = requestID,
            applicationSender = applicationSender,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        return callTransactionWithShutdown(serviceRequest) { status.invoke(it) }
    }

    fun abortRequest(requestID: String): ServiceResponse {
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
            requestID = "0",
            applicationSender = applicationSender,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        return callWithShutdown(serviceRequest, 5000)
    }

    private inline fun <reified Response : BaseXMLEntity> callWithShutdown(xmlEntity: BaseXMLEntity, customTimeout: Int? = null): Response {
        val posChanelHandler = ClientHandler(
            ipAddress,
            inputPort,
            connectTimeout,
            customTimeout ?: valueReadWriteTimeout,
            opiLogger
        )
        val request = xmlEntity.toXMLString()
        posChanelHandler.write(request)

        val response = posChanelHandler.read()
        posChanelHandler.shutdown()

        if (response.isNullOrEmpty()) {
            return when (xmlEntity) {
                is CardRequest -> CardResponse(OperationResult.Failure) as Response
                is DeviceRequest -> DeviceResponse(OperationResult.Failure) as Response
                is ServiceRequest -> ServiceResponse(OperationResult.Failure) as Response
                else -> throw IllegalStateException("OPI: Not supported request type")
            }
        }
        return when (xmlEntity) {
            is CardRequest -> CardResponse(response) as Response
            is DeviceRequest -> DeviceResponse(response) as Response
            is ServiceRequest -> ServiceResponse(response) as Response
            else -> throw IllegalStateException("OPI: Not request supported type")
        }
    }

    private inline fun <reified Response : BaseXMLEntity> callTransactionWithShutdown(xmlEntity: BaseXMLEntity, crossinline status: (String) -> Unit): Response {
        val posChanelHandler =
            ClientHandler(ipAddress, inputPort, connectTimeout, valueReadWriteTimeout, opiLogger)
        val request = xmlEntity.toXMLString()
        posChanelHandler.write(request)

        val deviceChanelHandler =
            TerminalDeviceChanelHandler(outputPort, valueReadWriteTimeout, opiLogger)
        var merchantInformation: DeviceRequest? = null
        var userInformation: DeviceRequest? = null
        val asyncDeviceCommunication = asyncWithCatchException {
            deviceChanelHandler.startServer { request ->
                when {
                    request.isPrinterRequest() -> merchantInformation = request
                    request.isPrinterReceiptRequest() -> userInformation = request
                    else -> {
                        val stringBuilder = StringBuilder().apply {
                            request.output?.textLines?.forEach {
                                appendLine(it.text.orEmpty())
                            }
                        }
                        status.invoke(stringBuilder.toString())
                    }
                }
            }
        }
        val response = posChanelHandler.read()

        deviceChanelHandler.shutdown("shutdown after receive overall result")
        asyncDeviceCommunication.interrupt()
        posChanelHandler.shutdown()

        if (response.isNullOrEmpty()) {
            return when (xmlEntity) {
                is CardRequest -> CardResponse(OperationResult.Failure) as Response
                is DeviceRequest -> DeviceResponse(OperationResult.Failure) as Response
                is ServiceRequest -> ServiceResponse(OperationResult.Failure) as Response
                else -> throw IllegalStateException("OPI: Not supported request type")
            }
        }

        return when (xmlEntity) {
            is CardRequest -> CardResponse(response).apply {
                merchantReceipt = merchantInformation?.getReceiptString()
                customerReceipt = userInformation?.getReceiptString()
            } as Response
            is DeviceRequest -> DeviceResponse(response) as Response
            is ServiceRequest -> ServiceResponse(response).apply {
                merchantReceipt = merchantInformation?.getReceiptString()
                customerReceipt = userInformation?.getReceiptString()
                totalAmount = userInformation?.getTotalAmount()
            } as Response
            else -> throw IllegalStateException("OPI: Not supported request type")
        }
    }
}