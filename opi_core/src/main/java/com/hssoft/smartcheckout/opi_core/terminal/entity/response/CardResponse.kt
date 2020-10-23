package com.hssoft.smartcheckout.opi_core.terminal.entity.response

import com.hssoft.smartcheckout.opi_core.terminal.entity.BaseXMLEntity
import com.hssoft.smartcheckout.opi_core.terminal.entity.OperationResult
import org.simpleframework.xml.*
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.Reader
import java.io.StringReader

//<?xml version="1.0" encoding="ISO-8859-1" ?>
//<CardServiceResponse xmlns="http://www.nrf-arts.org/IXRetail/namespace" RequestID="10" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" RequestType="CardPayment" OverallResult="Failure" WorkstationID="Elo C1242435235" xsi:noNamespaceSchemaLocation="C:\Windows\OPISchema\CardResponse.xsd">
// <Terminal STAN="2427" TerminalID="48306007" />
// <PrivateData>
// <RebootInfo>2020-10-24T03:00:00</RebootInfo>
// </PrivateData>
//
//
//
// <Tender>
// <TotalAmount Currency="EUR" PaymentAmount="5.00">5.00</TotalAmount><Authorisation CardPAN="541333######0010" Merchant="455600000599   " TimeStamp="2020-10-23T11:13:00" AcquirerID="483" ActionCode="1709" ReturnCode="1709" CardCircuit="MasterCard" ApprovalCode="" ReceiptNumber="212" AuthorisationType="Online" /></Tender>
//
//
//
// <CardDetails>
// <ExpiryDate>12/25</ExpiryDate>
// </CardDetails>
//
// <CardValue>
// <CardCircuit>MasterCard</CardCircuit>
//<ExpiryDate>12/25</ExpiryDate
// ><Track1></Track1>
// <Track2>;541333######0010=25122010123409172?</Track2>
// <Track3></Track3>
// <CardPAN>541333######0010</CardPAN>
// </CardValue>
//
//
// </CardServiceResponse>

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

    @field: Attribute(name = "TerminalID", required = true)
    @field:Path("Terminal")
    var terminalID: String = "",

    @field: Text
    @field:Path("PrivateData/RebootInfo")
    var terminalLastRebootTime: String = "",

    @field:Text
    @field:Path("Tender/TotalAmount")
    var totalAmount: String = "",

    @field:Path("PrivateData/Tender/TotalAmount")
    @field:Element(name = "Currency", required = false)
    var currency: String? = null,

    @field:Attribute(name = "CardPAN", required = false)
    @field:Path("Tender/Authorisation")
    var tenderAuthorisationCardPan: String? = null,

    @field:Attribute(name = "Merchant", required = false)
    @field:Path("Tender/Authorisation")
    var merchantId: String? = null,

    @field:Attribute(name = "TimeStamp", required = false)
    @field:Path("Tender/Authorisation")
    var timestamp: String? = null,

    @field:Attribute(name = "AcquirerID", required = false)
    @field:Path("Tender/Authorisation")
    var acquirerId: String? = null,

    @field:Attribute(name = "ActionCode", required = false)
    @field:Path("Tender/Authorisation")
    var actionCode: String? = null,

    @field:Attribute(name = "ReturnCode", required = false)
    @field:Path("Tender/Authorisation")
    var returnCode: String? = null,

    @field:Attribute(name = "CardCircuit", required = false)
    @field:Path("Tender/Authorisation")
    var cardCircuit: String? = null,

    @field:Attribute(name = "ApprovalCode", required = false)
    @field:Path("Tender/Authorisation")
    var approvalCode: String? = null,

    @field: Attribute(name = "ReceiptNumber", required = false)
    @field: Path("Tender/Authorisation")
    var receiptNumber: String? = null,

    @field: Attribute(name = "AuthorisationType", required = false)
    @field:Path("Tender/Authorisation")
    var authorisationType: String? = null,

    @field: Text
    @field:Path("CardValue/Track2")
    var track2: String? = null,

    @field: Text
    @field:Path("CardDetails/ExpiryDate")
    var cardExpireDate: String? = null

) : BaseXMLEntity() {

    constructor(xmlString: String) : this() {
        deserializeFromXMLString(xmlString)
    }

    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        val format = Format("<?xml version=\"1.0\" encoding= \"ISO-8859-1\" ?>")
        val serializer = Persister(format)
        try {
            serializer.read(this::class.java, reader, false)?.also {
                this.requestID = it.requestID
                this.requestType = it.requestType
                this.workstationID = it.workstationID
                this.cardHolderAuthentication = it.cardHolderAuthentication
                this.panHash = it.panHash
                this.terminalID = it.terminalID
                this.terminalLastRebootTime = it.terminalLastRebootTime
                this.receiptNumber = it.receiptNumber
                this.totalAmount = it.totalAmount
                this.operationResult = it.operationResult
                this.errorCode = it.errorCode
                this.errorText = it.errorText
                this.currency = it.currency
                this.tenderAuthorisationCardPan = it.tenderAuthorisationCardPan
                this.timestamp = it.timestamp
                this.acquirerId = it.acquirerId
                this.merchantId = it.merchantId
                this.approvalCode = it.approvalCode
                this.authorisationType = it.authorisationType
                this.actionCode = it.actionCode
                this.returnCode = it.returnCode
                this.cardCircuit = it.cardCircuit
                this.track2 = it.track2
                this.cardExpireDate = it.cardExpireDate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


