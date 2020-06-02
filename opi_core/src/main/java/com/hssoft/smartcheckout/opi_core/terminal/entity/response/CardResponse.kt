package com.hssoft.smartcheckout.opi_core.terminal.entity.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "CardServiceResponse")
data class CardResponse(

    @field: Element(name = "Terminal", required = true)
    var terminal: Terminal? = null,

    @field: Element(name = "Tender", required = true)
    var tender: Tender? = null,

    @field: Element(name = "Loyalty", required = true)
    var loyalty: Loyalty? = null,

    @field: Element(name = "SaleItem", required = true)
    var saleItem: SaleItem? = null,

    @field: Element(name = "OriginalHeader", required = true)
    var originalHeader: OriginalHeader? = null,

    @field: Element(name = "CardValue", required = true)
    var cardValue: CardValue? = null,

    @field: Element(name = "PrivateData", required = true)
    var Data: PrivateData? = null,

    @field: Element(name = "RequestType", required = true)
    var RequestType: String? = null,

    @field: Element(name = "ApplicationSender", required = true)
    var ApplicationSender: String? = null,

    @field: Element(name = "WorkstationID", required = true)
    var WorkstationID: String? = null,

    @field: Element(name = "POPID", required = true)
    var popID: String? = null,

    @field: Element(name = "RequestID", required = true)
    var RequestID: String? = null,

    @field: Element(name = "OverallResult", required = true)
    var OverallResult: String? = null,

    @field: Element(name = "prefix", required = true) ///??????????
    var prefix: String? = null
) {

    @Root(name = "PrivateData")
    data class PrivateData(
        @field: Element(name = "AdditionalHostData", required = true)
        var additionalHostData: AdditionalHostData? = null,

        @field: Element(name = "prefix", required = true) ///???????
        var prefix: String? = null,

        @field: Element(name = "text", required = true)
        var text: String? = null
    )

    @Root(name = "Authorisation")
    data class Authorisation(

        @field: Element(name = "AcquirerID", required = true)
        var AcquirerID: String? = null,

        @field: Element(name = "CardPAN", required = true)
        var CardPAN: String? = null,

        @field: Element(name = "StartDate", required = true)
        var StartDate: String? = null,

        @field: Element(name = "ExpiryDate", required = true)
        var ExpiryDate: String? = null,

        @field: Element(name = "TimeStamp", required = true)
        var TimeStamp: String? = null,

        @field: Element(name = "ActionCode", required = true)
        var ActionCode: String? = null,

        @field: Element(name = "ApprovalCode", required = true)
        var ApprovalCode: String? = null,

        @field: Element(name = "AcquirerBatch", required = true)
        var AcquirerBatch: String? = null,

        @field: Element(name = "CardCircuit", required = true)
        var CardCircuit: String? = null,

        @field: Element(name = "FiscalReceipt", required = true)
        var FiscalReceipt: String? = null,

        @field: Element(name = "PANprint", required = true)
        var PANprint: String? = null,

        @field: Element(name = "TimeDisplay", required = true)
        var TimeDisplay: String? = null,

        @field: Element(name = "LoyaltyAllowed", required = true)
        var LoyaltyAllowed: String? = null,

        @field: Element(name = "ReceiptCopies", required = true)
        var ReceiptCopies: String? = null,

        @field: Element(name = "Merchant", required = true)
        var Merchant: String? = null,

        @field: Element(name = "AuthorisationType", required = true)
        var AuthorisationType: String? = null,

        @field: Element(name = "ReceiptNumber", required = true)
        var ReceiptNumber: String? = null,

        @field: Element(name = "CaptureReference", required = true)
        var CaptureReference: String? = null,

        @field: Element(name = "text", required = true)
        var prefix: String? = null
    )


    @Root(name = "Tender")
    data class Tender(

        @field: Element(name = "TotalAmount", required = true)
        var totalAmount: TotalAmount? = null,

        @field: Element(name = "Authorisation", required = true)
        var authorisation: Authorisation? = null,

        @field: Element(name = "RestrictionCodes", required = true)
        var restrictionCodes: RestrictionCodes? = null,

        @field: Element(name = "LanguageCode", required = true)
        var LanguageCode: String? = null
    )


    @Root(name = "TotalAmount")
    data class TotalAmount(

        @field: Element(name = "PaymentAmount", required = true)
        var PaymentAmount: String? = null,

        @field: Element(name = "CashBackAmount", required = true)
        var CashBackAmount: String? = null,

        @field: Element(name = "OriginalAmount", required = true)
        var OriginalAmount: String? = null,

        @field: Element(name = "Currency", required = true)
        var Currency: String? = null
    )

    @Root(name = "Terminal")
    data class Terminal(

        @field: Element(name = "TerminalID", required = true)
        var TerminalID: String? = null,

        @field: Element(name = "TerminalBatch", required = true)
        var TerminalBatch: String? = null,

        @field: Element(name = "STAN", required = true)
        var STAN: String? = null,

        @field: Element(name = "ApplicationID", required = true)
        var ApplicationID: String? = null
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

    @Root(name = "Loyalty")
    data class Loyalty(

        @field: Element(name = "text", required = true)
        var LoyaltyFlag: String? = null,

        @field: Element(name = "text", required = true)
        var LoyaltyTimeStamp: String? = null,


        @field: Element(name = "LoyaltyCard", required = true)
        var loyaltyCard: LoyaltyCard? = null,

        @field: Element(name = "LoyaltyAmount", required = true)
        var loyaltyAmount: LoyaltyAmount? = null,

        @field: Element(name = "LoyaltyApprovalCode", required = true)
        var loyaltyApprovalCode: LoyaltyApprovalCode? = null
    )

    @Root(name = "LoyaltyCard")
    data class LoyaltyCard(

        @field: Element(name = "Byte", required = true)
        var byte: Byte? = null,

        @field: Element(name = "Ascii", required = true)
        var ascii: String? = null,

        @field: Element(name = "LoyaltyPAN", required = true)
        var LoyaltyPAN: String? = null
    )

    @Root(name = "LoyaltyAmount")
    data class LoyaltyAmount(

        @field: Element(name = "OriginalLoyaltyAmount", required = true)
        var OriginalLoyaltyAmount: String? = null,

        var text: String? = null
    )

    @Root(name = "LoyaltyApprovalCode")
    data class LoyaltyApprovalCode(

        @field: Element(name = "LoyaltyAcquirerID", required = true)
        var LoyaltyAcquirerID: String? = null,

        @field: Element(name = "LoyaltyAcquirerBatch", required = true)
        var LoyaltyAcquirerBatch: String? = null,

        var text: String? = null
    )

    @Root(name = "SaleItem")
    data class SaleItem(

        @field: Element(name = "ProductCode", required = true)
        var productCode: ProductCode? = null,

        @field: Element(name = "Amount", required = true)
        var amount: Amount? = null,

        @field: Element(name = "UnitMeasure", required = true)
        var unitMeasure: UnitMeasure? = null,

        @field: Element(name = "UnitPrice", required = true)
        var unitPrice: UnitPrice? = null,

        @field: Element(name = "Quantity", required = true)
        var quantity: Quantity? = null,

        @field: Element(name = "TaxCode", required = true)
        var taxCode: TaxCode? = null,

        @field: Element(name = "TypeMovement", required = true)
        var typeMovement: TypeMovement? = null,

        @field: Element(name = "SaleChannel", required = true)
        var saleChannel: SaleChannel? = null,

        @field: Element(name = "ItemID", required = true)
        var ItemID: String? = null,

        @field: Element(name = "LoyaltyAcquirerBatch", required = true)
        var prefix: String? = null
    )


    @Root(name = "CardValue")
    data class CardValue(
        var barcode: Barcode? = null,
        var inString: InString? = null,
        var cardPAN: CardPAN? = null,
        var startDate: StartDate? = null,
        var expiryDate: ExpiryDate? = null,
        var cardCircuit: CardCircuit? = null,
        var prefix: String? = null
    )


    @Root(name = "ProductCode")
    data class ProductCode(var code: String)

    @Root(name = "AdditionalHostData")
    data class AdditionalHostData(var text: String)

    @Root(name = "ExpiryDate")
    data class ExpiryDate(var date: String)

    @Root(name = "StartDate")
    data class StartDate(var date: String)

    @Root(name = "CardPAN")
    data class CardPAN(var pan: String)

    @Root(name = "InString")
    data class InString(var inString: String)

    @Root(name = "CardCircuit")
    data class CardCircuit(var cardCircuit: String)

    @Root(name = "SaleChannel")
    data class SaleChannel(var saleChannel: String)

    @Root(name = "Barcode")
    data class Barcode(val barcode: String)

    @Root(name = "TypeMovement")
    data class TypeMovement(var typeMovement: String)

    @Root(name = "TaxCode")
    data class TaxCode(var text: String)

    @Root(name = "Quantity")
    data class Quantity(var quantity: String)

    @Root(name = "Quantity")
    data class UnitPrice(var unitPrice: String)

    @Root(name = "UnitMeasure")
    data class UnitMeasure(var unitMeasure: String)

    @Root(name = "Amount")
    data class Amount(var amount: String)

    @Root(name = "Byte")
    data class Byte(var byte: String)

    @Root(name = "RestrictionCodes")
    data class RestrictionCodes(var restrictionCodes: String)
}




