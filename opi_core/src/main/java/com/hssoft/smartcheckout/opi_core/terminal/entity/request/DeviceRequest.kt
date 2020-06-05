package com.hssoft.smartcheckout.opi_core.terminal.entity.request

import com.hssoft.smartcheckout.opi_core.terminal.entity.BaseXMLEntity
import org.simpleframework.xml.*
import java.io.Reader
import java.io.StringReader

@Root(name = "DeviceRequest")
data class DeviceRequest(

    @field:Namespace(reference = "http://www.nrf-arts.org/IXRetail/namespace")
    var attr: String = "http://www.nrf-arts.org/IXRetail/namespace",

    @field:Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance")
    var xsi: String = "http://www.w3.org/2001/XMLSchema-instance",

    @field: Attribute(name = "RequestType")
    var requestType: String? = null,

    @field: Attribute(name = "ApplicationSender")
    var applicationSender: String? = null,

    @field: Attribute(name = "WorkstationID", required = false)
    var workstationID: String? = null,

    @field: Attribute(name = "TerminalID", required = false)
    var terminalID: String? = null,

    @field: Attribute(name = "POPID", required = false)
    var popID: String? = null,

    @field: Attribute(name = "RequestID", required = false)
    var requestID: String? = null,

    @field: Attribute(name = "SequenceID", required = false)
    var sequenceID: String? = null,

    @field: Element(name = "Output", required = false)
    var output: Output? = null,

    @field: Element(name = "Input", required = false)
    var input: Input? = null,

    @field: Element(name = "Event", required = false)
    var event: Event? = null

) : BaseXMLEntity() {


    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        try {
            val deviceRequest = serializer.read(this::class.java, reader, false)
            this.requestType = deviceRequest.requestType
            this.applicationSender = deviceRequest.applicationSender
            this.workstationID = deviceRequest.workstationID
            this.terminalID = deviceRequest.terminalID
            this.popID = deviceRequest.popID
            this.requestID = deviceRequest.requestID
            this.sequenceID = deviceRequest.sequenceID
            this.output = deviceRequest.output
            this.input = deviceRequest.input
            this.event = deviceRequest.event
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Root(name = "Output")
    data class Output(

        @field: Element(name = "TextLine", required = false)
        var textLine: TextLine? = null,

        @field: Element(name = "Buzzer", required = false)
        var buzzer: Buzzer? = null,

        @field: Element(name = "OutSecureData", required = false)
        var outSecureData: OutSecureData? = null,

        @field: Element(name = "MAC", required = false)
        var mAC: MAC? = null,

        @field: Element(name = "imageFile", required = false)
        var imageFile: String? = null,

        @field: Element(name = "OutDeviceTarget", required = false)
        var outDeviceTarget: String? = null,

        @field: Element(name = "InputSynchronize", required = false)
        var inputSynchronize: String? = null,

        @field: Element(name = "Complete", required = false)
        var complete: String? = null,

        @field: Element(name = "Immediate", required = false)
        var immediate: String? = null

    )


    @Root(name = "Input")
    data class Input(

        @field: Element(name = "Command", required = false)
        var command: Command? = null,

        @field: Element(name = "InSecureData", required = false)
        var inSecureData: InSecureData? = null,

        @field: Element(name = "InDeviceTarget", required = false)
        var inDeviceTarget: String? = null
    )


    @Root(name = "Event")
    data class Event(
        @field: Element(name = "EventData", required = false)
        var eventData: EventData? = null,

        @field: Element(name = "EventType")
        var eventType: String? = null
    )


    @Root(name = "TextLine")
    data class TextLine(

        @field: Text
        var message: String? = null

    )

    @Root(name = "Buzzer")
    data class Buzzer(

        @field: Element(name = "DurationBeep", required = false)
        var durationBeep: String? = null,

        @field: Element(name = "CounterBeep", required = false)
        var counterBeep: String? = null,

        @field: Element(name = "DurationPause", required = false)
        var durationPause: String? = null,

        @field: Element(name = "text", required = false)
        var text: String? = null
    )


    @Root(name = "EventData")
    data class EventData(
        @field: Element(name = "dispenser")
        var dispenser: String? = null,

        @field: Element(name = "CardIdent")
        var cardIdent: CardIdent? = null,

        @field: Element(name = "restrictionCodes")
        var restrictionCodes: String? = null
    )

    @Root(name = "CardIdent")
    data class CardIdent(

        @field: Element(name = "barcode")
        var barcode: String? = null,

        @field: Element(name = "inString")
        var inString: String? = null,

        @field: Element(name = "cardPAN")
        var cardPAN: String? = null,

        @field: Element(name = "startDate")
        var startDate: String? = null,

        @field: Element(name = "expiryDate")
        var expiryDate: String? = null,

        @field: Element(name = "cardCircuit")
        var cardCircuit: String? = null
    )


    @Root(name = "InSecureData")
    data class InSecureData(
        @field: Element(name = "hex")
        var hex: String
    )

    @Root(name = "Command")
    data class Command(
        @field: Element(name = "Length")
        var length: String? = null,

        @field: Element(name = "MinLength")
        var minLength: String? = null,

        @field: Element(name = "MaxLength")
        var maxLength: String? = null,

        @field: Element(name = "Decimals")
        var decimals: String? = null,

        @field: Element(name = "Separator")
        var separator: String? = null,

        @field: Element(name = "CardReadElement")
        var cardReadElement: String? = null,

        @field: Element(name = "TimeOut")
        var timeOut: String? = null,

        @field: Element(name = "text")
        var text: String? = null
    )

    @Root(name = "MAC")
    data class MAC(
        @field: Element(name = "hex")
        var hex: String
    )

    @Root(name = "OutSecureData")
    data class OutSecureData(
        @field: Element(name = "hex")
        var hex: String
    )

}
