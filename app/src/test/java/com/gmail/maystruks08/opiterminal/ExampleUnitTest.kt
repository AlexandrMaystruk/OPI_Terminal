package com.gmail.maystruks08.opiterminal

import com.gmail.maystruks08.opiterminal.entity.request.DeviceRequest
import com.gmail.maystruks08.opiterminal.entity.request.ServiceRequest
import com.gmail.maystruks08.opiterminal.entity.response.ServiceResponse
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val testOut = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n<ServiceResponse xmlns=\"http://www.nrf-arts.org/IXRetail/namespace\" RequestID=\"b2d8b908-57fa-4808-914e-42db425b3d06\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" RequestType=\"Login\" OverallResult=\"Success\" WorkstationID=\"qqqqqqqqq\" ApplicationSender=\"SmartCheckout\" xsi:noNamespaceSchemaLocation=\"C:\\Windows\\OPISchema\\ServiceResponse.xsd\"><Terminal TerminalID=\"48306007\" /><PrivateData><RebootInfo>2020-05-29T03:00:00</RebootInfo></PrivateData></ServiceResponse>"

        val serviceResponse = ServiceResponse()
            .apply { deserializeFromXMLString(testOut) }

        print(serviceResponse)

        val serviceRequest = ServiceRequest(
            requestType = "Initialisation",
            workstationID = "workstationID",
            requestID = "requestID",
            elmeTunnelCallback = true,
            applicationSender = "applicationSender",
            posData = ServiceRequest.PosData(posTimeStamp = Date())
        ).apply {

        }.serializeToXMLString() ?: ""

        print(serviceRequest)

        val deviceRequest = DeviceRequest(
            requestID = "0",
            requestType = "Output",
            applicationSender = "applicationSender",
            output = DeviceRequest.Output(
                outDeviceTarget = "CashierDisplay",
                textLine = DeviceRequest.TextLine(timeOut = "120", message = "Please waite..")
            )
        ).serializeToXMLString() ?: ""

        print(deviceRequest)

        val des = DeviceRequest().apply { deserializeFromXMLString(deviceRequest) }

        print(des)


    }
}
