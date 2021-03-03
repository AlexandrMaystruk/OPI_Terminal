package com.gmail.maystruks08.opi_core.entity.response

import com.gmail.maystruks08.opi_core.entity.BaseXMLEntity
import com.gmail.maystruks08.opi_core.entity.OperationResult
import org.simpleframework.xml.*
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.Reader
import java.io.StringReader

@Root(name = "ServiceResponse")
data class ServiceResponse(

    @field:Attribute(name = "RequestType", required = true)
    var requestType: String? = null,

    @field:Attribute(name = "ApplicationSender", required = false)
    var applicationSender: String? = null,

    @field:Attribute(name = "WorkstationID", required = true)
    var workstationID: String? = null,

    @field:Attribute(name = "RequestID", required = true)
    var requestID: String? = null,

    @field:Attribute(name = "OverallResult", required = false)
    var operationResult: OperationResult = OperationResult.Failure,

    @field:Element(name = "Terminal", required = false)
    var terminal: Terminal? = null,

    @field:Element(name = "Authorisation", required = false)
    var authorisation: Authorisation? = null,

    @field:Element(name = "Reconciliation", required = false)
    var reconciliation: Reconciliation? = null,

    @field:Attribute(name = "DiagnosisResult", required = false)
    var diagnosisResult: String? = null,

    @field:Element(name = "OriginalHeader", required = false)
    var originalHeader: OriginalHeader? = null,

    @field:Element(name = "PrivateData", required = false)
    var privateData: PrivateData? = null

) : BaseXMLEntity() {

    var merchantReceipt: String? = null
    var customerReceipt: String? = null
    var totalAmount: String? = null

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
                    this.requestType = it.requestType
                    this.applicationSender = it.applicationSender
                    this.workstationID = it.workstationID
                    this.requestID = it.requestID
                    this.operationResult = it.operationResult
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

        @field: Attribute(name = "TerminalID", required = true)
        var id: String? = null

    )

    @Root(name = "PrivateData")
    data class PrivateData(

        @field: Text
        @field:Path("RebootInfo")
        var rebootInfo: String? = null

    )

    @Root(name = "Authorisation")
    data class Authorisation(

        @field:Attribute(name = "AcquirerID", required = false)
        var acquirerID: String? = null,

        @field:Attribute(name = "TimeStamp", required = false)
        var timeStamp: String? = null,

        @field:Attribute(name = "ApprovalCode", required = false)
        var approvalCode: String? = null,

        @field:Attribute(name = "AcquirerBatch", required = false)
        var acquirerBatch: String? = null
    )

    @Root(name = "Reconciliation")
    data class Reconciliation(

        @field:Attribute(name = "TotalAmount", required = false)
        var totalAmount: TotalAmount? = null,

        @field:Attribute(name = "LanguageCode", required = false)
        var languageCode: String? = null
    )

    @Root(name = "TotalAmount")
    data class TotalAmount(

        @field: Attribute(name = "NumberPayments", required = true)
        var numberPayments: String? = null,

        @field: Attribute(name = "PaymentType", required = true)
        var paymentType: String? = null,

        @field: Attribute(name = "Currency", required = true)
        var currency: String? = null,

        @field: Attribute(name = "CardCircuit", required = true)
        var cardCircuit: String? = null,

        @field: Attribute(name = "Acquirer", required = true)
        var acquirer: String? = null,

        //not field
        var total: String? = null
    )

    @Root(name = "OriginalHeader")
    data class OriginalHeader(

        @field: Attribute(name = "RequestType", required = true)
        var requestType: String? = null,

        @field: Attribute(name = "ApplicationSender", required = true)
        var applicationSender: String? = null,

        @field: Attribute(name = "WorkstationID", required = true)
        var workstationID: String? = null,

        @field: Attribute(name = "RequestID", required = true)
        var requestID: String? = null,

        @field: Attribute(name = "OverallResult", required = true)
        var overallResult: String? = null
    )
}









