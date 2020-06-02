package com.gmail.maystruks08.opiterminal.entity

import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import org.simpleframework.xml.transform.RegistryMatcher
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

    open fun serializeToXMLString(): String {
        val writer: Writer = StringWriter()
        val format = Format("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
        val matcher = RegistryMatcher()
        matcher.bind(SimpleText::class.java, SimpleTextTransformer())
        val serializer = Persister(matcher, format)
        return try {
            serializer.write(this, writer)
            val result = writer.toString()
                .replace("\n", "")
                .replace("\\s+".toRegex(), " ")
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
