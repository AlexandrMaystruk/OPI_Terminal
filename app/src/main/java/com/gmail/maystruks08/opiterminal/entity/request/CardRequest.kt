package com.gmail.maystruks08.opiterminal.entity.request

import com.gmail.maystruks08.opiterminal.entity.BaseXMLEntity
import com.gmail.maystruks08.opiterminal.entity.SimpleText
import org.simpleframework.xml.*
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.Reader
import java.io.StringReader


@Root(name = "CardServiceRequest")
data class CardRequest(

    @field: Attribute(name = "ElmeTunnelCallback", required = false)
    var elmeTunnelCallback: Boolean? = null,

    @field: Attribute(name = "RequestID")
    var requestID: String? = null,

    @field: Attribute(name = "RequestType")
    var requestType: String? = null,

    @field: Attribute(name = "WorkstationID")
    var workstationID: String? = null,

    @field:Namespace(reference = "http://www.nrf-arts.org/IXRetail/namespace")
    var attr: String = "",

    @field:Element(name = "POSdata", required = false)
    var posData: PosData? = null,

    @field: Element(name = "PrivateData", required = true)
    var privateData: PrivateData? = null,


    @field: Element(name = "TotalAmount")
    var totalAmount: TotalAmount? = null,

    @field: Element(name = "OriginalTransaction", required = false)
    var originalTransaction: OriginalTransaction? = null,

    @field: Attribute(name = "ApplicationSender", required = false)
    var applicationSender: String? = null,

    @field: Attribute(name = "POPID", required = false)
    var popID: String? = null


) : BaseXMLEntity() {

    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        val format = Format("<?xml version=\"1.0\" encoding= \"ISO-8859-1\" ?>")
        val serializer = Persister(format)
        try {
            val cardServiceRequest = serializer.read(CardRequest::class.java, reader, false)
            this.posData = cardServiceRequest.posData
            this.privateData = cardServiceRequest.privateData
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Root(name = "TotalAmount")
    data class TotalAmount(

        @field: Element(name = "SimpleText", required = false)
        var paymentAmount: SimpleText? = null,

        @field: Element(name = "CashBackAmount", required = false)
        var cashBackAmount: String? = null,

        @field: Element(name = "OriginalAmount", required = false)
        var originalAmount: String? = null,

        @field: Attribute(name = "Currency", required = false)
        var currency: String? = null
    )


    @Root(name = "PrivateData")
    data class PrivateData(

        @field: Element(name = "LastReceiptNumber", required = true)
        var lastReceiptNumber: String? = null
    )

    @Root(name = "POSdata")
    data class PosData constructor(

        @field:Element(name = "POSTimeStamp", required = false)
        var posTimeStamp: String? = null,

        @field:Element(name = "ClerkID", required = false)
        var clerkId: String? = null,

        @field:Element(name = "ManualPAN", required = false)
        var manualPAN: Boolean? = null,

        @field:Element(name = "ClerkPermission", required = false)
        var clerkPermission: ClerkPermission? = null,

        @field:Element(name = "TransactionNumber", required = false)
        var transactionNumber: String? = null,

        @field:Element(name = "UsePreselectedCard", required = false)
        var usePreselectedCard: Boolean? = null
    )


    @Root(name = "OriginalTransaction")
    data class OriginalTransaction constructor(

        @field:Element(name = "TerminalID", required = false)
        var terminalID: String? = null,

        @field:Element(name = "TerminalBatch", required = false)
        var terminalBatch: String? = null,

        @field:Element(name = "STAN", required = false)
        var STAN: String? = null,

        @field:Element(name = "TimeStamp", required = false)
        var timeStamp: String? = null,

        @field:Element(name = "ApplicationID", required = false)
        var applicationID: String? = null
    )

    @Root(name = "CardServiceRequest")
    enum class ClerkPermission { Low, Medium, High }

}

