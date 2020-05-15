package com.gmail.maystruks08.opiterminal.terminal

import java.math.BigDecimal


enum class PaymentType { SALE }

data class Payment(
     val total: BigDecimal,
     val currency: String,
     val transactionId: String,
     val type: PaymentType
) {

    class Builder {
        private var total: BigDecimal? = null
        private var currency: String? = null
        private var transactionId: String? = null
        private var type: PaymentType? = null

        fun total(total: BigDecimal) = apply { this.total = total }
        fun currency(currency: String) = apply { this.currency = currency }
        fun transactionId(transactionId: String) = apply { this.transactionId = transactionId }
        fun type(type: PaymentType) = apply { this.type = type }

        fun build(): Payment {
            if (total == null || currency == null || transactionId == null) throw Exception() //TODO change exception type
            return Payment(
                total!!,
                currency!!,
                transactionId!!,
                type!!
            )
        }
    }
}