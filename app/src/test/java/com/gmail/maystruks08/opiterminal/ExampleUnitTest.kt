package com.gmail.maystruks08.opiterminal

import com.gmail.maystruks08.opi_terminal_api.connection.entity.SimpleText
import com.gmail.maystruks08.opi_terminal_api.connection.entity.request.CardRequest
import com.gmail.maystruks08.opi_terminal_api.connection.entity.request.DeviceRequest
import com.gmail.maystruks08.opi_terminal_api.connection.terminal.toServerUTCFormat
import org.junit.Test
import java.util.*


class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {


//        val serviceRequest = ServiceRequest(
//            requestType = "Initialisation",
//            workstationID = "workstationID",
//            requestID = "requestID",
//            elmeTunnelCallback = true,
//            applicationSender = "applicationSender",
//            posData = ServiceRequest.PosData(posTimeStamp = Date())
//        ).serializeToXMLString() ?: ""
//
//        println(serviceRequest)

//        val deviceRequest = DeviceRequest(
//            requestID = "0",
//            requestType = "Output",
//            applicationSender = "applicationSender",
//            output = DeviceRequest.Output(
//                outDeviceTarget = "CashierDisplay",
//                textLine = DeviceRequest.TextLine(
//                    timeOut = "120",
//                    message = SimpleText("Hi Babe!!!!!!!! ..")
//                )
//            )
//        ).serializeToXMLString() ?: ""
//
//        println(deviceRequest)

        val cardServiceRequest = com.gmail.maystruks08.opi_terminal_api.connection.entity.request.CardRequest(
            elmeTunnelCallback = true,
            requestID = "3c0c5e789e534754be2736fb266e5bd0",
            workstationID = "1CEEC90780FB",
            requestType = "CardPayment",
            posData = com.gmail.maystruks08.opi_terminal_api.connection.entity.request.CardRequest.PosData(
                posTimeStamp = Date().toServerUTCFormat(),
                usePreselectedCard = false
            ),
            privateData = com.gmail.maystruks08.opi_terminal_api.connection.entity.request.CardRequest.PrivateData(
                lastReceiptNumber = "3"
            ),
            totalAmount = com.gmail.maystruks08.opi_terminal_api.connection.entity.request.CardRequest.TotalAmount(
                currency = "EUR",
                paymentAmount = com.gmail.maystruks08.opi_terminal_api.connection.entity.SimpleText(
                    "5.00"
                )
            )
        ).serializeToXMLString() ?: ""

        println(cardServiceRequest)

    }
}
