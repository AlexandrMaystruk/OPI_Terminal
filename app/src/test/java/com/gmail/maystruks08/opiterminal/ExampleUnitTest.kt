package com.gmail.maystruks08.opiterminal


import com.hssoft.smartcheckout.opi_core.terminal.entity.SimpleText
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.DeviceRequest
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val deviceRequest = DeviceRequest(
            requestID = "1",
            requestType = RequestType.Output.name,
            applicationSender = "applicationSender",
            output = DeviceRequest.Output(
                outDeviceTarget = "CashierDisplay",
                textLine = DeviceRequest.TextLine(
                    timeOut = TEXT_OUTPUT_TIMEOUT,
                    message = SimpleText("Terminal is alive!!!!!!!! ..")
                )
            )
        )

        println(deviceRequest)

        val response1 = DeviceRequest().apply { deserializeFromXMLString( deviceRequest.serializeToXMLString())}

        println(response1)
    }
}
