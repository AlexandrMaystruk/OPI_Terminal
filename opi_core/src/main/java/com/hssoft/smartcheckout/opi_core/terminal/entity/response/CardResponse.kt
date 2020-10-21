package com.hssoft.smartcheckout.opi_core.terminal.entity.response

import com.hssoft.smartcheckout.opi_core.terminal.entity.BaseXMLEntity
import com.hssoft.smartcheckout.opi_core.terminal.entity.Result
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
    var ElmeErrorCode: String = "",

    @field:Attribute(name = "ElmeErrorText", required = false)
    var ElmeErrorText: String = "",

    @field:Attribute(name = "OverallResult", required = true)
    var result: Result = Result.Failure,

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

    @field:Attribute(name = "TimeStamp", required = false)
    @field:Path("Tender/Authorisation")
    var timestamp: String? = null,

    @field:Attribute(name = "AcquirerID", required = false)
    @field:Path("Tender/Authorisation")
    var acquirerId: String? = null,

    @field:Attribute(name = "Merchant", required = false)
    @field:Path("Tender/Authorisation")
    var merchantId: String? = null,

    @field:Attribute(name = "ApprovalCode", required = false)
    @field:Path("Tender/Authorisation")
    var approvalCode: String? = null,

    @field: Attribute(name = "AuthorisationType", required = false)
    @field:Path("Tender/Authorisation")
    var authorisationType: String? = null,

    @field: Attribute(name = "ReceiptNumber", required = false)
    @field: Path("Tender/Authorisation")
    var receiptNumber: String? = null

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
                this.result = it.result
                this.ElmeErrorCode = it.ElmeErrorCode
                this.ElmeErrorText = it.ElmeErrorText
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


