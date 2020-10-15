package com.gmail.maystruks08.opiterminal

import android.os.Handler
import android.os.Message
import com.hssoft.smartcheckout.opi_core.terminal.connector.Chanel1SocketConnector
import com.hssoft.smartcheckout.opi_core.terminal.connector.Chanel0SocketConnector
import com.hssoft.smartcheckout.opi_core.terminal.entity.Payment
import com.hssoft.smartcheckout.opi_core.terminal.entity.RequestType
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.CardRequest
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.ServiceRequest
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

const val SERVER_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val TEXT_OUTPUT_TIMEOUT = "120"

fun Date.toServerUTCFormat(): String = SimpleDateFormat(SERVER_UTC_FORMAT, Locale.getDefault()).format(this)

class Terminal(
    private val ipAddress: String?,
    private val inputPort: Int?,
    private val outputPort: Int?,
    private val timeout: Int = 4000,
    private val applicationSender: String?,
    private val workstationID: String?,
    private val clientChanel: Chanel0SocketConnector,
    private val serverChanel: Chanel1SocketConnector
) {
    private val tag = "Terminal"

    private var logger = Logger.getLogger(tag)

    lateinit var handler: Handler //TODO remove

    class Builder {

        private var ipAddress: String? = null
        private var inputPort: Int? = null
        private var outputPort: Int? = null
        private var timeout: Int = 4000
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
                workstationID,
                Chanel0SocketConnector(),
                Chanel1SocketConnector()
            )
        }
    }

    fun initialization() {
        Thread {
            clientChanel.openSendConnection(ipAddress, inputPort, timeout)
            val serviceRequest1 = ServiceRequest(
                requestType = RequestType.Login.name,
                workstationID = workstationID,
                requestID = "0",
                applicationSender = applicationSender,
                posData = ServiceRequest.PosData(
                    posTimeStamp = Date()
                )
            )
            clientChanel.sendData(serviceRequest1.serializeToXMLString())
            logger.log(Level.INFO, serviceRequest1.serializeToXMLString())

            val response0 = clientChanel.read()

            sendLogToUI("0", response0)

            clientChanel.disconnect()
        }.start()
    }

    fun status() {

        clientChanel.openSendConnection(ipAddress, inputPort, timeout)

        val serviceRequest = ServiceRequest(
            requestType = RequestType.Login.name,
            applicationSender = applicationSender,
            workstationID = workstationID,
            requestID = "0",
            elmeTunnelCallback = true,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        clientChanel.sendData(serviceRequest.serializeToXMLString())

        val response2 = clientChanel.read()

        sendLogToUI("2", response2)

        clientChanel.disconnect()
    }

    fun transaction(paymentData: Payment) {
        clientChanel.openSendConnection(ipAddress, inputPort, timeout)

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

        val cardRequest = cardServiceRequest.serializeToXMLString()

        clientChanel.sendData(cardRequest)

        val response3 = clientChanel.read()

        sendLogToUI("3", response3)

        clientChanel.disconnect()

        //THIS SHIT NOT WORKS, I DON'T KNOW WHY =(
        Thread {
            serverChanel.receiveData(outputPort) {
                sendLogToUI("10", "Message from device ->>>>>>>>>>>>>>>>>\n$it")
            }
        }.start()
    }

    fun cancelTransaction(paymentData: Payment) {
        clientChanel.openSendConnection(ipAddress, inputPort, timeout)

        val cardServiceRequest = CardRequest(
            elmeTunnelCallback = true,
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
        clientChanel.sendData(cardServiceRequest.serializeToXMLString())
        val response5 = clientChanel.read()
        sendLogToUI("5", response5)
        clientChanel.disconnect()
    }

    fun logout() {
        clientChanel.openSendConnection(ipAddress, inputPort, timeout)

        val serviceRequest = ServiceRequest(
            requestType = RequestType.Logoff.name,
            workstationID = workstationID,
            requestID = "0",
            elmeTunnelCallback = true,
            applicationSender = applicationSender,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        clientChanel.sendData(serviceRequest.serializeToXMLString())
        val response8 = clientChanel.read()

        sendLogToUI("8", response8)
        clientChanel.disconnect()
    }

    //just for log
    private fun sendLogToUI(code: String, message: String?) {
        handler.sendMessage(
            Message().apply {
                what = code.toInt()
                data.putString(code, message)
            })
    }
}