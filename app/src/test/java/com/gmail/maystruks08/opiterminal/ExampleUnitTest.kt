package com.gmail.maystruks08.opiterminal

import com.gmail.maystruks08.opiterminal.entity.request.*
import org.junit.Test

import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val testEntity = ServiceRequest()
            .apply {
            this.workstationID = "SmartCheckout id124235"
            this.applicationSender = "SmartCheckout android"
            this.posData =
                PosData(
                    Date(),
                    "clerkId = 10",
                    true,
                    ClerkPermission.High,
                    "#00011122"
                )

            this.privateData =
                PrivateData(
                    PrepaidCard(
                        "",
                        false,
                        "String value"
                    ),
                    listOf("Text 1", "Text 2", "Cat")
                )

        }

        val xml = testEntity.serializeToXMLString()

        println(xml)
        println()


        val dematerialized =
            ServiceRequest()
        dematerialized.deserializeFromXMLString(xml!!)

        val xml2 = dematerialized.serializeToXMLString()

        print(xml2)

    }
}
