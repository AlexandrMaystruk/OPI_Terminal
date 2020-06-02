package com.hssoft.smartcheckout.opi_core.terminal.entity.request

import com.hssoft.smartcheckout.opi_core.terminal.entity.BaseXMLEntity
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.Reader
import java.io.StringReader
import java.math.BigDecimal
import java.util.*


@Root(name = "ServiceRequest")
data class ServiceRequest(


    @field:Attribute(name = "ApplicationSender")
    var applicationSender: String? = null,

    @field:Attribute(name = "RequestID", required = false)
    var requestID: String? = null,


    @field:Attribute(name = "RequestType", required = false)
    var requestType: String? = null,

    @field:Attribute(name = "WorkstationID", required = false)
    var workstationID: String? = null,

    @field:Attribute(name = "POPID", required = false)
    var popID: String? = null,

    @field:Attribute(name = "ElmeTunnelCallback", required = false)
    var elmeTunnelCallback: Boolean? = null,


    @field:Element(name = "POSdata", required = false)
    var posData: PosData? = null,

    @field:Element(name = "TotalAmount", required = false)
    var totalAmount: TotalAmount? = null,

    @field:Element(name = "Agent", required = false)
    var agent: String? = null,

    @field:Element(name = "AgentSpecified", required = false)
    var agentSpecified: Boolean? = null,

    @field:Element(name = "PrivateData", required = false)
    var privateData: PrivateData? = null


) : BaseXMLEntity() {

    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        val format = Format("<?xml version=\"1.0\" encoding= \"ISO-8859-1\" ?>")
        val serializer = Persister(format)
        try {
            serializer.read(this::class.java, reader, false)
                ?.also {
                    this.posData = it.posData
                    this.totalAmount = it.totalAmount
                    this.agent = it.agent
                    this.agentSpecified = it.agentSpecified
                    this.privateData = it.privateData
                    this.requestType = it.requestType
                    this.applicationSender = it.applicationSender
                    this.workstationID = it.workstationID
                    this.popID = it.popID
                    this.requestID = it.requestID
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Root(name = "ClerkPermission")
    enum class ClerkPermission { Low, Medium, High }


    @Root(name = "POSdata")
    data class PosData constructor(

        @field:Element(name = "POSTimeStamp", required = false)
        var posTimeStamp: Date? = null,

        @field:Element(name = "ShiftNumber", required = false)
        var shiftNumber: String? = null,

        @field:Element(name = "ClerkID", required = false)
        var clerkId: String? = null,

        @field:Element(name = "DiagnosisMethod", required = false)
        var diagnosisMethod: DiagnosisMethod? = null,

        @field:Element(name = "LanguageCode", required = false)
        var LanguageCode: String? = null,

        @field:Element(name = "ManualPAN", required = false)
        var manualPAN: Boolean? = null,

        @field:Element(name = "ClerkPermission", required = false)
        var clerkPermission: ClerkPermission? = null,

        @field:Element(name = "TransactionNumber", required = false)
        var transactionNumber: String? = null

    )

    @Root(name = "TotalAmount")
    class TotalAmount(

        @field:Element(name = "Currency", required = false)
        var currency: Currency? = null,

        @field:Element(name = "CurrencySpecified", required = false)
        var currencySpecified: Boolean? = null,

        @field:Element(name = "Value", required = false)
        var value: BigDecimal? = null

    )

    @Root(name = "PrivateData")
    data class PrivateData(

        @field:Element(name = "PrepaidCard", required = false)
        private var prepaidCard: PrepaidCard? = null,

        @field:ElementList(name = "Text", inline = true, required = false)
        private var textFields: List<String>? = null

    )

    @Root(name = "PrepaidCard")
    data class PrepaidCard(

        @field:Element(name = "Paymode", required = false)
        private var payModeField: String? = null,

        @field:Element(name = "PaymodeSpecified", required = false)
        private var payModeFieldSpecified: Boolean? = null,

        @field:Element(name = "Value", required = false)
        private var value: String? = null
    )

    @Root(name = "Currency")
    enum class Currency { EUR, CHF }


    @Root(name = "DiagnosisMethod")
    enum class DiagnosisMethod { OnLine, Local, POPInit, POPInitAll, PrinterStatus }

}


