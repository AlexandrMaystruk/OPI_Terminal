package com.gmail.maystruks08.opiterminal.entity.request

import com.gmail.maystruks08.opiterminal.entity.BaseXMLEntity
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.Reader
import java.io.StringReader
import java.util.*


@Root(name = "CardServiceRequest")
data class CardRequest(

    @field:Element(name = "POSdata", required = false)
    var posData: PosData? = null,

    @field:ElementList(name = "item", inline = true, required = false)
    var privateDataField: List<String>? = null

) : BaseXMLEntity() {

    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        val format = Format("<?xml version=\"1.0\" encoding= \"ISO-8859-1\" ?>")
        val serializer = Persister(format)
        try {
            val cardServiceRequest = serializer.read(CardRequest::class.java, reader, false)
            this.posData = cardServiceRequest.posData
            this.privateDataField = cardServiceRequest.privateDataField
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

    @Root(name = "CardServiceRequest")
    enum class ClerkPermission { Low, Medium, High }

}

