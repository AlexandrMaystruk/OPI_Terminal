package com.gmail.maystruks08.opiterminal.entity.request

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.math.BigDecimal
import java.util.*

@Root(name = "POSdata")
data class PosData constructor(

    @field:Element(name = "POSTimeStamp", required = false)
    var posTimeStamp: Date? = null,

    @field:Element(name = "ClerkID", required = false)
    var clerkId: String? = null,

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

@Root(name = "CardServiceRequest")
enum class ClerkPermission { Low, Medium, High }

@Root(name = "Currency")
enum class Currency { EUR, CHF }