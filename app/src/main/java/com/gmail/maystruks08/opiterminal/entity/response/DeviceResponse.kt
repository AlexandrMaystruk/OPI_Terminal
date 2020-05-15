package com.gmail.maystruks08.opiterminal.entity.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "DeviceResponse")
data class DeviceResponse(

    @field:Element(name = "Output", required = false)
    val outputField: List<DeviceResponseOutput>? = null,

    @field:Element(name = "Input", required = false)
    val inputField: DeviceResponseInput? = null,

    @field:Element(name = "POSTimeStamp", required = false)
    val eventResultField: DeviceResponseEventResult? = null,

    @field:Element(name = "RequestType", required = false)
    val requestTypeField: String? = null,

    @field:Element(name = "ApplicationSender", required = false)
    val applicationSenderField: String? = null,

    @field:Element(name = "WorkstationID", required = false)
    val workstationIDField: String? = null,

    @field:Element(name = "TerminalID", required = false)
    val terminalIDField: String? = null,

    @field:Element(name = "POPID", required = false)
    val pOPIDField: String? = null,

    @field:Element(name = "RequestID", required = false)
    val requestIDField: String? = null,

    @field:Element(name = "SequenceID", required = false)
    val sequenceIDField: String? = null,

    @field:Element(name = "ReferenceRequestID", required = false)
    val referenceRequestIDField: String? = null,

    @field:Element(name = "OverallResult", required = false)
    val overallResultField: String? = null

)
