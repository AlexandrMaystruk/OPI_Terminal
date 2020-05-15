package com.gmail.maystruks08.opiterminal.entity.request

import com.gmail.maystruks08.opiterminal.entity.BaseXMLEntity
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import java.io.Reader
import java.io.StringReader


@Root(name = "CardServiceRequest")
data class CardRequest(

    @field:Element(name = "POSdata", required = false)
    var posData: PosData? = null,

    @field:ElementList(name = "item", inline = true, required = false)
    var privateDataField: List<String>? = null

) : BaseXMLEntity() {

    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        val serializer = Persister()
        try {
            val cardServiceRequest = serializer.read(CardRequest::class.java, reader, false)
            this.posData = cardServiceRequest.posData
            this.privateDataField = cardServiceRequest.privateDataField
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
