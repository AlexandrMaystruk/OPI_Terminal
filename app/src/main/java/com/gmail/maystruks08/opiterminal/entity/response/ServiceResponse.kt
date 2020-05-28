package com.gmail.maystruks08.opiterminal.entity.response

import com.gmail.maystruks08.opiterminal.entity.BaseXMLEntity
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.Reader
import java.io.StringReader

@Root(name = "ServiceResponse")
data class ServiceResponse(

    @field: Attribute(name = "xmlns", required = false)
    var xmlns: String? = "http://www.nrf-arts.org/IXRetail/namespace",

    @field: Attribute(name = "xmlns:xsi", required = false)
    var xsi: String? = "http://www.w3.org/2001/XMLSchema-instance",

    @field:Element(name = "RequestType", required = false)
    var RequestType: String? = null,

    @field:Element(name = "ApplicationSender", required = false)
    var ApplicationSender: String? = null,

    @field:Element(name = "WorkstationID", required = false)
    var WorkstationID: String? = null,

    @field:Element(name = "POPID", required = false)
    var POPID: String? = null,

    @field:Element(name = "RequestID", required = false)
    var RequestID: String? = null,

    @field:Element(name = "OverallResult", required = false)
    var OverallResult: String? = null,

    @field:Element(name = "Terminal", required = false)
    var terminal: Terminal? = null,

    @field:Element(name = "Authorisation", required = false)
    var authorisation: Authorisation? = null,

    @field:Element(name = "Reconciliation", required = false)
    var reconciliation: Reconciliation? = null,

    @field:Element(name = "DiagnosisResult", required = false)
    var diagnosisResult: String? = null,

    @field:Element(name = "OriginalHeader", required = false)
    var originalHeader: OriginalHeader? = null,

    @field:Element(name = "PrivateData", required = false)
    var privateData: String? = null

) : BaseXMLEntity() {

    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        val format = Format("<?xml version=\"1.0\" encoding= \"ISO-8859-1\" ?>")
        val serializer = Persister(format)
        try {
            serializer.read(this::class.java, reader, false)
                ?.also {
                    this.RequestType = it.RequestType
                    this.POPID = it.POPID
                    this.RequestID = it.RequestID
                    this.OverallResult = it.OverallResult
                    this.terminal = it.terminal
                    this.authorisation = it.authorisation
                    this.reconciliation = it.reconciliation
                    this.diagnosisResult = it.diagnosisResult
                    this.originalHeader = it.originalHeader
                    this.privateData = it.privateData
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Root(name = "Terminal")
    data class Terminal(

        @field:Element(name = "TerminalID", required = false)
        var TerminalID: String? = null,

        @field:Element(name = "TerminalBatch", required = false)
        var TerminalBatch: String? = null,

        @field:Element(name = "STAN", required = false)
        var STAN: String? = null
    )

    @Root(name = "Authorisation")
    data class Authorisation(

        @field:Element(name = "AcquirerID", required = false)
        var AcquirerID: String? = null,

        @field:Element(name = "TimeStamp", required = false)
        var TimeStamp: String? = null,

        @field:Element(name = "ApprovalCode", required = false)
        var ApprovalCode: String? = null,

        @field:Element(name = "AcquirerBatch", required = false)
        var AcquirerBatch: String? = null
    )

    @Root(name = "Authorisation")
    data class Reconciliation(

        @field:Element(name = "TotalAmount", required = false)
        var totalAmount: TotalAmount? = null,

        @field:Element(name = "LanguageCode", required = false)
        var LanguageCode: String? = null
    )

    @Root(name = "TotalAmount")
    data class TotalAmount(

        @field: Element(name = "NumberPayments", required = true)
        var NumberPayments: String? = null,

        @field: Element(name = "PaymentType", required = true)
        var PaymentType: String? = null,

        @field: Element(name = "Currency", required = true)
        var Currency: String? = null,

        @field: Element(name = "CardCircuit", required = true)
        var CardCircuit: String? = null,

        @field: Element(name = "Acquirer", required = true)
        var Acquirer: String? = null,

        //not field
        var total: String? = null
    )

    @Root(name = "OriginalHeader")
    data class OriginalHeader(

        @field: Element(name = "RequestType", required = true)
        var RequestType: String? = null,

        @field: Element(name = "ApplicationSender", required = true)
        var ApplicationSender: String? = null,

        @field: Element(name = "WorkstationID", required = true)
        var WorkstationID: String? = null,

        @field: Element(name = "POPID", required = true)
        var POPID: String? = null,

        @field: Element(name = "RequestID", required = true)
        var RequestID: String? = null,

        @field: Element(name = "OverallResult", required = true)
        var OverallResult: String? = null
    )
}









