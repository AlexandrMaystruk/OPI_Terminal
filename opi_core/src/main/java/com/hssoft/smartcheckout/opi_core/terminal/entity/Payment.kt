package com.hssoft.smartcheckout.opi_core.terminal.entity

import java.math.BigDecimal


enum class PaymentType { SALE }

data class Payment(
    val total: BigDecimal,
    val currency: String,
    val transactionId: String,
    val type: PaymentType,
    val lastReceiptNumber: Int
) {

    class Builder {
        private var total: BigDecimal? = null
        private var currency: String? = null
        private var transactionId: String? = null
        private var type: PaymentType? = null
        private var lastReceiptNumber: Int? = null

        fun total(total: BigDecimal) = apply { this.total = total }
        fun currency(currency: String) = apply { this.currency = currency }
        fun transactionId(transactionId: String) = apply { this.transactionId = transactionId }
        fun type(type: PaymentType) = apply { this.type = type }
        fun lastReceiptNumber(number: Int) = apply { this.lastReceiptNumber = number }

        fun build(): Payment {
            if (total == null || currency == null || transactionId == null) throw Exception() //TODO change exception type
            return Payment(
                total!!,
                currency!!,
                transactionId!!,
                type!!,
                lastReceiptNumber!!
            )
        }
    }
}