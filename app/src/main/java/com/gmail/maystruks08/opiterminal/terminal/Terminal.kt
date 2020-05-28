package com.gmail.maystruks08.opiterminal.terminal

import android.util.Log
import com.gmail.maystruks08.opiterminal.entity.request.DeviceRequest
import com.gmail.maystruks08.opiterminal.entity.response.DeviceResponse
import com.gmail.maystruks08.opiterminal.entity.request.ServiceRequest
import com.gmail.maystruks08.opiterminal.entity.response.ServiceResponse
import java.util.*


class Terminal(
    private val ipAddress: String?,
    private val inputPort: Int?,
    private val outputPort: Int?,
    private val timeout: Int?
) {

    private val tag = "Terminal"
    private val clientChanel = ClientSocketConnection()
    private val serverChanel = ServerSocketConnection()


    class Builder {

        private var ipAddress: String? = null
        private var inputPort: Int? = null
        private var outputPort: Int? = null
        private var timeout: Int? = null

        fun ipAddress(ip: String) = apply { this.ipAddress = ip }
        fun inputPort(port: Int) = apply { this.inputPort = port }
        fun outputPort(port: Int) = apply { this.outputPort = port }
        fun timeout(timeout: Int) = apply { this.timeout = timeout }

        fun build(): Terminal {
            return Terminal(
                ipAddress,
                inputPort,
                outputPort,
                timeout
            )
        }
    }

    fun login(workstationID: String, requestID: String, applicationSender: String) {

        Thread(Runnable {
            clientChanel.openSendConnection(ipAddress, inputPort)

            val serviceRequest = ServiceRequest(
                requestType = "Activate",
                workstationID = workstationID,
                requestID = requestID,
                elmeTunnelCallback = true,
                applicationSender = applicationSender,
                posData = ServiceRequest.PosData(
                    posTimeStamp = Date()
                )
            ).serializeToXMLString() ?: ""


            val deviceRequest = DeviceRequest(
                requestID = "0",
                requestType = "Output",
                applicationSender = "applicationSender",
                output = DeviceRequest.Output(
                    outDeviceTarget = "CashierDisplay",
                    textLine = DeviceRequest.TextLine(
                        timeOut = "120",
                        message = "Hi Babe!!!!!!!! .."
                    )
                )
            ).serializeToXMLString() ?: ""

            clientChanel.sendData(serviceRequest)

            val chanelReadData = clientChanel.read() ?: ""
            print(chanelReadData)
            val serviceResponse = ServiceResponse()
                .apply { deserializeFromXMLString(chanelReadData) }
            Log.d(tag, serviceResponse.toString())

            clientChanel.sendData(deviceRequest)

            val readData = clientChanel.read() ?: ""
            print(readData)
            val deviceResponse = DeviceResponse()
                .apply { deserializeFromXMLString(readData) }
            Log.d(tag, deviceResponse.toString())

        }).start()


        Thread(Runnable {
            serverChanel.receiveData(outputPort) {
                Log.d(tag, "Receive login data $it")
            }
        }).start()
    }

    fun transaction(paymentData: Payment) {
//        socketConnectionHelper.receiveData(outputPort) {
//            Log.d(tag, "Receive transaction data $it")
//        }
//
//        val testEntity = ServiceRequest()
//            .apply {
//                this.workstationID = "SmartCheckout id124235"
//                this.applicationSender = "SmartCheckout android"
//                this.posData = PosData(Date(), "clerkId = 10", true, ClerkPermission.High, paymentData.transactionId)
//                this.privateData = PrivateData(PrepaidCard("", false, paymentData.total.toString()), listOf("Text 1", "Text 2", "Cat"))
//            }
//
//        val xml = testEntity.serializeToXMLString()
//        if (xml != null) {
//            socketConnectionHelper.sendData(xml)
//        }
    }

    fun logout() {
//        socketConnectionHelper.receiveData(outputPort) {
//            Log.d(tag, "Receive logout data $it")
//        }
//        socketConnectionHelper.sendData("xml")
//        socketConnectionHelper.closeConnection()
    }
}