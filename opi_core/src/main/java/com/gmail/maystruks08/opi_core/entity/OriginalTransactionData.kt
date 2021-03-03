package com.gmail.maystruks08.opi_core.entity

import java.math.BigDecimal


data class OriginalTransactionData(
        val terminalID: String,
        val stan: String,
        val timeStamp: String,
        val total: BigDecimal,
        val currency: String
) {

    class Builder {

        private var terminalID: String = ""
        private var stan: String = ""
        private var timeStamp: String = ""
        private var total: BigDecimal = BigDecimal.ZERO
        private var currency: String = ""

        fun terminalID(terminalID: String) = apply { this.terminalID = terminalID }
        fun stan(stan: String) = apply { this.stan = stan }
        fun timeStamp(timeStamp: String) = apply { this.timeStamp = timeStamp }
        fun total(total: BigDecimal) = apply { this.total = total }
        fun currency(currency: String) = apply { this.currency = currency }

        fun build() = OriginalTransactionData(
                terminalID,
                stan,
                timeStamp,
                total,
                currency
        )
    }
}