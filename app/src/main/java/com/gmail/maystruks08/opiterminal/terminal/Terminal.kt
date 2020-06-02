package com.gmail.maystruks08.opiterminal.terminal

import android.util.Log
import com.gmail.maystruks08.opiterminal.entity.SimpleText
import com.gmail.maystruks08.opiterminal.entity.request.CardRequest
import com.gmail.maystruks08.opiterminal.entity.request.DeviceRequest
import com.gmail.maystruks08.opiterminal.entity.request.ServiceRequest
import java.text.SimpleDateFormat
import java.util.*

const val SERVER_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

enum class RequestType {
    Initialisation,
    Login,
    Output,
    CardPayment,
    PaymentReversal,
    Logoff
}

const val TEXT_OUTPUT_TIMEOUT = "120"

fun Date.toServerUTCFormat(): String {
    val sdf = SimpleDateFormat(SERVER_UTC_FORMAT, Locale.getDefault())
    return sdf.format(this)
}

class Terminal(
    private val ipAddress: String?,
    private val inputPort: Int?,
    private val outputPort: Int?,
    private val timeout: Int?,
    private val applicationSender: String?,
    private val workstationID: String?
) {

    private val tag = "OPITerminal"
    private val clientChanel = ClientSocketConnection()
    private val serverChanel = ServerSocketConnection()


    class Builder {

        private var ipAddress: String? = null
        private var inputPort: Int? = null
        private var outputPort: Int? = null
        private var timeout: Int? = null
        private var applicationSender: String? = null
        private var workstationID: String? = null

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

    fun initialization() {

        Thread(Runnable {
            clientChanel.openSendConnection(ipAddress, inputPort)

            val serviceRequest = ServiceRequest(
                requestType = RequestType.Initialisation.name,
                workstationID = workstationID,
                requestID = "0",
                elmeTunnelCallback = true,
                applicationSender = applicationSender,
                posData = ServiceRequest.PosData(posTimeStamp = Date())
            )

            clientChanel.sendData(serviceRequest.serializeToXMLString())
            clientChanel.read()

            val deviceRequest = DeviceRequest(
                requestID = "1",
                requestType = RequestType.Output.name,
                applicationSender = applicationSender,
                output = DeviceRequest.Output(
                    outDeviceTarget = "CashierDisplay",
                    textLine = DeviceRequest.TextLine(
                        timeOut = TEXT_OUTPUT_TIMEOUT,
                        message = SimpleText("Terminal is alive!!!!!!!! ..")
                    )
                )
            )

            clientChanel.sendData(deviceRequest.serializeToXMLString())
            clientChanel.read()

        }).start()
    }

    fun status() {
        val serviceRequest = ServiceRequest(
            requestType = RequestType.Login.name,
            applicationSender = applicationSender,
            workstationID = workstationID,
            requestID = "0",
            elmeTunnelCallback = true,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        clientChanel.sendData(serviceRequest.serializeToXMLString())
        clientChanel.read()
    }

    fun transaction(paymentData: Payment) {
        clientChanel.openSendConnection(ipAddress, inputPort)
        val cardServiceRequest = CardRequest(
            elmeTunnelCallback = true,
            requestID = paymentData.transactionId,
            workstationID = workstationID,
            requestType = RequestType.CardPayment.name,
            posData = CardRequest.PosData(
                posTimeStamp = Date().toServerUTCFormat(),
                usePreselectedCard = false
            ),
            privateData = CardRequest.PrivateData(lastReceiptNumber = paymentData.lastReceiptNumber.toString()),
            totalAmount = CardRequest.TotalAmount(
                currency = paymentData.currency,
                paymentAmount = SimpleText(paymentData.total.toString())
            )
        )
        clientChanel.sendData(cardServiceRequest.serializeToXMLString())
        clientChanel.read()

        val deviceRequest0 = DeviceRequest(
            requestID = "0",
            requestType = RequestType.Output.name,
            workstationID = workstationID,
            applicationSender = applicationSender,
            output = DeviceRequest.Output(
                outDeviceTarget = "CashierDisplay",
                textLine = DeviceRequest.TextLine(
                    timeOut = TEXT_OUTPUT_TIMEOUT,
                    message = SimpleText("Scan you card please")
                )
            )
        ).serializeToXMLString()
        clientChanel.sendData(deviceRequest0)
        clientChanel.read()
    }

    fun cancelTransaction(paymentData: Payment) {

        val cardServiceRequest = CardRequest(
            elmeTunnelCallback = true,
            requestID = paymentData.transactionId,
            workstationID = workstationID,
            requestType = RequestType.PaymentReversal.name,
            privateData = CardRequest.PrivateData(lastReceiptNumber = paymentData.lastReceiptNumber.toString()),
            posData = CardRequest.PosData(
                posTimeStamp = Date().toServerUTCFormat(),
                usePreselectedCard = false
            ),
            totalAmount = CardRequest.TotalAmount(
                currency = paymentData.currency,
                paymentAmount = SimpleText(paymentData.total.toString())
            )
        )
        clientChanel.sendData(cardServiceRequest.serializeToXMLString())
        clientChanel.read()

        val deviceRequest = DeviceRequest(
            requestID = "0",
            requestType = RequestType.Output.name,
            workstationID = workstationID,
            applicationSender = applicationSender,
            output = DeviceRequest.Output(
                outDeviceTarget = "CashierDisplay",
                textLine = DeviceRequest.TextLine(
                    timeOut = TEXT_OUTPUT_TIMEOUT,
                    message = SimpleText("Card please")
                )
            )
        )

        clientChanel.sendData(deviceRequest.serializeToXMLString())
        Log.d(tag, "Read data from deviceRequest0" + clientChanel.read())
    }

    fun logout() {
        val deviceRequest = DeviceRequest(
            requestID = "0",
            requestType = RequestType.Logoff.name,
            workstationID = workstationID,
            applicationSender = applicationSender,
            output = DeviceRequest.Output(
                outDeviceTarget = "CashierDisplay",
                textLine = DeviceRequest.TextLine(
                    timeOut = TEXT_OUTPUT_TIMEOUT,
                    message = SimpleText("Log out")
                )
            )
        )
        clientChanel.sendData(deviceRequest.serializeToXMLString())
        clientChanel.read()

        val serviceRequest = ServiceRequest(
            requestType = RequestType.Logoff.name,
            workstationID = workstationID,
            requestID = "0",
            elmeTunnelCallback = true,
            applicationSender = applicationSender,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        clientChanel.sendData(serviceRequest.serializeToXMLString())
        clientChanel.read()
        clientChanel.disconnect()
    }
}