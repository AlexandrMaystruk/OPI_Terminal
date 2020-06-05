package com.hssoft.smartcheckout.opi_core.terminal.entity

import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer

class SerializeToXmlException(message: String) : Exception(message)

val Exception.stackTraceString: String
    get() {
        val stringWriter = StringWriter()
        this.printStackTrace(PrintWriter(stringWriter))
        return stringWriter.toString()
    }

abstract class BaseXMLEntity {

    private val writer: Writer = StringWriter()
    private val format = Format("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
    val serializer = Persister(format)

    open fun serializeToXMLString(): String {

        return try {
            serializer.write(this, writer)
            val result = writer.toString()
                .replace("\n", "")
                .replace("\\s+".toRegex(), " ")
                .replace("&quot;".toRegex(), "\"")
                .replace("&gt;".toRegex(), ">")
                .replace("<SimpleText>".toRegex(), "")
                .replace("</SimpleText>".toRegex(), "")
            result
                .replace("> ".toRegex(), ">")
                .replace(" </".toRegex(), "</")
                .trim()
        } catch (e: Exception) {
            throw SerializeToXmlException(e.stackTraceString)
        }
    }

    abstract fun deserializeFromXMLString(xmlString: String)
}
