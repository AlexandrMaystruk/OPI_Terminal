package com.gmail.maystruks08.opiterminal.entity

import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.StringWriter
import java.io.Writer

const val IGNORE_TAG = "IgnoreTag"

abstract class BaseXMLEntity {

    open fun serializeToXMLString(): String? {
        val writer: Writer = StringWriter()
        val format = Format("<?xml version=\"1.0\" encoding= \"ISO-8859-1\" ?>")
        val serializer = Persister(format)
        return try {
            serializer.write(this, writer)
            writer.toString().replace("<$IGNORE_TAG>", "").replace("</$IGNORE_TAG>", "")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    abstract fun deserializeFromXMLString(xmlString: String)
}