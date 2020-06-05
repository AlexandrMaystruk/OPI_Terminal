package com.gmail.maystruks08.opiterminal

import android.os.Handler
import android.os.Message
import com.hssoft.smartcheckout.opi_core.terminal.connector.Chanel1SocketConnector
import com.hssoft.smartcheckout.opi_core.terminal.connector.ClientSocketConnection
import com.hssoft.smartcheckout.opi_core.terminal.entity.Payment
import com.hssoft.smartcheckout.opi_core.terminal.entity.RequestType
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.CardRequest
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.DeviceRequest
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.ServiceRequest
import java.text.SimpleDateFormat
import java.util.*

const val SERVER_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val TEXT_OUTPUT_TIMEOUT = "120"

fun Date.toServerUTCFormat(): String =
    SimpleDateFormat(SERVER_UTC_FORMAT, Locale.getDefault()).format(this)


class Terminal(
    private val ipAddress: String?,
    private val inputPort: Int?,
    private val outputPort: Int?,
    private val timeout: Int = 4000,
    private val applicationSender: String?,
    private val workstationID: String?,
    private val clientChanel: ClientSocketConnection,
    private val serverChanel: Chanel1SocketConnector
) {

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
                ClientSocketConnection(),
                Chanel1SocketConnector()
            )
        }
    }

    fun initialization() {

        Thread(Runnable {
            clientChanel.openSendConnection(ipAddress, inputPort, timeout)

            //THIS SHIT NOT WORKS, I DON'T KNOW WHY =(
//            Thread(Runnable {
//                serverChanel.receiveData(outputPort) {
//                    handler.sendMessage(
//                        Message().apply {
//                            what = 10
//                            data.putString("10", "Message from device ->>>>>>>>>>>>>>>>>\n$it")
//                        })
//
//                }
//            }).start()

            val serviceRequest = ServiceRequest(
                requestType = RequestType.Initialisation.name,
                workstationID = workstationID,
                requestID = "0",
                elmeTunnelCallback = true,
                applicationSender = applicationSender,
                posData = ServiceRequest.PosData(
                    posTimeStamp = Date()
                )
            )
            clientChanel.sendData(serviceRequest.serializeToXMLString())
            val response0 =
                ServiceRequest().apply { deserializeFromXMLString(clientChanel.read() ?: "") }
            handler.sendMessage(
                Message().apply {
                    what = 0
                    data.putString("0", response0.toString())
                })


            val deviceRequest = DeviceRequest(
                requestID = "1",
                requestType = RequestType.Output.name,
                applicationSender = applicationSender,
                output = DeviceRequest.Output(
                    outDeviceTarget = "CashierDisplay",
                    textLine = DeviceRequest.TextLine(message = "Terminal is alive!!!!!!!! ..")
                )
            )
            clientChanel.sendData(deviceRequest.serializeToXMLString())
            val response1 =
                DeviceRequest().apply { deserializeFromXMLString(clientChanel.read() ?: "") }
            handler.sendMessage(Message().apply {
                what = 1
                data.putString("1", response1.toString())
            })

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
        val response2 = clientChanel.read()
        handler.sendMessage(
            Message().apply {
                what = 2
                data.putString("2", response2)
            })
    }

    fun transaction(paymentData: Payment) {
        clientChanel.openSendConnection(ipAddress, inputPort, timeout)
        val cardServiceRequest = CardRequest(
            elmeTunnelCallback = true,
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
        clientChanel.sendData(cardServiceRequest.serializeToXMLString())
        val response3 = clientChanel.read()
        handler.sendMessage(
            Message().apply {
                what = 3
                data.putString("3", response3)
            })

        status()
    }

    fun cancelTransaction(paymentData: Payment) {

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

        //just for log
        handler.sendMessage(
            Message().apply {
                what = 5
                data.putString("5", response5)
            })
    }

    fun logout() {
        val deviceRequest = DeviceRequest(
            requestID = "0",
            requestType = RequestType.Logoff.name,
            workstationID = workstationID,
            applicationSender = applicationSender,
            output = DeviceRequest.Output(
                outDeviceTarget = "CashierDisplay",
                textLine = DeviceRequest.TextLine(message = "Log out")
            )
        )
        clientChanel.sendData(deviceRequest.serializeToXMLString())
        val response7 = clientChanel.read()

        //just for log
        handler.sendMessage(
            Message().apply {
                what = 7
                data.putString("7", response7)
            })

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

        //just for log
        handler.sendMessage(
            Message().apply {
                what = 8
                data.putString("8", response8)
            })
    }
}