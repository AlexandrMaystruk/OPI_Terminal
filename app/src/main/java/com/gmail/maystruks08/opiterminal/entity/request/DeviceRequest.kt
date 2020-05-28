package com.gmail.maystruks08.opiterminal.entity.request

import com.gmail.maystruks08.opiterminal.entity.BaseXMLEntity
import com.gmail.maystruks08.opiterminal.entity.IGNORE_TAG
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.Reader
import java.io.StringReader

@Root(name = "DeviceRequest")
data class DeviceRequest(

    @field: Attribute(name = "xmlns")
    var xmlns: String = "http://www.nrf-arts.org/IXRetail/namespace",

    @field: Attribute(name = "xmlns:xsi")
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
        val format = Format("<?xml version=\"1.0\" encoding= \"ISO-8859-1\" ?>")
        val serializer = Persister(format)
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

        @field: Element(name = IGNORE_TAG, required = false)
        var message: String? = null,

        @field: Element(name = "Row", required = false)
        var row: String? = null,

        @field: Element(name = "Column", required = false)
        var column: String? = null,

        @field: Element(name = "CharSet", required = false)
        var charSet: String? = null,

        @field: Element(name = "Erase", required = false)
        var erase: String? = null,

        @field: Element(name = "Echo", required = false)
        var echo: String? = null,

        @field: Element(name = "Cursor", required = false)
        var cursor: String? = null,

        @field: Attribute(name = "TimeOut", required = false)
        var timeOut: String? = null,

        @field: Element(name = "Color", required = false)
        var color: String? = null,

        @field: Element(name = "Alignment", required = false)
        var alignment: String? = null,

        @field: Element(name = "Height", required = false)
        var height: String? = null,

        @field: Element(name = "Width", required = false)
        var width: String? = null,

        @field: Element(name = "CharStyle1", required = false)
        var charStyle1: String? = null,

        @field: Element(name = "CharStyle2", required = false)
        var charStyle2: String? = null,

        @field: Element(name = "CharStyle3", required = false)
        var charStyle3: String? = null,

        @field: Element(name = "PaperCut", required = false)
        var paperCut: String? = null,

        @field: Element(name = "MenuItem", required = false)
        var menuItem: String? = null
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
