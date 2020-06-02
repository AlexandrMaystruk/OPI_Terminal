package com.hssoft.smartcheckout.opi_core.terminal.entity

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.transform.Transform

@Root(name = "SimpleText")
data class SimpleText(
    @field: Element(required = false)
    var textMessage: String? = ""
)

class SimpleTextTransformer : Transform<SimpleText> {

    @Throws(Exception::class)
    override fun read(value: String): SimpleText {
        return SimpleText(value)
    }

    override fun write(value: SimpleText?): String {
        return value?.textMessage ?: ""
    }
}