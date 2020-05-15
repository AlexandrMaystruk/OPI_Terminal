package com.gmail.maystruks08.opiterminal.entity

import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.io.StringWriter
import java.io.Writer

abstract class BaseXMLEntity {

    open fun serializeToXMLString(): String?{
        val writer: Writer = StringWriter()
        val serializer: Serializer = Persister()
        return try {
            serializer.write(this, writer)
            writer.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    abstract fun deserializeFromXMLString(xmlString: String)
}