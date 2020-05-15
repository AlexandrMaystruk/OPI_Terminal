package com.gmail.maystruks08.opiterminal.entity.request

import com.gmail.maystruks08.opiterminal.entity.BaseXMLEntity
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import java.io.Reader
import java.io.StringReader


@Root(name = "ServiceRequest")
data class ServiceRequest(

    @field:Element(name = "POSdata", required = false)
    var posData: PosData? = null,

    @field:Element(name = "TotalAmount", required = false)
    var totalAmount: TotalAmount? = null,

    @field:Element(name = "Agent", required = false)
    var agent: String? = null,

    @field:Element(name = "AgentSpecified", required = false)
    var agentSpecified: Boolean? = null,

    @field:Element(name = "PrivateData", required = false)
    var privateData: PrivateData? = null,

    @field:Element(name = "RequestType", required = false)
    var requestType: String? = null,

    @field:Element(name = "ApplicationSender", required = false)
    var applicationSender: String? = null,

    @field:Element(name = "WorkstationID", required = false)
    var workstationID: String? = null,

    @field:Element(name = "POPID", required = false)
    var popID: String? = null,

    @field:Element(name = "RequestID", required = false)
    var requestID: String? = null

) : BaseXMLEntity() {

    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        val serializer = Persister()
        try {
            serializer.read(this::class.java, reader, false)
                ?.also {
                    this.posData = it.posData
                    this.totalAmount = it.totalAmount
                    this.agent = it.agent
                    this.agentSpecified = it.agentSpecified
                    this.privateData = it.privateData
                    this.requestType = it.requestType
                    this.applicationSender = it.applicationSender
                    this.workstationID = it.workstationID
                    this.popID = it.popID
                    this.requestID = it.requestID
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
