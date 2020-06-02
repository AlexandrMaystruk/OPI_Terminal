package com.gmail.maystruks08.opiterminal.entity.response

import com.hssoft.smartcheckout.opi_core.terminal.entity.BaseXMLEntity
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.Reader
import java.io.StringReader

@Root(name = "DeviceResponse")
data class DeviceResponse(


    @field: Attribute(name = "RequestType")
    var RequestType: String = "",

    @field: Attribute(name = "ApplicationSender")
    var ApplicationSender: String = "",

    @field: Attribute(name = "OverallResult")
    var OverallResult: String = "",


    @field: Attribute(name = "xmlns", required = true)
    var xmlns: String = "http://www.nrf-arts.org/IXRetail/namespace",

    @field: Attribute(name = "xmlns:xsi", required = true)
    var xsi: String = "http://www.w3.org/2001/XMLSchema-instance",

    @field: Element(name = "Output")
    var output: Output = Output("", ""),

    @field: Element(name = "Input", required = false)
    var input: Input? = null,

    @field: Element(name = "EventResult", required = false)
    var eventResult: EventResult? = null,

    @field: Attribute(name = "WorkstationID", required = false)
    var WorkstationID: String? = null,

    @field: Attribute(name = "TerminalID", required = false)
    var TerminalID: String? = null,

    @field: Element(name = "Input", required = false)
    var POPID: String? = null,

    @field: Attribute(name = "RequestID", required = false)
    var RequestID: String? = null,

    @field: Attribute(name = "SequenceID", required = false)
    var SequenceID: String? = null,

    @field: Attribute(name = "ReferenceRequestID", required = false)
    var ReferenceRequestID: String? = null

) : BaseXMLEntity() {


    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        val format = Format("<?xml version=\"1.0\" encoding= \"ISO-8859-1\" ?>")
        val serializer = Persister(format)
        try {
            serializer.read(this::class.java, reader, false)
                ?.also {
                    this.output = it.output
                    this.input = it.input
                    this.eventResult = it.eventResult
                    this.RequestType = it.RequestType
                    this.ApplicationSender = it.ApplicationSender
                    this.WorkstationID = it.WorkstationID
                    this.TerminalID = it.TerminalID
                    this.POPID = it.POPID
                    this.RequestID = it.RequestID
                    this.SequenceID = it.SequenceID
                    this.ReferenceRequestID = it.ReferenceRequestID
                    this.OverallResult = it.OverallResult
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Root(name = "Output")
    data class Output(

        @field: Attribute(name = "OutDeviceTarget")
        var OutDeviceTarget: String,

        @field: Attribute(name = "OutResult")
        var OutResult: String
    )

    @Root(name = "EventResult")
    data class EventResult(
        @field: Attribute(name = "dispenser", required = false)
        var dispenser: String? = null,

        @field: Attribute(name = "productCode", required = false)
        var productCode: String? = null,

        @field: Attribute(name = "modifiedRequest", required = false)
        var modifiedRequest: String? = null,

        @field: Attribute(name = "SaleItem", required = false)
        var saleItem: List<SaleItem>? = null
    )

    @Root(name = "SaleItem")
    data class SaleItem(

        @field: Attribute(name = "productCode", required = false)
        var productCode: String? = null,

        @field: Attribute(name = "amount", required = false)
        var amount: String? = null,

        @field: Attribute(name = "unitMeasure", required = false)
        var unitMeasure: String? = null,

        @field: Attribute(name = "unitPrice", required = false)
        var unitPrice: String? = null,

        @field: Attribute(name = "quantity", required = false)
        var quantity: String? = null,

        @field: Attribute(name = "taxCode", required = false)
        var taxCode: String? = null,

        @field: Attribute(name = "additionalProductCode", required = false)
        var additionalProductCode: String? = null,

        @field: Attribute(name = "additionalProductInfo", required = false)
        var additionalProductInfo: String? = null,

        @field: Attribute(name = "typeMovement", required = false)
        var typeMovement: String? = null,

        @field: Attribute(name = "saleChannel", required = false)
        var saleChannel: String? = null,

        @field: Attribute(name = "ItemID", required = false)
        var ItemID: String? = null
    )

    @Root(name = "Input")
    data class Input(

        @field: Element(name = "InDeviceTarget")
        var InDeviceTarget: String,

        @field: Attribute(name = "InResult")
        var InResult: String? = null,

        @field: Element(name = "SecureData", required = false)
        var secureData: SecureData? = null,

        @field: Element(name = "InputValue", required = false)
        var inputValue: InputValue? = null
    )

    @Root(name = "InputValue")
    data class InputValue(

        @field: Attribute(name = "barcode", required = false)
        var barcode: String? = null,

        @field: Attribute(name = "inBoolean", required = false)
        var inBoolean: String? = null,

        @field: Attribute(name = "inNumber", required = false)
        var inNumber: String? = null,

        @field: Attribute(name = "inString", required = false)
        var inString: String? = null,

        @field: Attribute(name = "cardPAN", required = false)
        var cardPAN: String? = null,

        @field: Attribute(name = "startDate", required = false)
        var startDate: String? = null,

        @field: Attribute(name = "expiryDate", required = false)
        var expiryDate: String? = null
    )

    @Root(name = "SecureData")
    data class SecureData(var hex: String)
}
