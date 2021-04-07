package com.gmail.maystruks08.opi_core.entity.response

import com.gmail.maystruks08.opi_core.entity.BaseXMLEntity
import com.gmail.maystruks08.opi_core.entity.OperationResult
import org.simpleframework.xml.*
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.Reader
import java.io.StringReader

@Root(name = "CardServiceResponse")
data class CardResponse(

    @field:Attribute(name = "RequestID", required = true)
    var requestID: String = "",

    @field:Attribute(name = "RequestType", required = true)
    var requestType: String = "",

    @field:Attribute(name = "ElmeErrorCode", required = false)
    var errorCode: String = "",

    @field:Attribute(name = "ElmeErrorText", required = false)
    var errorText: String = "",

    @field:Attribute(name = "OverallResult", required = true)
    var operationResult: OperationResult = OperationResult.Failure,

    @field:Attribute(name = "WorkstationID", required = false)
    var workstationID: String = "",

    @field:Path("PrivateData/HashData/PANHash")
    @field:Text(required = false)
    var panHash: String? = null,

    @field:Path("PrivateData/CardHolderAuthentication")
    @field:Text(required = false)
    var cardHolderAuthentication: String? = null,

    @field: Attribute(name = "TerminalID")
    @field:Path("Terminal")
    var terminalID: String = "",

    @field: Attribute(name = "STAN", required = false)
    @field:Path("Terminal")
    var stan: String = "",

    @field: Text
    @field:Path("PrivateData/RebootInfo")
    var terminalLastRebootTime: String = "",

    @field:Text
    @field:Path("Tender/TotalAmount")
    var totalAmount: String = "",

    @field:Path("Tender/TotalAmount")
    @field:Attribute(name = "Currency", required = false)
    var currency: String? = null,


    @field:Attribute(name = "ApprovalCode", required = false)
    @field:Path("Tender/Authorisation")
    var approvalCode: String? = null,

    @field:Attribute(name = "AcquirerID", required = false)
    @field:Path("Tender/Authorisation")
    var acquirerId: String? = null,

    @field:Attribute(name = "TimeStamp", required = false)
    @field:Path("Tender/Authorisation")
    var timestamp: String? = null,

    @field:Attribute(name = "ExpiryDate", required = false)
    @field:Path("Tender/Authorisation")
    var cardExpireDate: String? = null,

    @field:Attribute(name = "CaptureReference", required = false)
    @field:Path("Tender/Authorisation")
    var captureReference: String? = null,

    @field: Attribute(name = "ReceiptNumber", required = false)
    @field: Path("Tender/Authorisation")
    var receiptNumber: String? = null,

    @field:Attribute(name = "Merchant", required = false)
    @field:Path("Tender/Authorisation")
    var merchantId: String? = null,

    @field:Attribute(name = "CardPAN", required = false)
    @field:Path("Tender/Authorisation")
    var cardPAN: String? = null,

    @field:Attribute(name = "CardCircuit", required = false)
    @field:Path("Tender/Authorisation")
    var cardCircuit: String? = null,

    @field: Attribute(name = "AuthorisationType", required = false)
    @field:Path("Tender/Authorisation")
    var authorisationType: String? = null,

    @field:Attribute(name = "ActionCode", required = false)
    @field:Path("Tender/Authorisation")
    var actionCode: String? = null,

    @field:Attribute(name = "ReturnCode", required = false)
    @field:Path("Tender/Authorisation")
    var returnCode: String? = null,

    @field: Text
    @field:Path("CardValue/Track2")
    var track2: String? = null,

    @field: Element(name = "OriginalHeader", required = false)
    var originalHeader: OriginalHeader? = null

) : BaseXMLEntity() {

    var customerReceipt: String? = null
    var merchantReceipt: String? = null

    constructor(xmlString: String) : this() {
        deserializeFromXMLString(xmlString)
    }

    constructor(operationResult: OperationResult): this(){
        this.operationResult = operationResult
    }

    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        val format = Format("<?xml version=\"1.0\" encoding= \"ISO-8859-1\" ?>")
        val serializer = Persister(format)
        serializer.read(this::class.java, reader, false)?.also {
            this.requestID = it.requestID
            this.requestType = it.requestType
            this.workstationID = it.workstationID
            this.cardHolderAuthentication = it.cardHolderAuthentication
            this.panHash = it.panHash
            this.terminalID = it.terminalID
            this.stan = it.stan
            this.terminalLastRebootTime = it.terminalLastRebootTime
            this.totalAmount = it.totalAmount
            this.operationResult = it.operationResult
            this.errorCode = it.errorCode
            this.errorText = it.errorText
            this.currency = it.currency
            this.approvalCode = it.approvalCode
            this.acquirerId = it.acquirerId
            this.timestamp = it.timestamp
            this.cardExpireDate = it.cardExpireDate
            this.captureReference = it.captureReference
            this.receiptNumber = it.receiptNumber
            this.merchantId = it.merchantId
            this.cardPAN = it.cardPAN
            this.cardCircuit = it.cardCircuit
            this.authorisationType = it.authorisationType
            this.actionCode = it.actionCode
            this.returnCode = it.returnCode
            this.track2 = it.track2
            this.originalHeader = it.originalHeader
        }
    }

    @Root(name = "OriginalHeader")
    data class OriginalHeader(

        @field: Attribute(name = "POPID", required = false)
        var popid: String? = null,

        @field: Attribute(name = "RequestID", required = false)
        var requestID: String? = null,

        @field: Attribute(name = "RequestType", required = false)
        var requestType: String? = null,

        @field: Attribute(name = "OverallResult", required = false)
        var overallResult: String? = null,

        @field: Attribute(name = "WorkstationID", required = false)
        var workstationID: String? = null
    )
}

