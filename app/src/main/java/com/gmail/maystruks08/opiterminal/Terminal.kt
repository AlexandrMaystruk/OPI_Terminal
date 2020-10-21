package com.gmail.maystruks08.opiterminal

import android.os.Handler
import android.os.Message
import com.hssoft.smartcheckout.opi_core.terminal.connector.Chanel1SocketConnector
import com.hssoft.smartcheckout.opi_core.terminal.connector.Chanel0SocketConnector
import com.hssoft.smartcheckout.opi_core.terminal.entity.Payment
import com.hssoft.smartcheckout.opi_core.terminal.entity.RequestType
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.CardRequest
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.DeviceRequest
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.ServiceRequest
import com.hssoft.smartcheckout.opi_core.terminal.entity.response.CardResponse
import com.hssoft.smartcheckout.opi_core.terminal.entity.response.ServiceResponse
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

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
    private val clientChanel: Chanel0SocketConnector,
    private val serverChanel: Chanel1SocketConnector
) {
    private val tag = "OPI_Terminal"
    private var logger = Logger.getLogger(tag)
    lateinit var handler: Handler

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

    fun login() {
        Thread {
            clientChanel.openSendConnection(ipAddress, inputPort, timeout)
//            val serviceRequest1 = ServiceRequest(
//                requestType = RequestType.Login.name,
//                workstationID = workstationID,
//                requestID = "0",
//                applicationSender = applicationSender,
//                posData = ServiceRequest.PosData(
//                    posTimeStamp = Date()
//                )
//            )
            val login ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<ServiceRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" RequestType=\"Login\" ApplicationSender=\"CashAssist\" WorkstationID=\"WINDOWS-3N69TMJ\" RequestID=\"162\" /> "
            clientChanel.sendData(login)
            logger.log(Level.INFO, login)

            val serviceRequest = ServiceRequest(clientChanel.read().orEmpty())

            sendLogToUI("0", serviceRequest.toString())
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
        val serviceResponse = ServiceResponse(clientChanel.read().orEmpty())
        sendLogToUI("2", serviceResponse.toString())
        clientChanel.disconnect()
    }

    fun transaction(paymentData: Payment) {
        clientChanel.openSendConnection(ipAddress, inputPort, timeout)

        val cardReq = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<CardServiceRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" RequestType=\"CardPayment\" ApplicationSender=\"CashAssist\" WorkstationID=\"WINDOWS-3N69TMJ\" RequestID=\"164\" ReferenceNumber=\"1\">\n" +
                "  <POSdata>\n" +
                "    <POSTimeStamp>2020-05-11T09:34:44.5049812+02:00</POSTimeStamp>\n" +
                "    <TransactionNumber>32</TransactionNumber>\n" +
                "  </POSdata>\n" +
                "  <TotalAmount Currency=\"EUR\">1.90</TotalAmount>\n" +
                "</CardServiceRequest> "
        clientChanel.sendData(cardReq)


        val dr0 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                "<DeviceRequest xmlns=\"http://www.nrf-arts.org/IXRetail/namespace\" RequestID=\"0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" RequestType=\"Output\" WorkstationID=\"WINDOWS-3N69TMJ\" ApplicationSender=\"SECpos II embedded\" xsi:noNamespaceSchemaLocation=\"C:\\Windows\\OPISchema\\DeviceRequest.xsd\"><Output OutDeviceTarget=\"CashierDisplay\"><TextLine TimeOut=\"120\">Card please</TextLine></Output></DeviceRequest> \n"

        val dr1 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                "<DeviceRequest xmlns=\"http://www.nrf-arts.org/IXRetail/namespace\" RequestID=\"1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" RequestType=\"Output\" WorkstationID=\"WINDOWS-3N69TMJ\" ApplicationSender=\"SECpos II embedded\" xsi:noNamespaceSchemaLocation=\"C:\\Windows\\OPISchema\\DeviceRequest.xsd\"><Output OutDeviceTarget=\"CashierDisplay\"><TextLine TimeOut=\"120\">Please wait ...</TextLine></Output></DeviceRequest> \n"

        val dr2 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                "<DeviceRequest xmlns=\"http://www.nrf-arts.org/IXRetail/namespace\" RequestID=\"2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" RequestType=\"Output\" WorkstationID=\"WINDOWS-3N69TMJ\" ApplicationSender=\"SECpos II embedded\" xsi:noNamespaceSchemaLocation=\"C:\\Windows\\OPISchema\\DeviceRequest.xsd\"><Output OutDeviceTarget=\"CashierDisplay\"><TextLine TimeOut=\"120\">Connecting ...</TextLine></Output></DeviceRequest> \n"

        val dr3 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                "<DeviceRequest xmlns=\"http://www.nrf-arts.org/IXRetail/namespace\" RequestID=\"3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" RequestType=\"Output\" WorkstationID=\"WINDOWS-3N69TMJ\" ApplicationSender=\"SECpos II embedded\" xsi:noNamespaceSchemaLocation=\"C:\\Windows\\OPISchema\\DeviceRequest.xsd\"><Output OutDeviceTarget=\"CashierDisplay\"><TextLine TimeOut=\"120\">Connecting ...</TextLine></Output></DeviceRequest> \n"


        val printReceipt = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<DeviceResponse xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" RequestType=\"Output\" WorkstationID=\"WINDOWS-3N69TMJ\" RequestID=\"5\" OverallResult=\"Success\">\n" +
                "  <Output OutDeviceTarget=\"PrinterReceipt\" OutResult=\"Success\" />\n" +
                "</DeviceResponse> "

        val server = Thread {
            serverChanel.receiveData(outputPort) {
                sendLogToUI("10","Message from device $it")
            }
            serverChanel.write(printReceipt)
        }
        server.start()

        val cardResponse = CardResponse(clientChanel.read().orEmpty())
        sendLogToUI("3", cardResponse.toString())



        clientChanel.disconnect()

//        val cardServiceRequest = CardRequest(
//            requestID = 10,
//            workstationID = workstationID,
//            requestType = RequestType.CardPayment.name,
//            posData = CardRequest.PosData(
//                posTimeStamp = Date().toServerUTCFormat(),
//                usePreselectedCard = false
//            ),
//            privateData = CardRequest.PrivateData(
//                lastReceiptNumber = paymentData.lastReceiptNumber.toString()
//            ),
//            totalAmount = CardRequest.TotalAmount(
//                currency = paymentData.currency,
//                paymentAmount = paymentData.total.toString()
//            )
//        )
//        val cardRequest = cardServiceRequest.serializeToXMLString()
//        clientChanel.sendData(cardRequest)
//
//        val cardResponse = CardResponse(clientChanel.read().orEmpty())
//        sendLogToUI("3", cardResponse.toString())
//
//        Thread {
//            serverChanel.receiveData(outputPort) {
//                sendLogToUI("10", "Message from device ->>>\n$it")
//                serverChanel.write(
//                    DeviceRequest(
//                        requestID = 12,
//                        workstationID = workstationID,
//                        requestType = RequestType.Output.name
//                    ).serializeToXMLString().toByteArray()
//                )
//                serverChanel.closeConnection()
//            }
//        }.start()
//
//        clientChanel.disconnect()
    }


    fun getMessageFromDevice() {
        var serverThread: Thread? = null
        serverThread = Thread {
            serverChanel.receiveData(outputPort) {
                sendLogToUI("10", "Message from device ->>>>>>>>>>>>>>>>>\n$it")
                serverThread?.interrupt()
            }
        }
        serverThread.start()
    }

    fun cancelTransaction(paymentData: Payment) {
        clientChanel.openSendConnection(ipAddress, inputPort, timeout)
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
        clientChanel.sendData(cardServiceRequest.serializeToXMLString())
        val cardResponse = CardResponse(clientChanel.read().orEmpty())
        sendLogToUI("5", cardResponse.toString())
        clientChanel.disconnect()
    }

    fun logout() {
        clientChanel.openSendConnection(ipAddress, inputPort, timeout)
        val serviceRequest = ServiceRequest(
            requestType = RequestType.Logoff.name,
            workstationID = workstationID,
            requestID = "8",
            applicationSender = applicationSender,
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        clientChanel.sendData(serviceRequest.serializeToXMLString())
        val serviceResponse = ServiceResponse(clientChanel.read().orEmpty())
        sendLogToUI("8", serviceResponse.toString())
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