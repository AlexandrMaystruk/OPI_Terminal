package com.gmail.maystruks08.opiterminal.entity.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.math.BigDecimal

@Root(name = "Output")
data class DeviceResponseOutput constructor(

    @field:Element(name = "OutDeviceTarget", required = false)
    val outDeviceTarget: String? = null,

    @field:Element(name = "OutResult", required = false)
    val outResult: String? = null
)

@Root(name = "Input")
class DeviceResponseInput(

    @field:Element(name = "SecureData", required = false)
    var secureDataField: List<Byte>? = null,

    @field:Element(name = "InputValue", required = false)
    var inputValueField: Boolean? = null,

    @field:Element(name = "InDeviceTarget", required = false)
    var inDeviceTargetField: String? = null,

    @field:Element(name = "InResult", required = false)
    var inResultField: String? = null

)

@Root(name = "EventResult")
data class DeviceResponseEventResult(

    @field:ElementList(name = "Dispenser", inline = true)
    private var dispenser: List<Byte>? = null,

    @field:ElementList(name = "Text", inline = true)
    private var productCodeField: List<String>? = null,

    @field:Element(name = "ModifiedRequest", required = false)
    var modifiedRequest: BigDecimal? = null,

    @field:ElementList(name = "SaleItem", inline = true)
    private var saleItems: List<SaleItem>? = null

)

@Root(name = "SaleItem")
data class SaleItem(

    @field:Element(name = "ProductCode")
    var productCodeField: Int? = null,

    @field:Element(name = "Amount")
    var amount: BigDecimal? = null,

    @field:Element(name = "UnitPrice")
    var unitPrice: BigDecimal? = null,

    @field:Element(name = "Quantity")
    var quantity: Float? = null,

    @field:Element(name = "TaxCode")
    var taxCode: String? = null,

    @field:Element(name = "ItemID")
    var itemId: String? = null

)