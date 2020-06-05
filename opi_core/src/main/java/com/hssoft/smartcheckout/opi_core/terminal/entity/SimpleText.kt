package com.hssoft.smartcheckout.opi_core.terminal.entity

import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(name = "SimpleText")
data class SimpleText(

    @field: Text
    var textMessage: String? = ""

)