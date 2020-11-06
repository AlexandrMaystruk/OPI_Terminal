package com.gmail.maystruks08.opi_core.entity.request

import com.gmail.maystruks08.opi_core.entity.BaseXMLEntity
import org.simpleframework.xml.*
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.Reader
import java.io.StringReader

@Root(name = "DeviceRequest")
data class DeviceRequest(

    @field:Namespace(reference = "http://www.nrf-arts.org/IXRetail/namespace")
    var attr: String = "http://www.nrf-arts.org/IXRetail/namespace",

    @field:Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance")
    var xsi: String = "http://www.w3.org/2001/XMLSchema-instance",

    @field: Attribute(name = "RequestType")
    var requestType: String = "",

    @field: Attribute(name = "ApplicationSender", required = false)
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
    var output: Output? = null

) : BaseXMLEntity() {

    fun isPrinterRequest(): Boolean {
        return output?.outDeviceTarget == "Printer"
    }

    fun isPrinterReceiptRequest(): Boolean {
        return output?.outDeviceTarget == "PrinterReceipt"
    }

    fun getReceiptString(): String {
        return StringBuilder().apply {
            output?.textLines?.forEach { if (!it.text.isNullOrEmpty()) appendLine(it.text) }
        }.toString()
    }

    fun getTotalAmount(): String? {
        return output?.textLines?.firstOrNull { it.text == "Gesamt" || it.text == "Total" }?.text?.let { line ->
            val startValueIndex = line.indexOfLast { it == ' ' }
            if (startValueIndex == -1) return@let null
            line.substring(startValueIndex, line.lastIndex)
        }
    }

    @Root(name = "Output")
    data class Output(

        @field: Attribute(name = "OutDeviceTarget", required = false)
        var outDeviceTarget: String? = null,

        @field: ElementList(inline = true, required = false)
        var textLines: List<TextLine>? = null
    )

    @Root(name = "TextLine")
    data class TextLine(

        @field: Attribute(name = "Erase", required = false)
        var erase: Boolean? = null,

        @field: Text(required = false)
        var text: String? = null
    )

    constructor(xmlString: String) : this() {
        deserializeFromXMLString(xmlString)
    }

    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        val format = Format("<?xml version=\"1.0\" encoding= \"ISO-8859-1\" ?>")
        val serializer = Persister(format)
        try {
            serializer.read(this::class.java, reader, false)
                ?.also {
                    this.workstationID = it.workstationID
                    this.requestType = it.requestType
                    this.requestID = it.requestID
                    this.output = it.output
                    this.applicationSender = it.applicationSender
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
